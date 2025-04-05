package com.cstav.genshinstrument.client.gui.screen.instrument.ukelele;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButtonRenderer;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.grid.NoteGridButton;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class UkuleleNoteButtonRenderer extends NoteButtonRenderer {
    public UkuleleNoteButtonRenderer(NoteButton noteButton, Supplier<ResourceLocation> labelTextureProvider) {
        super(noteButton, labelTextureProvider);
    }

    private NoteGridButton getButton() {
        return (NoteGridButton) noteButton;
    }

    @Override
    protected ResourceLocation getNoteReleasedLocation() {
        if (getButton().column == 0) {
            return notePressedLocation;
        }

        return super.getNoteReleasedLocation();
    }
}
