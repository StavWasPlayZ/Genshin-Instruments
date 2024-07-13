package com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.IHeldInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSound;
import com.cstav.genshinstrument.sound.held.cached.HeldNoteSoundKey;
import com.cstav.genshinstrument.sound.held.cached.HeldNoteSounds;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public interface IHoldableNoteButton {
    public boolean isHeld();
    void setHeld(final boolean held);

    public HeldNoteSound getHeldNoteSound();
    public void setHeldNoteSound(final HeldNoteSound sound);

    /**
     * Releases the active note
     * @param notePitch The note pitch to target
     * @param targetPitch Should only release the provided pitch?
     * @param heldSound The sound being released
     */
    default void releaseHeld(int notePitch, boolean targetPitch, HeldNoteSound heldSound) {
        final HeldNoteSoundKey soundKey = heldSound.getKey(Minecraft.getInstance().player);

        if (targetPitch) {
            HeldNoteSounds.release(soundKey, notePitch);
        } else {
            HeldNoteSounds.release(soundKey);
        }

        // If this is the last note playing; release it.
        // If we target everyone then ofc it will be empty.
        if (!targetPitch || !HeldNoteSounds.hasInstances(soundKey)) {
            setHeld(false);
            ((HeldNoteButtonRenderer) asNoteBtn().getRenderer()).playRelease();
        }
    }
    /**
     * Releases the active note
     * @param notePitch The note pitch to target
     * @param targetPitch Should only release the provided pitch?
     */
    default void releaseHeld(int notePitch, boolean targetPitch) {
        releaseHeld(notePitch, targetPitch, getHeldNoteSound());
    }
    /**
     * Releases all notes of the matching sound type
     * @param targetPitch Should only release the active pitch?
     */
    default void releaseHeld(boolean targetPitch) {
        releaseHeld(asNoteBtn().getPitch(), targetPitch);
    }


    default void playLocalHeldSound(final NoteSound sound, final int pitch) {
        // The requested note sound SHOULD be in here,
        // provided the note button kept its original sounds as
        // the attack phase.
        final HeldNoteSound heldNoteSound = toHeldSound(sound);
        heldNoteSound.startPlaying(pitch, asNoteBtn().instrumentScreen.volume(), Minecraft.getInstance().player);
    }


    /**
     * @return The first-matching note sound
     * of the provided held sound array.
     */
    default HeldNoteSound toHeldSound(NoteSound noteSound) {
        return Arrays.stream(heldInstrumentScreen().getHeldNoteSounds())
            .filter((heldSound) -> heldSound.attack().equals(noteSound))
            .findFirst().orElseThrow();
    }


    default NoteButton asNoteBtn() {
        return (NoteButton) this;
    }
    default IHeldInstrumentScreen heldInstrumentScreen() {
        return (IHeldInstrumentScreen) asNoteBtn().instrumentScreen;
    }
}
