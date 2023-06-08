package com.cstav.genshinstrument;

import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.item.ModItems;
import com.cstav.genshinstrument.sound.ModSounds;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * The main class for the Genshin Instruments mod
 * 
 * @author StavWasPlayZ
 */
@Mod(Main.MODID)
public class Main
{
    public static final String MODID = "genshinstrument";

    public Main()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        
        ModItems.register(bus);
        ModSounds.register(bus);
        ModCreativeModeTabs.regsiter(bus);

        bus.register(InstrumentPlayedEvent.class);

        
        MinecraftForge.EVENT_BUS.register(this);
    }
}
