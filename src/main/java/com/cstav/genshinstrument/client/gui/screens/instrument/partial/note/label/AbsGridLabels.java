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
    @Deprecated(forRemoval = true)
    public static String getNoteName(final float pitch, final int noteRow) {
        // Assuming pitch step is always .05 and (0 < pitch < 2)
        // +1 because the lyre starts at C
        float index = ((pitch + noteRow * .05f * 2) * 100 - 100) / 5 + 1;
        // Ceil/floor for float imperfections
        index += (index >= 0) ? .001f : -.001f;

        // E and F shenanigans
        // This is after a few hours of trail and error at 12am+, I'm too tired now
        // Thus:
        //FIXME there is probablly a much better way to do this
        // TODO Compose the method in the brackets into a seperate method
        if ( ((pitch * 100 - 100) / 5 + 1) + 5 < index)
            index--;
        if ( ((pitch * 100 - 100) / 5 + 1) + 10 < index)
            index++;

        // This makes a similar behaviour to python's indexing,
        // where negatives are counted as backwards indexing.
        if (index < 0)
            index += PITCHES.length;

        return PITCHES[(int)index % PITCHES.length];
    }

}