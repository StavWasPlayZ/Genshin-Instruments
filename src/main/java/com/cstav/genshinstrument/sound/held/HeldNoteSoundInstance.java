package com.cstav.genshinstrument.sound.held;

import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSound.Phase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeldNoteSoundInstance extends AbstractTickableSoundInstance {
    public final HeldNoteSound heldSoundContainer;
    public final Player player;
    public final HeldNoteSound.Phase phase;

    /**
     * A held note sound instance for 3rd party trigger
     */
    protected HeldNoteSoundInstance(HeldNoteSound heldSoundContainer, HeldNoteSound.Phase phase,
                                    final float pitch, final float volume,
                                    Player player, double distFromPlayer) {
        super(
            heldSoundContainer.getSound(phase).getByPreference(distFromPlayer),
            NoteSound.INSTRUMENT_SOUND_SOURCE,
            SoundInstance.createUnseededRandom()
        );

        this.heldSoundContainer = heldSoundContainer;
        this.phase = phase;

        this.player = player;
        updatePlayerPos();

        this.volume = volume;
        this.pitch = pitch;
        attenuation = Attenuation.NONE;
    }

    /**
     * A held note sound instance for local playing
     */
    protected HeldNoteSoundInstance(HeldNoteSound heldSoundContainer,
                                    final float pitch, final float volume, HeldNoteSound.Phase phase) {
        this(heldSoundContainer, phase, pitch, volume, Minecraft.getInstance().player, 0);
    }

    protected int timeAlive = 0;
    @Override
    public void tick() {
        timeAlive++;
        updatePlayerPos();

        switch (phase) {
            case ATTACK:
                // Attack wants to chain the first hold:
                if (timeAlive == heldSoundContainer.holdDelay())
                    queueHoldPhase();
                break;

            case HOLD:
                //TODO fade in & out

                // Hold wants to chain the next hold:
                if (timeAlive == heldSoundContainer.chainedHoldDelay())
                    queueHoldPhase();
                break;
        }
    }

    protected void queueHoldPhase() {
        Minecraft.getInstance().getSoundManager().queueTickingSound(new HeldNoteSoundInstance(
            heldSoundContainer, Phase.HOLD, pitch, volume,
            player, player.position().distanceTo(Minecraft.getInstance().player.position())
        ));
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
