package com.cstav.genshinstrument.sound.held;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held.IHoldableNoteButton;
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
    private IHoldableNoteButton noteButton;

    /**
     * A held note sound instance for 3rd party trigger
     */
    public HeldNoteSoundInstance(HeldNoteSound heldSoundContainer, HeldNoteSound.Phase phase,
                                    IHoldableNoteButton noteButton, float volume,
                                    Player player, double distFromPlayer) {
        super(
            heldSoundContainer.getSound(phase).getByPreference(distFromPlayer),
            NoteSound.INSTRUMENT_SOUND_SOURCE,
            SoundInstance.createUnseededRandom()
        );

        this.heldSoundContainer = heldSoundContainer;
        this.phase = phase;
        this.noteButton = noteButton;

        this.player = player;
        updatePlayerPos();

        this.volume = volume;
        this.pitch = NoteSound.getPitchByNoteOffset(noteButton().getPitch());
        attenuation = Attenuation.NONE;
    }

    public HeldNoteSoundInstance(HeldNoteSound heldSoundContainer, HeldNoteSound.Phase phase,
                                    IHoldableNoteButton noteButton,
                                    Player player, double distFromPlayer) {
        this(
            heldSoundContainer, phase,
            noteButton, ((NoteButton)noteButton).instrumentScreen.volume(),
            player, distFromPlayer
        );
    }
    /**
     * A held note sound instance for local playing
     */
    public HeldNoteSoundInstance(HeldNoteSound heldSoundContainer,
                                    IHoldableNoteButton noteButton, HeldNoteSound.Phase phase) {
        this(heldSoundContainer, phase, noteButton, Minecraft.getInstance().player, 0);
    }

    protected NoteButton noteButton() {
        return (NoteButton) noteButton;
    }


    private boolean fadingOut = false;

    protected int timeAlive = 0;
    @Override
    public void tick() {
        timeAlive++;
        updatePlayerPos();

        if (noteButton.isHeld())
            handleHolding();
        else
            triggerFadeout();

        if (fadingOut) {
            volume -= .05f;
        }
    }

    public void triggerFadeout() {
        fadingOut = true;
    }


    private void handleHolding() {
        switch (phase) {
            case ATTACK:
                // Attack wants to chain the first hold:
                if (timeAlive == (int)(heldSoundContainer.holdDelay() * 20 - 2))
                    queueHoldPhase(false);
                break;

            case HOLD:
                // Hold wants to chain the next hold:
                if (timeAlive == (int)((heldSoundContainer.holdDuration() + heldSoundContainer.chainedHoldDelay()) * 20 + 2.2f))
                    queueHoldPhase(true);
                break;
        }
    }

    protected void queueHoldPhase(final boolean decreaseVol) {
        if (volume <= .2f)
            return;

        Minecraft.getInstance().getSoundManager().queueTickingSound(new HeldNoteSoundInstance(
            heldSoundContainer, Phase.HOLD, noteButton, volume - (decreaseVol ? .05f : 0),
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
