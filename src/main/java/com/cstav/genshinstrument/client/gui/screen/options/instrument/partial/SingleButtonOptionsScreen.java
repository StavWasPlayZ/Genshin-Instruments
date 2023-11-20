package com.cstav.genshinstrument.client.gui.screen.options.instrument.partial;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.GridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.GridInstrumentOptionsScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

/**
 * A subclass of {@link GridInstrumentOptionsScreen} that implements a simple, singular button.
 */
@OnlyIn(Dist.CLIENT)
public abstract class SingleButtonOptionsScreen extends GridInstrumentOptionsScreen {
    private final static int SPACE_BEFORE = 20, SPACER_HEIGHT = 13;
    private int heightBefore;

    public SingleButtonOptionsScreen(final GridInstrumentScreen screen) {
        super(screen);
    }
    public SingleButtonOptionsScreen(final Screen lastScreen) {
        super(lastScreen);
    }

    protected abstract String optionsLabelKey();
    protected abstract AbstractButton constructButton();


    @Override
    protected void initOptionsGrid(GridLayout grid, GridLayout.RowHelper rowHelper) {
        super.initOptionsGrid(grid, rowHelper);

        rowHelper.addChild(SpacerElement.height(SPACER_HEIGHT), 2);
        grid.arrangeElements();
        heightBefore = grid.getHeight();

        rowHelper.addChild(constructButton(), 2);
    }

    @Override
    public void render(GuiGraphics gui, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(gui, pMouseX, pMouseY, pPartialTick);

        gui.drawCenteredString(font,
                Component.translatable(optionsLabelKey()),
                width/2, heightBefore + SPACE_BEFORE
                , Color.WHITE.getRGB()
        );
    }

}