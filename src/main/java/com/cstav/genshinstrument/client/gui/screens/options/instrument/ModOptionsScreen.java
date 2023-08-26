package com.cstav.genshinstrument.client.gui.screens.options.instrument;

import java.awt.Color;
import java.util.HashMap;

import javax.annotation.Nullable;

import com.cstav.genshinstrument.client.ClientUtil;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//TODO rename to AbstractInstrumentOptionsScreen
@OnlyIn(Dist.CLIENT)
public class ModOptionsScreen extends Screen {

    public final @Nullable AbstractInstrumentScreen instrumentScreen;
    public final boolean isOverlay;
    
    public ModOptionsScreen(Component pTitle, AbstractInstrumentScreen instrumentScreen) {
        super(pTitle);
        this.instrumentScreen = instrumentScreen;

        this.isOverlay = instrumentScreen != null;
    }
    public ModOptionsScreen(Component pTitle) {
        this(pTitle, null);
    }


    public int getSmallButtonWidth() {
        return 190;
    }
    public int getBigButtonWidth() {
        return (getSmallButtonWidth() + ClientUtil.GRID_HORZ_PADDING) * 2;
    }
    public int getButtonHeight() {
        return 20;
    }


    @Override
    public void render(PoseStack stack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(stack);

        GuiComponent.drawCenteredString(stack, font, title, width/2, 15, Color.WHITE.getRGB());

        super.render(stack, pMouseX, pMouseY, pPartialTick);
    }


    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        // Pass keys to the instrument screen if they are consumed
        if (isOverlay && instrumentScreen.isKeyConsumed(pKeyCode, pScanCode))
            instrumentScreen.keyPressed(pKeyCode, pScanCode, pModifiers);

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (isOverlay && instrumentScreen.isKeyConsumed(pKeyCode, pScanCode))
            instrumentScreen.keyReleased(pKeyCode, pScanCode, pModifiers);

        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }


    /* ---------------- Save System --------------- */

    protected final HashMap<String, Runnable> appliedOptions = new HashMap<>();

    /**
     * Queues the given option to later be saved.
     * Most notably, a save occures when the client closes this screen.
     * @param optionKey A unique identifier of this option. If a duplicate entry
     * exists, it will be overwritten.
     * @param saveRunnable The runnable for saving the option
     */
    protected void queueToSave(final String optionKey, final Runnable saveRunnable) {
        if (appliedOptions.containsKey(optionKey))
            appliedOptions.replace(optionKey, saveRunnable);
        else
            appliedOptions.put(optionKey, saveRunnable);
    }

    protected void onSave() {
        for (final Runnable runnable : appliedOptions.values())
            runnable.run();
        
        ModClientConfigs.CONFIGS.save();
    }
    
}
