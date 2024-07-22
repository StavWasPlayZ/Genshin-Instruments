package com.cstav.genshinstrument.sound.held;

import com.cstav.genshinstrument.client.util.ClientUtil;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSound.Phase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class HeldNoteSoundInstance extends AbstractTickableSoundInstance {
    public final HeldNoteSound heldSoundContainer;
    public final HeldNoteSound.Phase phase;

    public final Optional<Player> initiator;
    public final String initiatorId;

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
                                    @Nullable Player initiator, @Nullable BlockPos soundOrigin,
                                    int timeAlive, boolean released) {
        super(
            heldSoundContainer.getSound(phase).getByPreference(
                Minecraft.getInstance().player.position().distanceTo(
                    (soundOrigin == null) ? initiator.position() : soundOrigin.getCenter()
                )
            ),
            NoteSound.INSTRUMENT_SOUND_SOURCE,
            SoundInstance.createUnseededRandom()
        );


        initiatorId = HeldNoteSounds.getInitiatorId(
            (soundOrigin == null) ? initiator : soundOrigin
        );

        this.heldSoundContainer = heldSoundContainer;
        this.phase = phase;
        this.overallTimeAlive = timeAlive;

        this.initiator = Optional.ofNullable(initiator);
        this.soundOrigin = Optional.ofNullable(soundOrigin);
        updatePlayerPos();

        this.volume = volume;
        this.notePitch = notePitch;
        this.pitch = NoteSound.getPitchByNoteOffset(notePitch);
        attenuation = Attenuation.NONE;

        this.released = released;
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
                                 @Nullable Player initiator, @Nullable BlockPos soundOrigin) {
        this(heldSoundContainer, phase, notePitch, volume, initiator, soundOrigin, 0, false);
    }
    /**
     * A held note sound instance for local playing
     */
    public HeldNoteSoundInstance(HeldNoteSound heldSoundContainer, HeldNoteSound.Phase phase,
                                 int notePitch, float volume) {
        this(
            heldSoundContainer, phase,
            notePitch, volume,
            Minecraft.getInstance().player, null
        );
    }

    public void queueAndAddInstance() {
        Minecraft.getInstance().getSoundManager().queueTickingSound(this);
        ClientUtil.stopMusicIfClose(
            soundOrigin.orElseGet(initiator.map(LivingEntity::blockPosition)::get)
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
        this.released = true;
    }

    protected int timeAlive = 0, overallTimeAlive;
    @Override
    public void tick() {
        updatePlayerPos();

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
            overallTimeAlive, released
        ).queueAndAddInstance();
    }

    protected void updatePlayerPos() {
        if (soundOrigin.isPresent() || initiator.isEmpty())
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
