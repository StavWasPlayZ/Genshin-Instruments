package com.cstav.genshinstrument.client.gui.widget;

import com.cstav.genshinstrument.client.util.ClientUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

public class IconToggleButton extends Button {
    private static final int ICON_MARGIN = 1;

    protected boolean enabled = false;
    private final ResourceLocation iconEnabled, iconDisabled;

    public IconToggleButton(int x, int y, int size,
                            ResourceLocation iconEnabled, ResourceLocation iconDisabled,
                            OnPress onPress) {
        super(new Button.Builder(CommonComponents.EMPTY, onPress)
            .pos(x, y)
        );
        width = height = size;

        this.iconDisabled = iconDisabled;
        this.iconEnabled = iconEnabled;
    }
    public IconToggleButton(int x, int y, ResourceLocation iconEnabled, ResourceLocation iconDisabled, OnPress onPress) {
        this(x, y, DEFAULT_HEIGHT, iconEnabled, iconDisabled, onPress);
    }
    public IconToggleButton(int x, int y, ResourceLocation iconEnabled, ResourceLocation iconDisabled) {
        this(x, y, iconEnabled, iconDisabled, (idc) -> {});
    }

    public boolean enabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    /**
     * Toggles the value of {@link IconToggleButton#enabled}
     */
    public void toggle() {
        setEnabled(!enabled);
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        final int size = getHeight() - ICON_MARGIN;
        pGuiGraphics.blit(ClientUtil::guiRT,
            enabled ? iconEnabled : iconDisabled,
            getX() + ICON_MARGIN, getY() + ICON_MARGIN,

            0, 0,
            size, size,
            size, size
        );
    }

    @Override
    public void onPress() {
        toggle();
        super.onPress();
    }
}
