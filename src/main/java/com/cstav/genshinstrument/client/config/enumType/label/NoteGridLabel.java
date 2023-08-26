package com.cstav.genshinstrument.client.config.enumType.label;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.label.INoteLabel;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.NoteGridButton;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings;
import com.cstav.genshinstrument.util.LabelUtil;
import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.Lazy;

/**
 * An enum holding all labels for {@code NoteGridButton}.
 * When getting from their respected suppliers, it is expected you pass
 * an instance of {@code NoteGridButton}.
 */
public enum NoteGridLabel implements INoteLabel {
    KEYBOARD_LAYOUT((note) -> INoteLabel.upperComponent(
        InstrumentKeyMappings.GRID_INSTRUMENT_MAPPINGS[ng(note).column][ng(note).row].getDisplayName()
    )),
    QWERTY((note) -> Component.translatable(
        InstrumentKeyMappings.GRID_INSTRUMENT_MAPPINGS[ng(note).column][ng(note).row].getName()
            .substring("key.keyboard.".length()).toUpperCase()
    )),
    NOTE_NAME((note) -> Component.literal(
        note.getCutNoteName()
    )),
    DO_RE_MI((note) ->
        LabelUtil.toDoReMi(note.getCutNoteName())
    ),
    NONE(NoteLabelSupplier.EMPTY);

    private final NoteLabelSupplier labelSupplier;
    private NoteGridLabel(final NoteLabelSupplier labelSupplier) {
        this.labelSupplier = labelSupplier;
    }


    @Override
    public NoteLabelSupplier getLabelSupplier() {
        return labelSupplier;
    }


    private static final Lazy<Boolean> HAS_QWERTY = Lazy.of(() -> {
        final String qwerty = "QWERTY";
        final Key[] keyRow = InstrumentKeyMappings.GRID_INSTRUMENT_MAPPINGS[0];

        // Assuming there will be more than 6 entries here
        for (int i = 0; i < qwerty.length(); i++) {
            if (qwerty.charAt(i) != keyRow[i].getDisplayName().getString(1).charAt(0))
                return false;
        }

        return true;
    });

    
    public static NoteGridLabel[] availableVals() {
        final NoteGridLabel[] vals = values();

        // Ignore QWERTY if already using this layout
        if (HAS_QWERTY.get() && (ModClientConfigs.GRID_LABEL_TYPE.get() != QWERTY)) {
            final NoteGridLabel[] result = new NoteGridLabel[vals.length - 1];

            // 2nd index to not go out of bounds
            int j = 0;
            for (int i = 0; i < vals.length; i++) {
                if (vals[i] == QWERTY)
                    i++;

                result[j] = vals[i];
                j++;
            }

            return result;
        }

        return vals;
    }
    

    private static NoteGridButton ng(final NoteButton btn) {
        return (NoteGridButton)btn;
    }
    // private static AbstractGridInstrumentScreen gs(final NoteButton btn) {
    //     return (AbstractGridInstrumentScreen)btn.instrumentScreen;
    // }
}