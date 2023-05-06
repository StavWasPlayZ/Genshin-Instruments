package com.cstav.genshinstrument.criteria;

import static net.minecraft.advancements.CriteriaTriggers.register;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

//NOTE: There to make it load on setup too
@EventBusSubscriber(bus = Bus.FORGE, modid = Main.MODID)
public class ModCriteria {

    public static final InstrumentPlayedTrigger INSTRUMENT_PLAYED_TRIGGER = register(new InstrumentPlayedTrigger());

    @SubscribeEvent
    public static void onInstrumentPlayed(final InstrumentPlayedEvent event) {
        INSTRUMENT_PLAYED_TRIGGER.trigger(event.player, event.instrument);
    }
    
}