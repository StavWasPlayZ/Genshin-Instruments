package com.cstav.genshinstrument.criteria;

import static net.minecraft.advancements.CriteriaTriggers.register;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;

//NOTE: There to make it load on setup too
@EventBusSubscriber(bus = Bus.FORGE, modid = GInstrumentMod.MODID)
public class ModCriteria {

    public static final InstrumentPlayedTrigger INSTRUMENT_PLAYED_TRIGGER = register(new InstrumentPlayedTrigger());

    @SubscribeEvent
    public static void onInstrumentPlayed(final InstrumentPlayedEvent.ByPlayer event) {
        if (!event.isClientSide)
            INSTRUMENT_PLAYED_TRIGGER.trigger((ServerPlayer)event.player, new ItemStack(ForgeRegistries.ITEMS.getValue(event.instrumentId)));
    }
    
}