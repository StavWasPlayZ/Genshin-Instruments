package com.cstav.genshinstrument.client.gui.screen.options.instrument;

import java.util.function.BiFunction;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.config.enumType.label.NoteGridLabel;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.label.INoteLabel;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.AbstractGridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.BaseInstrumentOptionsScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.GridLayout.RowHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = Bus.MOD, modid = GInstrumentMod.MODID, value = Dist.CLIENT)
public class GridInstrumentOptionsScreen extends BaseInstrumentOptionsScreen {

    public GridInstrumentOptionsScreen(final AbstractGridInstrumentScreen screen) {
        super(screen);
    }
    public GridInstrumentOptionsScreen(final Screen lastScreen) {
        super(lastScreen);
    }


    @Override
    public INoteLabel[] getLabels() {
        return NoteGridLabel.availableVals();
    }
    @Override
    public NoteGridLabel getCurrentLabel() {
        return ModClientConfigs.GRID_LABEL_TYPE.get();
    }

    @Override
    protected void saveLabel(final INoteLabel newLabel) {
        if (newLabel instanceof NoteGridLabel)
            ModClientConfigs.GRID_LABEL_TYPE.set((NoteGridLabel)newLabel);
    }


    @Override
    public boolean isPitchSliderEnabled() {
        return (instrumentScreen == null) ||
            !((AbstractGridInstrumentScreen)instrumentScreen).isSSTI();
    }


    @Override
    protected void initVisualsSection(GridLayout grid, RowHelper rowHelper) {
        final CycleButton<Boolean> renderBackground = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(ModClientConfigs.RENDER_BACKGROUND.get())
            .create(0, 0,
                getSmallButtonWidth(), getButtonHeight(),
                Component.translatable("button.genshinstrument.render_background"), this::onRenderBackgroundChanged
            );
        rowHelper.addChild(renderBackground);

        super.initVisualsSection(grid, rowHelper);
    }

    protected void onRenderBackgroundChanged(final CycleButton<Boolean> button, final boolean value) {
        ModClientConfigs.RENDER_BACKGROUND.set(value);
    }


    // Register this options type as the main configs
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory.class,
            () -> new ConfigScreenFactory(new BiFunction<Minecraft, Screen, Screen>() {

                @Override
                public Screen apply(Minecraft minecraft, Screen screen) {
                    return new GridInstrumentOptionsScreen(screen);
                }
                
            })
        );
    }
    
}
