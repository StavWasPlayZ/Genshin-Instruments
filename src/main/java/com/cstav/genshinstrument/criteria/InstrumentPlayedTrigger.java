package com.cstav.genshinstrument.criteria;

import com.cstav.genshinstrument.networking.packet.instrument.c2s.C2SNoteSoundPacket;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * <p>The class holding the genshinstrument_instrument_played trigger.</p>
 * 
 * <p>
 * It is triggered in {@link C2SNoteSoundPacket} such that every sound
 * produced by an instrument will trigger this criteria.
 * It will pass the played instrument from within the {@code instrument} JSON item object.
 * </p>
 * 
 * Internally, used for triggering advancements for the player.
 */
public class InstrumentPlayedTrigger extends SimpleCriterionTrigger<InstrumentPlayedTrigger.TriggerInstance> {
    // It doesn't account for namespaces, so will use genshinstrument_ prefix instead
    public static final ResourceLocation ID = new ResourceLocation("genshinstrument_instrument_played");
    

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject pJson, ContextAwarePredicate player, DeserializationContext pContext) {
        return new TriggerInstance(player, ItemPredicate.fromJson(pJson.get("instrument")));
    }

    public void trigger(final ServerPlayer player, final ItemStack instrument) {
        trigger(player, (triggerInstance) ->
            triggerInstance.matches(instrument)
        );
    }


    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ItemPredicate item;

        public TriggerInstance(ContextAwarePredicate pPlayer, ItemPredicate item) {
            super(ID, pPlayer);
            this.item = item;
        }

        public boolean matches(final ItemStack instrument) {
            return item.matches(instrument);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext pConditions) {
            return super.serializeToJson(pConditions);
        }
    }

}
