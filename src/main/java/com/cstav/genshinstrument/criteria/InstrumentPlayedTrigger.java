package com.cstav.genshinstrument.criteria;

import java.util.Optional;

import com.cstav.genshinstrument.networking.packet.instrument.InstrumentPacket;
import com.google.gson.JsonObject;

import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * <p>The class holding the genshinstrument_instrument_played trigger.</p>
 * 
 * <p>
 * It is triggered in {@link InstrumentPacket} such that every sound
 * produced by an instrument will trigger this criteria.
 * It will pass the played instrument from within the {@code instrument} JSON item object.
 * </p>
 * 
 * Internally, used for triggering advancements for the player.
 */
public class InstrumentPlayedTrigger extends SimpleCriterionTrigger<InstrumentPlayedTrigger.TriggerInstance> {
    // It doesn't account for namespaces, so will use genshinstrument_ prefix instead
    public static final String ID = "genshinstrument_instrument_played";
    

    @Override
    protected TriggerInstance createInstance(JsonObject pJson, Optional<ContextAwarePredicate> player,
            DeserializationContext pContext) {
        return new TriggerInstance(player, ItemPredicate.fromJson(pJson.get("instrument")));
    }

    public void trigger(final ServerPlayer player, final ItemStack instrument) {
        trigger(player, (triggerInstance) ->
            triggerInstance.matches(instrument)
        );
    }


    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final Optional<ItemPredicate> item;

        public TriggerInstance(Optional<ContextAwarePredicate> pPlayer, Optional<ItemPredicate> item) {
            super(pPlayer);
            this.item = item;
        }

        public boolean matches(final ItemStack instrument) {
            return item.isEmpty() || item.get().matches(instrument);
        }
    }

}
