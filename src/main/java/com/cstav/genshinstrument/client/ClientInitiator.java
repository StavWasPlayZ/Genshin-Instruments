package com.cstav.genshinstrument.client;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.GIRenderStates;
import com.cstav.genshinstrument.client.gui.screen.instrument.InstrumentScreenRegistry;
import com.cstav.genshinstrument.client.gui.screen.instrument.drum.AratakisGreatAndGloriousDrumScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.floralzither.FloralZitherScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.nightwind_horn.NightwindHornScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.vintagelyre.VintageLyreScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.windsonglyre.WindsongLyreScreen;
import com.cstav.genshinstrument.item.clientExtensions.ModItemPredicates;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.SeparateTransformsModel;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

import java.util.Map;
import java.util.function.Supplier;

@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD, modid = GInstrumentMod.MODID)
public class ClientInitiator {

    private static final Map<ResourceLocation, Supplier<? extends InstrumentScreen>> INSTRUMENTS = Map.of(
        WindsongLyreScreen.INSTRUMENT_ID, WindsongLyreScreen::new,
        VintageLyreScreen.INSTRUMENT_ID, VintageLyreScreen::new,
        FloralZitherScreen.INSTRUMENT_ID, FloralZitherScreen::new,
        AratakisGreatAndGloriousDrumScreen.INSTRUMENT_ID, AratakisGreatAndGloriousDrumScreen::new,
        NightwindHornScreen.INSTRUMENT_ID, NightwindHornScreen::new
    );

    @SubscribeEvent
    public static void initClient(final FMLClientSetupEvent event) {
        ModArmPose.load();
        GIRenderStates.load();
        ModItemPredicates.register();

        InstrumentScreenRegistry.register(INSTRUMENTS);
    }

    @SubscribeEvent
    public static void registerConfigs(final FMLConstructModEvent event) {
        ModLoadingContext.get().registerConfig(Type.CLIENT, ModClientConfigs.CONFIGS, "instrument_configs.toml");
    }

    @SubscribeEvent
    public static void modelLoadEvent(final ModelEvent.RegisterGeometryLoaders event) {
        event.register("separate_transforms", SeparateTransformsModel.Loader.INSTANCE);
    }

}
