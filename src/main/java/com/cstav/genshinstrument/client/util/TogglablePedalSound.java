package com.cstav.genshinstrument.client.util;

import com.cstav.genshinstrument.client.config.enumType.SoundType;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TogglablePedalSound<T extends SoundType> {

    public final T enabled, disabled;

    public TogglablePedalSound(final T enabled, final T disabled) {
        this.enabled = enabled;
        this.disabled = disabled;
    }

    /**
     * Constructs a {@link TogglablePedalSound} using the first 2 elements of the given array {@code (on = 0, off = 1)}
     */
    public TogglablePedalSound(final T[] arr) {
        this(arr[0], arr[1]);
    }

}