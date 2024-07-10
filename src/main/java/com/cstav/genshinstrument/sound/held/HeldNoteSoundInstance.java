package com.cstav.genshinstrument.sound.held;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSound.Phase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class HeldNoteSoundInstance extends AbstractTickableSoundInstance {
    private static final Map<HeldNoteSoundKey, List<HeldNoteSoundInstance>> SOUND_INSTANCES = new HashMap<>();

    public final HeldNoteSound heldSoundContainer;
    public final Player player;
    public final HeldNoteSound.Phase phase;
    // This is so we can cache this to the key
    protected int notePitch;

    protected HeldNoteSoundInstance(HeldNoteSound heldSoundContainer, HeldNoteSound.Phase phase,
                                    int notePitch, float volume,
                                    Player player, double distFromPlayer, int timeAlive) {
        super(
            heldSoundContainer.getSound(phase).getByPreference(distFromPlayer),
            NoteSound.INSTRUMENT_SOUND_SOURCE,
            SoundInstance.createUnseededRandom()
        );

        this.heldSoundContainer = heldSoundContainer;
        this.phase = phase;
        this.overallTimeAlive = timeAlive;

        this.player = player;
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
                                 Player player, double distFromPlayer) {
        this(heldSoundContainer, phase, notePitch, volume, player, distFromPlayer, 0);
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
        SOUND_INSTANCES
            .computeIfAbsent(
                new HeldNoteSoundKey(player, heldSoundContainer.baseSoundLocation(), heldSoundContainer.index(), notePitch),
                (_s) -> new ArrayList<>()
            )
            .add(this);
    }

    public static void triggerRelease(final HeldNoteSoundKey key) {
        if (!SOUND_INSTANCES.containsKey(key))
            return;

        SOUND_INSTANCES.get(key).forEach(HeldNoteSoundInstance::triggerRelease);
        SOUND_INSTANCES.remove(key);
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
            float fhft = heldSoundContainer.fullHoldFadeoutTime();

            // Lesser the significance of hold in the first FULL_HOLD_FADE_OUT_TIME ticks
            // Basically fade in the fade out
            if ((phase == Phase.HOLD) && (fhft != 0)) {
                if (overallTimeAlive < fhft) {
                    fadeOutMultiplier = 1 / (((overallTimeAlive + 1) / fhft));
                }
            }

            volume -= heldSoundContainer.releaseFadeOut() * fadeOutMultiplier;
        }

        timeAlive++;
        overallTimeAlive++;
    }

    public void triggerRelease() {
        released = true;
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
            player, player.position().distanceTo(Minecraft.getInstance().player.position()),
            overallTimeAlive
        ).queueAndAddInstance();
    }

    protected void updatePlayerPos() {
        x = player.getX();
        y = player.getY();
        z = player.getZ();
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
