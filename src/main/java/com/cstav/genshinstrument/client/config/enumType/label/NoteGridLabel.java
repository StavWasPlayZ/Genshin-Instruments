package com.cstav.genshinstrument.client.config.enumType.label;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.INoteLabel;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.AbstractGridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.NoteGridButton;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings;
import com.cstav.genshinstrument.util.LabelUtil;

import net.minecraft.network.chat.Component;

/**
 * An enum holding all labels for {@code NoteGridButton}.
 * When getting from their respected suppliers, it is expected you pass
 * an instance of {@code NoteGridButton}.
 */
public enum NoteGridLabel implements INoteLabel {
    KEYBOARD_LAYOUT((note) -> INoteLabel.upperComponent(
        InstrumentKeyMappings.GRID_INSTRUMENT_MAPPINGS[ng(note).column][ng(note).row].getDisplayName()
    )),
    NOTE_NAME((note) -> Component.literal(
        LabelUtil.getCutNoteName(ng(note))
    )),
    DO_RE_MI((note) ->
        Component.translatable(
            INoteLabel.TRANSLATABLE_PATH + LabelUtil.DO_RE_MI[ng(note).row % gs(note).rows()]
        ).append(LabelUtil.getCutNoteName(ng(note)).substring(1))
    ),
    ABC_1((note) -> Component.literal(
        String.valueOf(LabelUtil.ABC[ng(note).row]) + (gs(note).columns() - ng(note).column)
    )),
    ABC_2((note) -> Component.literal(
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