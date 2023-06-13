package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbsGridLabels {
    
    public static final String[]
        DO_RE_MI = {
            "do", "re", "mi", "fa", "so", "la", "ti"
        },
        PITCHES = {
            "Cb", "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B"
        }
    ;

    // nvm i suck
    // i have no idea what are these crazy dumb rules over this format
    @Deprecated
    //NOTE: Assuming pitch step is always .05 and (0 < pitch < 2)
    public static String getNoteName(final float pitch, final int noteRow) {
        int index = getNoteIndex(pitch + noteRow * .05f * 2);
        
        // Cb at start
        // final boolean didWrap = index > PITCHES.length;
        
        // E and F shenanigans
        final int startIndex = getNoteIndex(pitch);
        if (steppedOnPoint(startIndex, index - startIndex, 6 + (startIndex - 1)))
            index--;
        // if (Math.abs(index - getPitchUnit(pitch)) > 10)
        //     index--;

        return PITCHES[pyWrap(index % PITCHES.length, PITCHES) /*+ (didWrap ? 1 : 0)*/];
    }

    private static boolean steppedOnPoint(final int start, int passed, final int point) {
        for (int i = start; passed > 0; i = (i + 1) % PITCHES.length) {
            if (i == point)
                return true;

            passed--;
        }

        return false;
    }

    private static int getNoteIndex(final float pitch) {
        return getPitchUnit(pitch);
    }
    private static int getPitchUnit(final float pitch) {
        float result = (pitch * 100 - 100) / 5 + 1;
        // Ceil/floor for float imperfections
        result += (result >= 0) ? .001f : -.001f;

        return pyWrap((int)result, PITCHES);
    }
    /**
     * Provides a similar behaviour to python's indexing,
     * where negatives are counted backwards.
     */
    private static int pyWrap(int index, final Object[] arr, boolean add1) {
        if (index < 0)
            index += arr.length + (add1 ? 1 : 0);

        return index;
    }
    private static int pyWrap(int index, final Object[] arr) {
        return pyWrap(index, arr, false);
    }

}