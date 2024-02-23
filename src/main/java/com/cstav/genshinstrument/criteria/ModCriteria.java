package com.cstav.genshinstrument.criteria;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

//NOTE: There to make it load on setup too
@EventBusSubscriber(bus = Bus.FORGE, modid = GInstrumentMod.MODID)
public class ModCriteria {
    private static final DeferredRegister<CriterionTrigger<?>> CRITERION = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES.key(), GInstrumentMod.MODID);
    public static void register(final IEventBus bus) {
        CRITERION.register(bus);
    }

    // It doesn't account for namespaces, so will use genshinstrument_ prefix instead
    public static final RegistryObject<InstrumentPlayedTrigger> INSTRUMENT_PLAYED_TRIGGER = CRITERION.register("instrument_played", InstrumentPlayedTrigger::new);

    @SubscribeEvent
    public static void onInstrumentPlayed(final InstrumentPlayedEvent.ByPlayer event) {
        if (!event.isClientSide)
            INSTRUMENT_PLAYED_TRIGGER.get().trigger((ServerPlayer)event.player, new ItemStack(ForgeRegistries.ITEMS.getValue(event.instrumentId)));
    }

}