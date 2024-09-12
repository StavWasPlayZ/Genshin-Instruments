package com.cstav.genshinstrument.client.gui.screen.options.instrument.partial;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.widget.copied.AbstractContainerWidget;
import com.cstav.genshinstrument.client.util.ClientUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import net.minecraft.client.OptionInstance.TooltipSupplier;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

import java.awt.*;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

/**
 * The base class for all instrument options screens.
 */
@OnlyIn(Dist.CLIENT)
public abstract class AbstractInstrumentOptionsScreen extends Screen {

    public final Optional<InstrumentScreen> instrumentScreen;
    public final Screen lastScreen;

    /**
     * True if this menu is an overlay of an {@link InstrumentScreen};
     * and one does exist
     */
    public final boolean isOverlay;
    
    
    public AbstractInstrumentOptionsScreen(Component pTitle, InstrumentScreen instrumentScreen, Screen lastScreen) {
        super(pTitle);
        this.instrumentScreen = Optional.ofNullable(instrumentScreen);
        this.lastScreen = lastScreen;

        this.isOverlay = instrumentScreen != null;
    }
    public AbstractInstrumentOptionsScreen(Component pTitle, Optional<InstrumentScreen> instrumentScreen, Screen lastScreen) {
        this(pTitle, instrumentScreen.orElse(null), lastScreen);
    }
    public AbstractInstrumentOptionsScreen(Component pTitle, InstrumentScreen instrumentScreen) {
        this(pTitle, instrumentScreen, null);
    }
    public AbstractInstrumentOptionsScreen(Component pTitle, Optional<InstrumentScreen> instrumentScreen) {
        this(pTitle, instrumentScreen, null);
    }
    public AbstractInstrumentOptionsScreen(Component pTitle, Screen prevScreen) {
        this(pTitle, Optional.empty(), prevScreen);
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
        super.render(stack, pMouseX, pMouseY, pPartialTick);
        drawCenteredString(stack, font, title, width/2, 15, Color.WHITE.getRGB());

        renderables.forEach((renderable) -> renderTooltips(renderable, stack, pMouseX, pMouseY));
    }
    private void renderTooltips(Widget widget, PoseStack stack, int pMouseX, int pMouseY) {
        if (widget instanceof AbstractContainerWidget container) {
            container.getContainedChildren().forEach((child) -> renderTooltips(child, stack, pMouseX, pMouseY));
            return;
        }

        if (!(widget instanceof AbstractWidget aWidget) || !(aWidget instanceof TooltipAccessor))
            return;

        if (aWidget.isHoveredOrFocused())
            renderTooltip(stack, ((TooltipAccessor)aWidget).getTooltip(), pMouseX, pMouseY);
    }


    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        // Pass keys to the instrument screen if they are consumed
        instrumentScreen.ifPresent((screen) -> {
           if (screen.isKeyConsumed(pKeyCode, pScanCode))
               screen.keyPressed(pKeyCode, pScanCode, pModifiers);
        });

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        instrumentScreen.ifPresent((screen) -> {
           if (screen.isKeyConsumed(pKeyCode, pScanCode))
               screen.keyReleased(pKeyCode, pScanCode, pModifiers);
        });

        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }


    protected <T> TooltipSupplier<T> tooltip(final Function<T, Component> text) {
        return (value) -> minecraft.font.split(text.apply(value), 200);
    }

    @Override
    public boolean isPauseScreen() {
        return !isOverlay;
    }

    @Override
    public void onClose() {
        saveOptions();

        if (isOverlay) {
            super.onClose();
            if (lastScreen != null)
                minecraft.pushGuiLayer(lastScreen);
        }
        else if (lastScreen != null)
            minecraft.setScreen(lastScreen);
        else
            super.onClose();
    }


    /* ---------------- Save System --------------- */

    protected final HashMap<String, Runnable> appliedOptions = new HashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Queues the given option to later be saved.
     * Most notably, a save occurs when the client closes this screen.
     * @param optionKey A unique identifier of this option. If a duplicate entry
     * exists, it will be overwritten.
     * @param saveRunnable The runnable for saving the option
     */
    protected void queueToSave(String optionKey, final Runnable saveRunnable) {
        final String modId = modId();
        if (modId != null)
            optionKey = modId + ":" + optionKey;

        if (appliedOptions.containsKey(optionKey))
            appliedOptions.replace(optionKey, saveRunnable);
        else
            appliedOptions.put(optionKey, saveRunnable);
    }

    public void saveOptions() {
        if (appliedOptions.isEmpty())
            return;

        appliedOptions.values().forEach(Runnable::run);
        ModClientConfigs.CONFIGS.save();
        
        LOGGER.info("Successfully saved "+appliedOptions.size()+" option(s) for "+title.getString());
    }


    /**
     * Fetches the Mod ID of the instrument being used
     * @apiNote Should be overwritten in the case of not being used by an instrument
     */
    public String modId() {
        return instrumentScreen.map(InstrumentScreen::getModId).orElse(null);
    }
    
}
