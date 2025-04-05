package com.cstav.genshinstrument.client.gui.screen.instrument.gloriousdrum;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public enum DominantDrumType {
    DON, KA, BOTH;

    public static final String DDT_KEY = "button.genshinstrument.dominantDrumType";

    public String getKey() {
        return (this == BOTH)
            ? (DDT_KEY + ".both")
            : ((this == KA) ? DrumButtonType.KA : DrumButtonType.DON).getTransKey();
    }

    public String getDescKey() {
        return DDT_KEY + "." + name().toLowerCase(Locale.ENGLISH) + ".tooltip";
    }
}
