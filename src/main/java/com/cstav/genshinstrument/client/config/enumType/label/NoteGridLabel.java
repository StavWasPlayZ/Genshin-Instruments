package com.cstav.genshinstrument.client.config.enumType.label;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid.GridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.grid.NoteGridButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.label.INoteLabel;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.label.NoteLabelSupplier;
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

    FIXED_ABC((note) -> Component.literal(
        String.valueOf(LabelUtil.ABC[noteGridIndex(note) % 7])
    )),
    FIXED_DO_RE_MI((note) -> Component.literal(
        String.valueOf(LabelUtil.DO_RE_MI[noteGridIndex(note) % 7])
    )),

    NONE(NoteLabelSupplier.EMPTY);


    /**
     * @return The note button's grid index
     */
    private static int noteGridIndex(final NoteButton note) {
        return ng(note).row + ng(note).column * gs(note).rows();
    }
    

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
    private static GridInstrumentScreen gs(final NoteButton btn) {
        return ng(btn).gridInstrument();
    }
}