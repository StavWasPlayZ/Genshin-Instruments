package com.cstav.genshinstrument.capability;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.FORGE, modid = GInstrumentMod.MODID)
public class ModCapabilities {

    // The below should've been named "attachCapabilities" but oh well
    @SubscribeEvent
    public static void registerCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {

            if (!event.getObject().getCapability(InstrumentOpenProvider.INSTRUMENT_OPEN).isPresent())
                event.addCapability(new ResourceLocation(GInstrumentMod.MODID, "instrument_caps"), new InstrumentOpenProvider());

        }
    }

    @SubscribeEvent
    public static void actuallyRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(InstrumentOpenProvider.class);
    }
    
}
