package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbsGridLabels {
    
    public static final String[]
        DO_RE_MI = {
            "do", "re", "mi", "fa", "so", "la", "ti"
        },
        ABC = {
            "C", "D", "E", "F", "G", "A", "B"
        };
        // PITCHES = {
        //     "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B"
        // }
    ;

    // The pitches dont even match, 1/16th is annoying to get
    // @Deprecated(forRemoval = true)
    // public static String getNoteName(final float pitch, final int noteRow) {
    //     int index = getNoteIndex(pitch + noteRow * .05f * 2);
        
    //     // // Cb at start
    //     // final boolean didWrap = index > PITCHES.length;
        
    //     // E and F
    //     final int startIndex = getNoteIndex(pitch), passed = index - startIndex;
    //     for (int i = 0; i < steppedOnPoint(startIndex, passed, 6 + (startIndex - 1)); i++)
    //         index--;
    //     // B and C
    //     // for (int i = 0; i < steppedOnPoint(startIndex, passed, 0 + (startIndex - 1)); i++)
    //     //     index--;

    //     return PITCHES[doublyPyWrap(index, PITCHES) /*+ (didWrap ? 1 : 0)*/];
    // }

    // private static int steppedOnPoint(final int start, int passed, final int point) {
    //     int counter = 0;

    //     for (int i = start; passed > 0; i = (i + 1) % PITCHES.length) {
    //         if (i == point)
    //             counter++;

    //         passed--;
    //     }

    //     return counter;
    // }

    // //NOTE: Assuming pitch step is always .05 and (0 < pitch < 2)
    // private static int getNoteIndex(final float pitch) {
    //     float result = (pitch * 100 - 100) / 5;
    //     // Ceil/floor for float imperfections
    //     result += (result >= 0) ? .001f : -.001f;

    //     return pyWrap((int)result, PITCHES);
    // }
    // /**
    //  * Provides a similar behaviour to python's indexing,
    //  * where negatives are counted backwards.
    //  */
    // private static int pyWrap(int index, final Object[] arr, boolean add1) {
    //     while (index < 0)
    //         index += arr.length + (add1 ? 1 : 0);

    //     return index;
    // }
    // private static int doublyPyWrap(int index, final Object[] arr) {
    //     while (index >= arr.length)
    //         index -= arr.length;

    //     return pyWrap(index, arr);
    // }

    // private static int pyWrap(int index, final Object[] arr) {
    //     return pyWrap(index, arr, false);
    // }

}