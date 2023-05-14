package com.cstav.genshinstrument.client.gui.screens.options.widget;

import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ForgeSlider;

//NOTE: ForgeSlider is currently struggling with keyboard navigation
public class BetterSlider extends ForgeSlider {

    protected final SliderEvent onSliderChanged;
    public BetterSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue,
            double maxValue, double currentValue, double stepSize, SliderEvent onSliderChanged) {
        super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, 0, true);

        this.onSliderChanged = onSliderChanged;
    }
    
    @Override
    protected void applyValue() {
        onSliderChanged.run(this, getValue());
    }
    

    @FunctionalInterface
    public static interface SliderEvent {
        void run(final BetterSlider slider, final double value);
    }
}
