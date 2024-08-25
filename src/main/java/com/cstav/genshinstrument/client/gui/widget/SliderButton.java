package com.cstav.genshinstrument.client.gui.widget;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;

/**
 * A slider implementation that automatically
 * clamps the desired value
 */
public abstract class SliderButton extends AbstractSliderButton {

    public final double min, max;

    /**
     * Constructs a new Slider
     * @param pWidth The width of the slider
     * @param value The initial value
     * @param min The minimum input value
     * @param max The maximum input value
     */
    public SliderButton(int pWidth, double value, double min, double max) {
        super(0, 0,
            pWidth, 20,
            TextComponent.EMPTY, Mth.clampedMap(value, min, max, 0, 1)
        );

        this.min = min;
        this.max = max;

        updateMessage();
    }

    @Override
    protected void updateMessage() {
        setMessage(getMessage());
    }

    public abstract Component getMessage();


    public double getValueClamped() {
        return Mth.clampedLerp(min, max, value);
    }
    
}
