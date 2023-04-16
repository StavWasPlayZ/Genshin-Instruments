package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteGridButton;
import com.cstav.genshinstrument.client.keyMaps.KeyMappings;

import net.minecraft.network.chat.Component;

/**
 * An enum holding all labels for {@code NoteGridButton}.
 * When getting from their respected suppliers, it is expected you pass
 * an instance of {@code NoteGridButton}.
 */
public enum NoteGridLabel {
    KEYBOARD_LAYOUT((note) -> Component.literal(
        KeyMappings.INSTRUMENT_MAPPINGS[ng(note).column][ng(note).row]
            .getDisplayName().getString().toUpperCase()
    )),
    DO_RE_MI((note) -> Component.translatable(
        AbstractNoteLabels.TRANSLATABLE_PATH + AbstractNoteLabels.DO_RE_MI[ng(note).row % ng(note).maxRows]
    )),
    ABC((note) -> Component.literal(
        (
            (ng(note).column == 0) ? "A" :
            (ng(note).column == 1) ? "B" :
            "C"
        ) + (ng(note).row + 1)
    )),
    NONE(NoteLabelSupplier.EMPTY);
        

    private final NoteLabelSupplier labelSupplier;
    private NoteGridLabel(final NoteLabelSupplier labelSupplier) {
        this.labelSupplier = labelSupplier;
    }

    public NoteLabelSupplier getLabelSupplier() {
        return labelSupplier;
    }

    
    public String getKey() {
        return "button.genshinstrument.label." + toString().toLowerCase();
    }


    /**
     * @return Returns {@code note} as a NoteGridButton
     */
    private static NoteGridButton ng(final NoteButton note) {
        return (NoteGridButton)note;
    }

}