package com.cstav.genshinstrument;

import com.cstav.genshinstrument.item.GIItems;
import com.cstav.genshinstrument.networking.GIPacketHandler;
import com.cstav.genshinstrument.sound.GISounds;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class for the Genshin Instruments mod
 * 
 * @author StavWasPlayZ
 */
@Mod(GInstrumentMod.MODID)
public class GInstrumentMod
{
    public static final String MODID = "genshinstrument";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);


    public GInstrumentMod()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();


        GIPacketHandler.registerPackets();

        GIItems.register(bus);
        // ModBlocks.register(bus);
        // ModBlockEntities.register(bus);

        GISounds.register(bus);

        
        MinecraftForge.EVENT_BUS.register(this);
    }
}
