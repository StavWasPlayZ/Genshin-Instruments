package com.cstav.genshinstrument.capability;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.capability.lyreOpen.LyreOpenProvider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.FORGE, modid = Main.MODID)
public class ModCapabilities {

    @SubscribeEvent
    public static void registerCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {

            if (!event.getObject().getCapability(LyreOpenProvider.LYRE_OPEN).isPresent())
                event.addCapability(new ResourceLocation(Main.MODID, "lyre_caps"), new LyreOpenProvider());

        }
    }
    
}
