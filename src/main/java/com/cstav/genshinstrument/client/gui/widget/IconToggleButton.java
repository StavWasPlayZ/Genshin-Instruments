package com.cstav.genshinstrument.client.gui.widget;

import com.cstav.genshinstrument.client.util.ClientUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class IconToggleButton extends Button {
    private static final int ICON_MARGIN = 1;

    protected boolean enabled = false;
    private final ResourceLocation iconEnabled, iconDisabled;

    public IconToggleButton(int x, int y, int size,
                            ResourceLocation iconEnabled, ResourceLocation iconDisabled,
                            OnPress onPress) {
    super(x, y, size, size, new TextComponent(""), onPress);

        this.iconDisabled = iconDisabled;
        this.iconEnabled = iconEnabled;
    }
    public IconToggleButton(int x, int y, ResourceLocation iconEnabled, ResourceLocation iconDisabled, OnPress onPress) {
        this(x, y, 20, iconEnabled, iconDisabled, onPress);
    }
    public IconToggleButton(int x, int y, ResourceLocation iconEnabled, ResourceLocation iconDisabled) {
        this(x, y, iconEnabled, iconDisabled, (idc) -> {});
    }

    public boolean enabled() {
        return enabled;
    }

    @Override
    public void renderButton(PoseStack stack, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderButton(stack, pMouseX, pMouseY, pPartialTick);

        final int size = getHeight() - ICON_MARGIN;
        ClientUtil.displaySprite(enabled ? iconEnabled : iconDisabled);

        blit(stack,
            x + ICON_MARGIN, y + ICON_MARGIN,

            0, 0,
            size, size,
            size, size
        );
    }

    @Override
    public void onPress() {
        enabled = !enabled;
        super.onPress();
    }
}
