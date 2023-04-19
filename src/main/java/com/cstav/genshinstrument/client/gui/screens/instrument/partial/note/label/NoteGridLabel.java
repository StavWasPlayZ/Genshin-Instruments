package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label;

import static com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteLabelSupplier.create;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteGridButton;
import com.cstav.genshinstrument.client.keyMaps.KeyMappings;

import net.minecraft.network.chat.Component;

/**
 * An enum holding all labels for {@code NoteGridButton}.
 * When getting from their respected suppliers, it is expected you pass
 * an instance of {@code NoteGridButton}.
 */
public enum NoteGridLabel implements INoteLabel {
    KEYBOARD_LAYOUT(create(() -> (note) -> INoteLabel.upperComponent(
        KeyMappings.GRID_INSTRUMENT_MAPPINGS[ng(note).column][ng(note).row].getDisplayName()
    ))),
    DO_RE_MI(create(() -> (note) -> Component.translatable(
        INoteLabel.TRANSLATABLE_PATH + AbsGridLabels.DO_RE_MI[ng(note).row % ng(note).maxRows]
    ))),
    ABC(create(() -> (note) -> Component.literal(
        (
            (ng(note).column == 0) ? "A" :
            (ng(note).column == 1) ? "B" :
            "C"
        ) + (ng(note).row + 1)
    ))),
    NONE(NoteLabelSupplier.EMPTY);
        

    private final NoteLabelSupplier labelSupplier;
    private NoteGridLabel(final NoteLabelSupplier labelSupplier) {
        this.labelSupplier = labelSupplier;
    }


    @Override
    public NoteLabelSupplier getLabelSupplier() {
        return labelSupplier;
    }
    @Override
    public NoteGridLabel[] getValues() {
        return values();
    }


    private static NoteGridButton ng(final NoteButton btn) {
        return (NoteGridButton)btn;
    }
}