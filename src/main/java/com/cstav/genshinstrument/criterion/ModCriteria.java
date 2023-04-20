package com.cstav.genshinstrument.criterion;

import static net.minecraft.advancements.CriteriaTriggers.register;

import com.cstav.genshinstrument.Main;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

//NOTE: There just to make it load on mod bus
@EventBusSubscriber(bus = Bus.MOD, modid = Main.MODID)
public class ModCriteria {

    public static final PlayInstrumentTrigger PLAY_INSTRUMENT_TRIGGER = register(new PlayInstrumentTrigger());
    
}