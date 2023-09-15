package com.cstav.genshinstrument.client.gui.widget;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.Mth;

public abstract class SliderButton extends AbstractSliderButton {

    public final double min, max;

    public SliderButton(int pWidth, double value, double min, double max) {
        super(0, 0,
            pWidth, 20,
            CommonComponents.EMPTY, Mth.clampedMap(value, min, max, 0, 1)
        );

        this.min = min;
        this.max = max;

        updateMessage();
    }


    public double getValueClamped() {
        return Mth.clampedLerp(min, max, value);
    }
    
}
