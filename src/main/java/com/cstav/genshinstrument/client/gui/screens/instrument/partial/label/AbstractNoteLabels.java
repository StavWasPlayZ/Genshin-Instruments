package com.cstav.genshinstrument.client.gui.screens.instrument.partial.label;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractNoteLabels {
    public static final String TRANSLATABLE_PATH = "genshinstrument.lyre.label.";

    public static final String[] DO_RE_MI = {
        "do", "re", "mi", "fa", "so", "la", "ti"
    };
}