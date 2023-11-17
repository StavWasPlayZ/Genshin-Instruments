package com.cstav.genshinstrument.client.gui.screen.instrument;

import java.awt.*;

import com.cstav.genshinstrument.client.config.ModClientConfigs;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * @implNote
 * This screen was heavily inspired by <a href=https://ko-fi.com/s/665c3cc518>Kistu-Lyre+</a>'s Disclaimer screen.
 * Please consider supporting them on Ko-fi!
 */
public class GenshinConsentScreen extends WarningScreen {

    private static final Component TITLE = new TranslatableComponent(
        "genshinstrument.genshin_disclaimer.title"
    ).withStyle(ChatFormatting.BOLD);
    // Can't create object field because of constructor
    private static final MutableComponent CONTENT = new TranslatableComponent(
        "genshinstrument.genshin_disclaimer.content", boldenAll(2)
    );
    private static final Component NARRATION = TITLE.copy().append("\n").append(CONTENT);

    public GenshinConsentScreen(final Screen previousScreen) {
        super(TITLE, CONTENT, null, NARRATION, previousScreen);
    }

    private MultiLineLabel message = MultiLineLabel.EMPTY;

    @Override
    protected void init() {
        final Button acknowledgeButton = new Button(
            0, 0,
            160, 20, CommonComponents.GUI_PROCEED,
            (button) -> {
                ModClientConfigs.ACCEPTED_GENSHIN_CONSENT.set(true);
                minecraft.setScreen(previous);
            }
        );

        // Make space for if the button is overlayed atop of the greeting
        final int preferredButtonY = 100 + 140, annoyingButtonY = height - acknowledgeButton.getHeight() - 10;

        acknowledgeButton.x = (width - acknowledgeButton.getWidth()) / 2;
        acknowledgeButton.y = Math.min(annoyingButtonY, preferredButtonY);

        this.addRenderableWidget(acknowledgeButton);

        super.init();
        //1.18- being annoying
        removeWidget(stopShowing);
        this.message = MultiLineLabel.create(this.font, CONTENT, this.width - 50);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pPoseStack);
        drawCenteredString(pPoseStack, font, TITLE, width/2, 20, Color.WHITE.getRGB());
        this.message.renderLeftAligned(pPoseStack, 25, 45, 9 * 2, 16777215);

        //superduper.render
        for(Widget widget : this.renderables) {
            widget.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }
    }
    

    private static Component bolden(final int index) {
        return new TranslatableComponent("genshinstrument.genshin_disclaimer.bolden"+index).withStyle(ChatFormatting.BOLD);
    }
    private static Object[] boldenAll(final int amount) {
        final Object[] result = new Object[amount];

        for (int i = 0; i < amount; i++)
            result[i] = bolden(i+1);

        return result;
    }


    @Override
    public void onClose() {
        super.onClose();
        previous.onClose();
    }


    @Override
    protected void initButtons(int idc) {}
}
