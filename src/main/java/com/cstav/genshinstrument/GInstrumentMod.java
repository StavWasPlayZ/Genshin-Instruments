package com.cstav.genshinstrument;

import com.cstav.genshinstrument.client.ModArmPose;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings;
import com.cstav.genshinstrument.item.ModItems;
import com.cstav.genshinstrument.item.GIItems;
import com.cstav.genshinstrument.item.clientExtensions.ModItemPredicates;
import com.cstav.genshinstrument.networking.GIPacketHandler;
import com.cstav.genshinstrument.sound.GISounds;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * The main class for the Genshin Instruments mod
 * 
 * @author StavWasPlayZ
 */
@Mod(GInstrumentMod.MODID)
public class GInstrumentMod
{
    public static final String MODID = "genshinstrument";


    public GInstrumentMod()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        GIPacketHandler.registerPackets();
        bus.addListener(GInstrumentMod::clientSetup);


        GIItems.register(bus);
        // ModBlocks.register(bus);
        // ModBlockEntities.register(bus);

        GISounds.register(bus);

        
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static void clientSetup(final FMLClientSetupEvent event) {
        ModArmPose.load();
        ModItemPredicates.register();

        InstrumentKeyMappings.registerKeybinds();
    }
}
