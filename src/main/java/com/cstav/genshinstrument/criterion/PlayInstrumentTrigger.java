package com.cstav.genshinstrument.criterion;

import com.google.gson.JsonObject;

import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate.Composite;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class PlayInstrumentTrigger extends SimpleCriterionTrigger<PlayInstrumentTrigger.TriggerInstance> {
    // It doesn't account for namespaces, so will use genshinstrument_ prefix instead
    public static final ResourceLocation ID = new ResourceLocation("genshinstrument_play_instrument");
    

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject pJson, Composite pPlayer, DeserializationContext pContext) {
        return new TriggerInstance(pPlayer, ItemPredicate.fromJson(pJson.get("instrument")));
    }

    public void trigger(final ServerPlayer player, final ItemStack instrument) {
        trigger(player, (triggerInstance) ->
            triggerInstance.matches(instrument)
        );
    }


    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ItemPredicate item;

        public TriggerInstance(Composite pPlayer, ItemPredicate item) {
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
