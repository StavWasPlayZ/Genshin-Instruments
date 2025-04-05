package com.cstav.genshinstrument.client.gui.screen.instrument.ukelele;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButtonRenderer;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.grid.NoteGridButton;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class UkuleleNoteButtonRenderer extends NoteButtonRenderer {
    protected final ResourceLocation
        topColumnNotePressedLocation,
        topColumnNoteReleasedLocation,
        topColumnNoteHoverLocation
    ;

    public UkuleleNoteButtonRenderer(NoteButton noteButton, Supplier<ResourceLocation> labelTextureProvider) {
        super(noteButton, labelTextureProvider);

        topColumnNotePressedLocation = getResourceFromRoot("note/top_pressed.png");
        topColumnNoteReleasedLocation = getResourceFromRoot("note/top_released.png");
        topColumnNoteHoverLocation = getResourceFromRoot("note/top_hovered.png");
    }

    private NoteGridButton getButton() {
        return (NoteGridButton) noteButton;
    }


    @Override
    protected ResourceLocation getNoteReleasedLocation() {
        return getTopColumnOverride(topColumnNoteReleasedLocation, super.getNoteReleasedLocation());
    }
    @Override
    protected ResourceLocation getNotePressedLocation() {
        return getTopColumnOverride(topColumnNotePressedLocation, super.getNotePressedLocation());
    }
    @Override
    protected ResourceLocation getNoteHoverLocation() {
        return getTopColumnOverride(topColumnNoteHoverLocation, super.getNoteHoverLocation());
    }

    private ResourceLocation getTopColumnOverride(final ResourceLocation newLocation, final ResourceLocation superLocation) {
        if (getButton().column == 0) {
            return newLocation;
        }

        return superLocation;
    }
}
