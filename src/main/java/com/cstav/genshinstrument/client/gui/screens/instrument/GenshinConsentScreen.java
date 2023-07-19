package com.cstav.genshinstrument.client.gui.screens.instrument;

import java.awt.Color;
import java.util.List;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * @implNote <p>
 * This screen was heavily inspired by <a href=https://ko-fi.com/s/665c3cc518>Kistu-Lyre+</a>'s Disclaimer screen.
 * Please consider supporting them on Ko-fi!
 * </p>
 * On a personal note, I unfortunately lack any budget to be in position of donating any sum of money to any one person ;-;
 */
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
    protected int getLineHeight() {
        return 10;
    }

    private static boolean hasGreeting = false;

    @Override
    protected void init() {
        final Button acknowledgeButton = Button.builder(CommonComponents.GUI_ACKNOWLEDGE, (button) -> {
            ModClientConfigs.ACCEPTED_GENSHIN_CONSENT.set(true);
            minecraft.setScreen(previousScreen);
        }).build();


        // Make space for if the button is overlayed atop of the greeting
        final int perferredButtonY = 100 + 140, annoyingButtonY = height - acknowledgeButton.getHeight() - 10;

        if (annoyingButtonY <= perferredButtonY) {
            acknowledgeButton.setPosition(
                (width - acknowledgeButton.getWidth()) / 2,
                annoyingButtonY
            );
            
            if (hasGreeting) {
                // i have to eradicate brothers alike UmU
                final List<Component> siblings = CONTENT.getSiblings();
                for (int i = 0; i < 2; i++)
                    siblings.remove(siblings.size() - 1);
                    
                hasGreeting = false;
            }
        } else {
            acknowledgeButton.setPosition(
                (width - acknowledgeButton.getWidth()) / 2,
                perferredButtonY
            );

            if (!hasGreeting) {
                CONTENT.append("\n\n\n\n")
                    .append(Component.translatable("genshinstrument.genshin_disclaimer.content.no_legal"));

                hasGreeting = true;
            }
        }

        this.addRenderableWidget(acknowledgeButton);

        super.init();
    }

    @Override
    protected void renderTitle(PoseStack stack) {
        drawCenteredString(stack, font, title, width/2, 30, Color.WHITE.getRGB());
    }
    
    

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


    @Override
    protected void initButtons(int idc) {}
}
