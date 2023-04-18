package com.cstav.genshinstrument.client.gui.screens.options.instrument;

import org.slf4j.Logger;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractGridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteGridLabel;
import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.GridWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(value = Dist.CLIENT, modid = Main.MODID, bus = Bus.FORGE)
public class GridInstrumentOptionsScreen extends AbstractInstrumentOptionsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();


    public GridInstrumentOptionsScreen(final AbstractGridInstrumentScreen screen) {
        super(screen);
    }
    public GridInstrumentOptionsScreen(final Screen lastScreen) {
        super(lastScreen);
    }


    @Override
    public NoteGridLabel[] getLabels() {
        return NoteGridLabel.values();
    }
    @Override
    public NoteGridLabel getCurrentLabel() {
        return ModClientConfigs.GRID_LABEL_TYPE.get();
    }


    // Register a button to open this GUI on the Options screen
    @SubscribeEvent
    public static void onScreenRendered(final ScreenEvent.Init.Post event) {
        final Screen screen = event.getScreen();
        if (!(screen instanceof OptionsScreen))
            return;

        GridWidget grid = null;
        for (final GuiEventListener listener : event.getListenersList())
            if (listener instanceof GridWidget) {
                grid = (GridWidget)listener;
                break;
            }
        if (grid == null)
            LOGGER.info("No Grid found on Options screen, aborting button insertion");

        
        // Assuming 2 columns
        final int size = grid.children().size(),
            column = size % 2, row = size / 2;

        final Minecraft minecraft = Minecraft.getInstance();
        grid.addChild(
            Button.builder(Component.translatable("button.genshinstrument.instrumentOptions"), (btn) ->
                minecraft.setScreen(new GridInstrumentOptionsScreen(minecraft.screen))
            ).build()
        , row, column);

        grid.pack();
        
    }
    
}
