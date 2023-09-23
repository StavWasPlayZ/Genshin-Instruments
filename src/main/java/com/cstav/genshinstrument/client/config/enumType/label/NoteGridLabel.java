package com.cstav.genshinstrument.client.config.enumType.label;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.label.INoteLabel;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.NoteGridButton;
import com.cstav.genshinstrument.util.LabelUtil;

import net.minecraft.network.chat.Component;

/**
 * An enum holding all labels for {@code NoteGridButton}.
 * When getting from their respected suppliers, it is expected you pass
 * an instance of {@code NoteGridButton}.
 */
public enum NoteGridLabel implements INoteLabel {
    KEYBOARD_LAYOUT((note) -> INoteLabel.upperComponent(
        ng(note).getKey().getDisplayName()
    )),
    QWERTY((note) ->
        INoteLabel.getQwerty(ng(note).getKey())
    ),
    
    NOTE_NAME((note) -> Component.literal(
        note.getFormattedNoteName()
    )),
    DO_RE_MI((note) ->
        LabelUtil.toDoReMi(note.getFormattedNoteName())
    ),

    NONE(NoteLabelSupplier.EMPTY);
    

    private final NoteLabelSupplier labelSupplier;
    private NoteGridLabel(final NoteLabelSupplier labelSupplier) {
        this.labelSupplier = labelSupplier;
    }

    public static INoteLabel[] availableVals() {
        return INoteLabel.filterQwerty(values(), ModClientConfigs.GRID_LABEL_TYPE.get(), QWERTY);
    }


    @Override
    public NoteLabelSupplier getLabelSupplier() {
        return labelSupplier;
    }
    

    private static NoteGridButton ng(final NoteButton btn) {
        return (NoteGridButton)btn;
    }
    // private static AbstractGridInstrumentScreen gs(final NoteButton btn) {
    //     return (AbstractGridInstrumentScreen)btn.instrumentScreen;
    // }
}