package com.cstav.genshinstrument.client.gui.screen.instrument;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @implNote
 * This screen was heavily inspired by <a href=https://ko-fi.com/s/665c3cc518>Kistu-Lyre+</a>'s Disclaimer screen.
 * Please consider supporting them on Ko-fi!
 */
@OnlyIn(Dist.CLIENT)
public class GenshinConsentScreen extends WarningScreen {

    private static final Component TITLE = Component.translatable(
        "genshinstrument.genshin_disclaimer.title"
    ).withStyle(ChatFormatting.BOLD);
    // Can't create object field because of constructor
    private static final MutableComponent CONTENT = Component.translatable(
        "genshinstrument.genshin_disclaimer.content", boldenAll(2)
    );
    private static final Component NARRATION = TITLE.copy().append("\n").append(CONTENT);

    private final Screen previousScreen;

    public GenshinConsentScreen(final Screen previousScreen) {
        super(TITLE, CONTENT, null, NARRATION);
        this.previousScreen = previousScreen;
    }

    @Override
    protected Layout addFooterButtons() {
        final Button acknowledgeButton = Button.builder(CommonComponents.GUI_ACKNOWLEDGE, (button) -> {
            ModClientConfigs.ACCEPTED_GENSHIN_CONSENT.set(true);
            minecraft.setScreen(previousScreen);
        }).build();

        acknowledgeButton.setPosition(
            (width - acknowledgeButton.getWidth()) / 2,
            height - acknowledgeButton.getHeight() - 10
        );

        LinearLayout linearlayout = LinearLayout.vertical().spacing(8);
        linearlayout.addChild(acknowledgeButton);
        return linearlayout;
    }

    //    @Override
//    protected void renderTitle(GuiGraphics gui) {
//        gui.drawCenteredString(font, title, width/2, 30, Color.WHITE.getRGB());
//    }


    private static Component bolden(final int index) {
        return Component.translatable("genshinstrument.genshin_disclaimer.bolden"+index).withStyle(ChatFormatting.BOLD);
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
        previousScreen.onClose();
    }
}