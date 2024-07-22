package com.cstav.genshinstrument.client.config.enumType;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public enum InstrumentChannelType {
    MONO, MIXED, STEREO;

    /**
     * @return The translation key name
     * for this element
     */
    public String getKey() {
        return toString().toLowerCase(Locale.ENGLISH);
    }
}
