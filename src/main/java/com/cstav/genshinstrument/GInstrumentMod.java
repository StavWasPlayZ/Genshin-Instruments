package com.cstav.genshinstrument;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.ModArmPose;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.item.ModItems;
import com.cstav.genshinstrument.item.clientExtensions.ModItemPredicates;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.sound.ModSounds;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
        ModPacketHandler.registerPackets();
        bus.addListener(GInstrumentMod::clientSetup);


        ModItems.register(bus);
        // ModBlocks.register(bus);
        // ModBlockEntities.register(bus);

        ModSounds.register(bus);

        
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static void clientSetup(final FMLClientSetupEvent event) {
//        ModArmPose.register();
        ModItemPredicates.register();

        InstrumentKeyMappings.registerKeybinds();
    }
}
