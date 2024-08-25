package com.cstav.genshinstrument.sound.held;

import com.cstav.genshinstrument.client.util.ClientUtil;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSound.Phase;
import com.cstav.genshinstrument.util.CommonUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class HeldNoteSoundInstance extends AbstractTickableSoundInstance {
    public final HeldNoteSound heldSoundContainer;
    public final HeldNoteSound.Phase phase;

    public final ResourceLocation instrumentId;

    public final Optional<Entity> initiator;
    public final InitiatorID initiatorId;

    /**
     * The origin of the sound. May be empty
     * for the initiator's position.
     */
    public final Optional<BlockPos> soundOrigin;
    public final int notePitch;

    private boolean released;

    /**
     * @param initiator The initiator of the sound. Empty for a non-player initiator.
     *                  Value must be present if {@code soundOrigin} is empty.
     * @param soundOrigin The block position of where the sound was originated from.
     *                    Value must be present if {@code initiator} is empty.
     */
    protected HeldNoteSoundInstance(HeldNoteSound heldSoundContainer, HeldNoteSound.Phase phase,
                                    int notePitch, float volume,
                                    @Nullable Entity initiator, @Nullable BlockPos soundOrigin,
                                    InitiatorID initiatorId, ResourceLocation instrumentId,
                                    int timeAlive, boolean released) {
        super(
            heldSoundContainer.getSound(phase).getByPreference(distFromSourceSqr(soundOrigin, initiator)),
            NoteSound.INSTRUMENT_SOUND_SOURCE
        );

        this.initiatorId = initiatorId;
        this.instrumentId = instrumentId;

        this.heldSoundContainer = heldSoundContainer;
        this.phase = phase;
        this.overallTimeAlive = timeAlive;

        this.initiator = Optional.ofNullable(initiator);
        this.soundOrigin = Optional.ofNullable(soundOrigin);

        this.volume = volume;
        this.notePitch = notePitch;
        this.pitch = NoteSound.getPitchByNoteOffset(notePitch);

        this.released = released;


        if (distFromSourceSqr() < Mth.square(NoteSound.LOCAL_RANGE)) {
            // Very close; play relative
            attenuation = Attenuation.NONE;
            relative = true;
            x = y = z = 0;
        } else {
            // Not close; play local
            attenuation = Attenuation.LINEAR;
            relative = false;

            this.soundOrigin.ifPresentOrElse(
                (loc) -> {
                    x = loc.getX();
                    y = loc.getY();
                    z = loc.getZ();
                },
                this::toInitiatorPos
            );
        }
    }

    /**
     * A held note sound instance for 3rd party trigger
     * @param initiator The initiator of the sound. Empty for a non-player initiator.
     *                  Value must be present if {@code soundOrigin} is empty.
     * @param soundOrigin The block position of where the sound was originated from.
     *                    Value must be present if {@code initiator} is empty.
     */
    public HeldNoteSoundInstance(HeldNoteSound heldSoundContainer, HeldNoteSound.Phase phase,
                                 int notePitch, float volume,
                                 @Nullable Entity initiator, @Nullable BlockPos soundOrigin,
                                 InitiatorID initiatorId, ResourceLocation instrumentId) {
        this(
            heldSoundContainer,
            phase, notePitch, volume,
            initiator, soundOrigin, initiatorId, instrumentId,
            0, false
        );
    }


    public void queueAndAddInstance() {
        Minecraft.getInstance().getSoundManager().queueTickingSound(this);
        ClientUtil.stopMusicIfClose(
            soundOrigin.orElseGet(initiator.map(Entity::blockPosition)::get)
        );
        addSoundInstance();
    }

    /**
     * Adds a new held sound to the cached held sounds.
     * Its identifier will either be the initiator's UUID
     * or the block position string.
     */
    public void addSoundInstance() {
        HeldNoteSounds.put(initiatorId, heldSoundContainer, notePitch, this);
    }
    protected void removeSoundInstance() {
        HeldNoteSounds.release(initiatorId, heldSoundContainer, notePitch, this);
    }

    /**
     * Marks this held sound as being released
     */
    public void setReleased() {
        if (released)
            return;

        this.released = true;

        // Play release sound, if applicable.
        // Only a 'hold' sound type may play a release.
        if (phase == Phase.HOLD) {

            if (heldSoundContainer.release() != null) {
                final Vec3 pos = getSourcePos();

                heldSoundContainer.release().playLocally(
                    pitch, volume,
                    new BlockPos((int) pos.x, (int) pos.y, (int) pos.z)
                );
            }

        }
    }
    public boolean isReleased() {
        return released;
    }


    protected static double distFromSourceSqr(@Nullable BlockPos soundOrigin, @Nullable Entity initiator) {
        return Minecraft.getInstance().player.position().distanceToSqr(getSourcePos(soundOrigin, initiator));
    }
    public double distFromSourceSqr() {
        return Minecraft.getInstance().player.position().distanceToSqr(getSourcePos());
    }

    protected static Vec3 getSourcePos(@Nullable BlockPos soundOrigin, @Nullable Entity initiator) {
        return (soundOrigin == null) ? initiator.position() : CommonUtil.getCenter(soundOrigin);
    }
    protected Vec3 getSourcePos() {
        return getSourcePos(soundOrigin.orElse(null), initiator.orElse(null));
    }


    protected int timeAlive = 0, overallTimeAlive;
    @Override
    public void tick() {
        toInitiatorPos();

        handleChainHolding();

        if (released) {
            float fadeOutMultiplier = 1;
            float fhft = heldSoundContainer.fullHoldFadeoutTime() * 20;

            // Lesser the significance of hold in the first FULL_HOLD_FADE_OUT_TIME ticks
            // Basically fade in the fade out
            if ((phase == Phase.HOLD) && (fhft != 0)) {
                if (overallTimeAlive < fhft) {
                    fadeOutMultiplier = 1 / ((overallTimeAlive + 1) / fhft);
                }
            }

            volume -= heldSoundContainer.releaseFadeOut() * fadeOutMultiplier;
            if (volume <= 0)
                stopHeld();
        }

        timeAlive++;
        overallTimeAlive++;
    }

    protected boolean chainedHolding = false;
    protected void handleChainHolding() {
        if (chainedHolding || (pitch == 0)) // if, for some reason, ig
            return;

        switch (phase) {
            case ATTACK: {
                // Attack wants to chain the first hold:
                if (timeAlive == (int)(heldSoundContainer.holdDelay() * 20)) {
                    queueHoldPhase(false);
                    chainedHolding = true;
                }
                break;
            }
            case HOLD: {
                // Hold wants to chain the next hold:
                if ((timeAlive * pitch) >= (int)((heldSoundContainer.holdDuration() + heldSoundContainer.chainedHoldDelay()) * 20)) {
                    queueHoldPhase(heldSoundContainer.decay() > 0);
                    chainedHolding = true;

                    // We now don't need to cache it anymore.
                    removeSoundInstance();
                }
                break;
            }
        }
    }

    protected void queueHoldPhase(final boolean decreaseVol) {
        if (volume <= .2f)
            return;

        new HeldNoteSoundInstance(
            heldSoundContainer, Phase.HOLD, notePitch, volume - (decreaseVol ? heldSoundContainer.decay() : 0),
            initiator.orElse(null), soundOrigin.orElse(null),
            initiatorId, instrumentId,
            overallTimeAlive, released
        ).queueAndAddInstance();
    }

    protected void toInitiatorPos() {
        if (relative)
            return;
        if (soundOrigin.isPresent() || initiator.isEmpty())
            return;
        // "Blown air" at the same location
        if (released)
            return;

        x = initiator.get().getX();
        y = initiator.get().getY();
        z = initiator.get().getZ();
    }

    // We don't want to randomly distort this stuff unlike the parent
    @Override
    public float getVolume() {
        return volume;
    }
    @Override
    public float getPitch() {
        return pitch;
    }

    // For some reason 'stop' is final...
    public void stopHeld() {
        stop();
        removeSoundInstance();
    }
}
