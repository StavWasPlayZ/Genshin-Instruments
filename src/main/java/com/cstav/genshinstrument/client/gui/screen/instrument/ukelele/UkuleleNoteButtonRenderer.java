package com.cstav.genshinstrument.client.gui.screen.instrument.ukelele;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButtonRenderer;
import net.minecraft.client.gui.GuiGraphics;
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

    private UkuleleNoteButton getButton() {
        return (UkuleleNoteButton) noteButton;
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


    @Override
    protected void renderNote(GuiGraphics gui, InstrumentThemeLoader themeLoader) {
        if (getButton().column != 0) {
            super.renderNote(gui, themeLoader);
            return;
        }

        final int noteWidth = noteButton.getWidth(), noteHeight = noteButton.getHeight();
        final int noteX = noteButton.getX(), noteY = noteButton.getY();

        final float scaleMultiplier = noteButton.getWidth() / ((float)noteButton.instrumentScreen.getNoteSize()/2);

        gui.pose().pushPose();
        gui.pose().scale(scaleMultiplier, scaleMultiplier, scaleMultiplier);

        gui.drawCenteredString(
            MINECRAFT.font, getButton().getChordNameOfRow(),
            (int)((noteX + noteWidth/2f) / scaleMultiplier),
            (int)((noteY + noteHeight/4f + 2) / scaleMultiplier),

            ((noteButton.isPlaying() && !foreignPlaying)
                ? themeLoader.labelPressed(noteButton)
                : themeLoader.labelReleased(noteButton)
            ).getRGB()
        );

        gui.pose().popPose();
    }
}
