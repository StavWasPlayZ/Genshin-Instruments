package com.cstav.genshinstrument.client.gui.screen.options.instrument.partial;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.GridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.GridInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.gui.widget.copied.GridWidget;
import com.cstav.genshinstrument.client.gui.widget.copied.SpacerWidget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
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
    protected void initOptionsGrid(GridWidget grid, GridWidget.RowHelper rowHelper) {
        super.initOptionsGrid(grid, rowHelper);

        rowHelper.addChild(SpacerWidget.height(SPACER_HEIGHT), 2);
        grid.pack();
        heightBefore = grid.getHeight();

        rowHelper.addChild(constructButton(), 2);
    }

    @Override
    public void render(PoseStack stack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(stack, pMouseX, pMouseY, pPartialTick);

        drawCenteredString(stack, font,
                new TranslatableComponent(optionsLabelKey()),
                width/2, heightBefore + SPACE_BEFORE
                , Color.WHITE.getRGB()
        );
    }

}