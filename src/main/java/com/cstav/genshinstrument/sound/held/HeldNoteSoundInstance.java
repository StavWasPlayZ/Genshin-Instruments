package com.cstav.genshinstrument.sound.held;

import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSound.Phase;
import com.cstav.genshinstrument.sound.held.cached.HeldNoteSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeldNoteSoundInstance extends AbstractTickableSoundInstance {
    public final HeldNoteSound heldSoundContainer;
    public final Player initiator;
    public final HeldNoteSound.Phase phase;
    // This is so we can cache this to the key
    protected int notePitch;

    protected HeldNoteSoundInstance(HeldNoteSound heldSoundContainer, HeldNoteSound.Phase phase,
                                    int notePitch, float volume,
                                    Player initiator, double distFromPlayer, int timeAlive) {
        super(
            heldSoundContainer.getSound(phase).getByPreference(distFromPlayer),
            NoteSound.INSTRUMENT_SOUND_SOURCE,
            SoundInstance.createUnseededRandom()
        );

        this.heldSoundContainer = heldSoundContainer;
        this.phase = phase;
        this.overallTimeAlive = timeAlive;

        this.initiator = initiator;
        updatePlayerPos();

        this.volume = volume;
        this.notePitch = notePitch;
        this.pitch = NoteSound.getPitchByNoteOffset(notePitch);
        attenuation = Attenuation.NONE;
    }

    /**
     * A held note sound instance for 3rd party trigger
     */
    public HeldNoteSoundInstance(HeldNoteSound heldSoundContainer, HeldNoteSound.Phase phase,
                                 int notePitch, float volume,
                                 Player initiator, double distFromPlayer) {
        this(heldSoundContainer, phase, notePitch, volume, initiator, distFromPlayer, 0);
    }
    /**
     * A held note sound instance for local playing
     */
    public HeldNoteSoundInstance(HeldNoteSound heldSoundContainer, HeldNoteSound.Phase phase,
                                 int notePitch, float volume) {
        this(heldSoundContainer, phase, notePitch, volume, Minecraft.getInstance().player, 0);
    }


    public void queueAndAddInstance() {
        Minecraft.getInstance().getSoundManager().queueTickingSound(this);
        addSoundInstance();
    }
    public void addSoundInstance() {
        HeldNoteSounds.put(heldSoundContainer.getKey(initiator), notePitch, this);
    }

    /**
     * Marks this held sound as being released
     */
    public void setReleased() {
        this.released = true;
    }

    private boolean released = false;
    protected int timeAlive = 0, overallTimeAlive;
    @Override
    public void tick() {
        updatePlayerPos();

        if (!released) {
            handleHolding();
        } else {
            float fadeOutMultiplier = 1;
            float fhft = heldSoundContainer.fullHoldFadeoutTime() * 20;

            // Lesser the significance of hold in the first FULL_HOLD_FADE_OUT_TIME ticks
            // Basically fade in the fade out
            if ((phase == Phase.HOLD) && (fhft != 0)) {
                if (overallTimeAlive < fhft) {
                    fadeOutMultiplier = 1 / (((overallTimeAlive + 1) / fhft));
                }
            }

            volume -= heldSoundContainer.releaseFadeOut() * fadeOutMultiplier;
            if (volume <= 0)
                stop();
        }

        timeAlive++;
        overallTimeAlive++;
    }


    protected void handleHolding() {
        switch (phase) {
            case ATTACK: {
                // Attack wants to chain the first hold:
                if (timeAlive == (int)(heldSoundContainer.holdDelay() * 20))
                    queueHoldPhase(false);
                break;
            }
            case HOLD: {
                // Hold wants to chain the next hold:
                if (timeAlive == (int)((heldSoundContainer.holdDuration() + heldSoundContainer.chainedHoldDelay()) * 20))
                    queueHoldPhase(heldSoundContainer.decay() > 0);
                break;
            }
        }
    }

    protected void queueHoldPhase(final boolean decreaseVol) {
        if (volume <= .2f)
            return;

        new HeldNoteSoundInstance(
            heldSoundContainer, Phase.HOLD, notePitch, volume - (decreaseVol ? heldSoundContainer.decay() : 0),
            initiator, initiator.position().distanceTo(Minecraft.getInstance().player.position()),
            overallTimeAlive
        ).queueAndAddInstance();
    }

    protected void updatePlayerPos() {
        x = initiator.getX();
        y = initiator.getY();
        z = initiator.getZ();
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
}
