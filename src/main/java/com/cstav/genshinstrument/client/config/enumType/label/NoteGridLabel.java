package com.cstav.genshinstrument.client.config.enumType.label;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.AbsGridLabels;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.INoteLabel;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.AbstractGridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.NoteGridButton;
import com.cstav.genshinstrument.client.keyMaps.KeyMappings;

import net.minecraft.network.chat.Component;

/**
 * An enum holding all labels for {@code NoteGridButton}.
 * When getting from their respected suppliers, it is expected you pass
 * an instance of {@code NoteGridButton}.
 */
public enum NoteGridLabel implements INoteLabel {
    KEYBOARD_LAYOUT((note) -> INoteLabel.upperComponent(
        KeyMappings.GRID_INSTRUMENT_MAPPINGS[ng(note).column][ng(note).row].getDisplayName()
    )),
    DO_RE_MI((note) -> Component.translatable(
        INoteLabel.TRANSLATABLE_PATH + AbsGridLabels.DO_RE_MI[ng(note).row % gs(note).rows()]
    )),
    ABC((note) -> Component.literal(
        AbsGridLabels.ABC[ng(note).row] + (gs(note).columns() - ng(note).column)
    )),
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
    private static AbstractGridInstrumentScreen gs(final NoteButton btn) {
        return (AbstractGridInstrumentScreen)btn.instrumentScreen;
    }
}