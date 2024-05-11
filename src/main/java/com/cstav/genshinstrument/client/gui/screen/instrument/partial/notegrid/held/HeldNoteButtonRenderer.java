package com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.held;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButtonRenderer;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteRing;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.animation.HeldNoteAnimationController;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.animation.NoteAnimationController;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class HeldNoteButtonRenderer extends NoteButtonRenderer {
    public HeldNoteButtonRenderer(HeldGridNoteButton noteButton, Supplier<ResourceLocation> noteTextureProvider) {
        super(noteButton, noteTextureProvider);
    }

    private HeldGridNoteButton getBtn() {
        return (HeldGridNoteButton) noteButton;
    }

    @Override
    protected NoteAnimationController initNoteAnimation() {
        // Divide by 2 because we are doing the start-end animation for each
        // Unlike NoteAnimationController which does them at once
        return new HeldNoteAnimationController(NOTE_DUR / 2, NOTE_TARGET_VAL / 2, getBtn());
    }
    private HeldNoteAnimationController noteAnimation() {
        return (HeldNoteAnimationController)noteAnimation;
    }

    public void playRelease() {
        noteAnimation().playReleased(foreignPlaying);
    }

    public void playNoteAnimation(final boolean isForeign) {
        foreignPlaying = isForeign;

        noteAnimation().playHold(isForeign);
        rings.add(new NoteRing(noteButton, isForeign));
    }
}
