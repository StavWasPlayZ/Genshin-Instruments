package com.cstav.genshinstrument;

import com.cstav.genshinstrument.item.ModItems;
import com.cstav.genshinstrument.sounds.ModSounds;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Main.MODID)
public class Main
{
    public static final String MODID = "genshinstrument";

    public Main()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        
        ModItems.register(bus);
        ModSounds.register(bus);

        
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(Type.CLIENT, ModClientConfigs.CONFIGS, "instrument_configs.toml");
    }
}
