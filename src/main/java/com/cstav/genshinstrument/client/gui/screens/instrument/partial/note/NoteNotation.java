package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note;

public enum NoteNotation {
    NONE, FLAT, DOUBLE_FLAT, SHARP, DOUBLE_SHARP;

    public static NoteNotation getNotation(final String noteName) {
        if (noteName.endsWith("b"))
            return noteName.endsWith("bb") ? DOUBLE_FLAT : FLAT;
        if (noteName.endsWith("#"))
            return noteName.endsWith("##") ? DOUBLE_SHARP : SHARP;

        return NONE;
    }
}
