package com.cstav.genshinstrument.client.gui.screens.options.widget;

import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ForgeSlider;

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

    // Forge's very, very clever overflow implementation makes clients
    // (primarily Optifine clients) crash
    // For some reason the ellipsize method is undefined
    // Beats me idk
    //NOTE: Seems to note exist in 1.19.2
    // @Override
    // public void renderButton(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    // {
    //     RenderSystem.setShader(GameRenderer::getPositionTexShader);
    //     RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);

    //     final Minecraft mc = Minecraft.getInstance();
    //     final int bgYImage = this.getYImage(this.isHoveredOrFocused());
    //     ScreenUtils.blitWithBorder(poseStack,
    //         this.x, this.y,
    //         0, 46 + bgYImage * 20,
    //         this.width, this.height,
    //         200, 20,
    //         2, 3,
    //         2, 2
    //     , this.getBlitOffset());

    //     final int sliderYImage = (this.isHoveredOrFocused() ? 2 : 1) * 20;
    //     ScreenUtils.blitWithBorder(poseStack,
    //         this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(),
    //         0, 46 + sliderYImage,
    //         8, this.height,
    //         200, 20,
    //         2, 3, 2, 2
    //     , this.getBlitOffset());

    //     // final FormattedText message = mc.font.ellipsize(getMessage(), this.width - 6);
    //     drawCenteredString(poseStack, mc.font, getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, getFGColor());
    // }


    @FunctionalInterface
    public static interface SliderEvent {
        void run(final BetterSlider slider, final double value);
    }
}
