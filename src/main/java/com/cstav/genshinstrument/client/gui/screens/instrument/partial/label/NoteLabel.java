package com.cstav.genshinstrument.client.gui.screens.instrument.partial.label;

import com.cstav.genshinstrument.client.keyMaps.KeyMappings;

import net.minecraft.network.chat.Component;

public enum NoteLabel {
    KEYBOARD_LAYOUT((row, column) -> Component.literal(
        KeyMappings.LYRE_MAPPINGS[column][row].getDisplayName().getString().toUpperCase()
    )),
    DO_RE_MI((row, column) -> Component.translatable(
        AbstractNoteLabels.TRANSLATABLE_PATH + AbstractNoteLabels.DO_RE_MI[row % 7]
    )),
    ABC((row, column) -> Component.literal(
        (
            (column == 0) ? "A" :
            (column == 1) ? "B" :
            "C"
        ) + (column + 1)
    )),
    NONE((row, column) -> Component.empty());
        

    private final NoteLabelSupplier labelSupplier;
    private NoteLabel(final NoteLabelSupplier labelSupplier) {
        this.labelSupplier = labelSupplier;
    }

    public NoteLabelSupplier getLabelSupplier() {
        return labelSupplier;
    }

    
    public String getKey() {
        return "button.genshinstrument.label." + toString().toLowerCase();
    }

}