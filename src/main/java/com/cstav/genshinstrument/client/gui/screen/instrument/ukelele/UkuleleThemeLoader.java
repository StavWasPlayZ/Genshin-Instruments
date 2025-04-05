package com.cstav.genshinstrument.client.gui.screen.instrument.ukelele;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.grid.NoteGridButton;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public class UkuleleThemeLoader extends InstrumentThemeLoader {
    private Color
        topColumnNoteReleasedColor = Color.BLACK,
        topColumnLabelReleasedColor = Color.BLACK,
        topColumnNotePressedColor = Color.BLACK,
        topColumnLabelPressedColor = Color.BLACK
    ;

    public UkuleleThemeLoader(ResourceLocation instrumentId) {
        super(instrumentId);
        addListener(this::loadColorTheme);
    }

    private void loadColorTheme(final JsonObject theme) {
        topColumnNoteReleasedColor = getRawNotePressed();
        topColumnNotePressedColor = getRawNoteReleased();
        topColumnLabelPressedColor = getRawLabelReleased();
        topColumnLabelReleasedColor = getRawLabelPressed();

        if (!theme.has("ukulele"))
            return;


        final JsonObject ukuleleMeta = theme.getAsJsonObject("ukulele");

        final JsonObject noteMeta = ukuleleMeta.getAsJsonObject("note");
        topColumnNoteReleasedColor = getTheme(noteMeta, "noteReleased", topColumnNoteReleasedColor);
        topColumnNotePressedColor = getTheme(noteMeta, "notePressed", topColumnNotePressedColor);

        final JsonObject labelMeta = ukuleleMeta.getAsJsonObject("label");
        topColumnLabelReleasedColor = getTheme(labelMeta, "noteReleased", topColumnLabelReleasedColor);
        topColumnLabelPressedColor = getTheme(labelMeta, "notePressed", topColumnLabelPressedColor);
    }


    public Color topColumnNoteReleasedColor(final NoteButton noteButton) {
        return topColumnNoteReleasedColor;
    }
    public Color topColumnLabelReleasedColor(final NoteButton noteButton) {
        return topColumnLabelReleasedColor;
    }
    public Color topColumnNotePressedColor(final NoteButton noteButton) {
        return topColumnNotePressedColor;
    }
    public Color topColumnLabelPressedColor(final NoteButton noteButton) {
        return topColumnLabelPressedColor;
    }

    // Override all defaults for top column
    @Override
    public Color noteReleased(NoteButton noteButton) {
        return overrideTopColumn(
            noteButton,
            topColumnNoteReleasedColor(noteButton),
            super.noteReleased(noteButton)
        );
    }
    @Override
    public Color notePressed(NoteButton noteButton) {
        return overrideTopColumn(
            noteButton,
            topColumnNotePressedColor(noteButton),
            super.notePressed(noteButton)
        );
    }
    @Override
    public Color labelReleased(NoteButton noteButton) {
        return overrideTopColumn(
            noteButton,
            topColumnLabelReleasedColor(noteButton),
            super.labelReleased(noteButton)
        );
    }
    @Override
    public Color labelPressed(NoteButton noteButton) {
        return overrideTopColumn(
            noteButton,
            topColumnLabelPressedColor(noteButton),
            super.labelPressed(noteButton)
        );
    }

    private Color overrideTopColumn(final NoteButton noteButton, final Color newColor, final Color superColor) {
        if (((NoteGridButton)noteButton).column == 0) {
            return newColor;
        }

        return superColor;
    }
}
