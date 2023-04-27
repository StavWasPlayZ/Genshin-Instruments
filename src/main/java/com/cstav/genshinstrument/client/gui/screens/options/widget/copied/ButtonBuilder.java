package com.cstav.genshinstrument.client.gui.screens.options.widget.copied;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ButtonBuilder {
    public static final CreateNarration DEFAULT_NARRATION = (p_253298_) -> {
        return p_253298_.get();
    };
    

    private final Component message;
    private final OnPress onPress;
    @Nullable
    private Tooltip tooltip;
    private int x;
    private int y;
    private int width = 150;
    private int height = 20;
    private CreateNarration createNarration = DEFAULT_NARRATION;

    public ButtonBuilder(Component pMessage, OnPress pOnPress) {
         this.message = pMessage;
         this.onPress = pOnPress;
      }

    public ButtonBuilder pos(int pX, int pY) {
        this.x = pX;
        this.y = pY;
        return this;
    }

    public ButtonBuilder width(int pWidth) {
        this.width = pWidth;
        return this;
    }

    public ButtonBuilder size(int pWidth, int pHeight) {
        this.width = pWidth;
        this.height = pHeight;
        return this;
    }

    public ButtonBuilder bounds(int pX, int pY, int pWidth, int pHeight) {
        return this.pos(pX, pY).size(pWidth, pHeight);
    }

    public ButtonBuilder tooltip(@Nullable Tooltip pTooltip) {
        this.tooltip = pTooltip;
        return this;
    }

    public ButtonBuilder createNarration(CreateNarration pCreateNarration) {
        this.createNarration = pCreateNarration;
        return this;
    }

    public Button build() {
        return new Button(x, y, width, height, message, onPress, tooltip);
    }

    public Button build(java.util.function.Function<ButtonBuilder, Button> builder) {
        return builder.apply(this);
    }
}