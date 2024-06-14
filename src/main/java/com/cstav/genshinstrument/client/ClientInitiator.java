package com.cstav.genshinstrument.client;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.gui.screen.instrument.drum.AratakisGreatAndGloriousDrumScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.floralzither.FloralZitherScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.nightwind_horn.windsonglyre.NightwindHornScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.vintagelyre.VintageLyreScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.windsonglyre.WindsongLyreScreen;
import com.cstav.genshinstrument.item.clientExtensions.ModItemPredicates;
import com.cstav.genshinstrument.util.CommonUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD, modid = GInstrumentMod.MODID)
public class ClientInitiator {

    private static final Class<?>[] LOAD_ME = new Class[] {
        WindsongLyreScreen.class, VintageLyreScreen.class,
        FloralZitherScreen.class, AratakisGreatAndGloriousDrumScreen.class,
        NightwindHornScreen.class
    };

    @SubscribeEvent
    public static void initClient(final FMLClientSetupEvent event) {
        ModArmPose.load();
        ModItemPredicates.register();

        CommonUtil.loadClasses(LOAD_ME);
    }

    //TODO figure out why overrides do not work
//    @SubscribeEvent
//    public static void modelLoadEvent(final ModelEvent.RegisterGeometryLoaders event) {
//        event.register("separate_transforms", SeparateTransformsModel.Loader.INSTANCE);
//    }

}
