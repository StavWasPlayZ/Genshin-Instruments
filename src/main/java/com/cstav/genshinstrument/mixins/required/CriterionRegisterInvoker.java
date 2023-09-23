package com.cstav.genshinstrument.mixins.required;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;

@Mixin(CriteriaTriggers.class)
public interface CriterionRegisterInvoker {
    
    @Invoker
    public static <T extends CriterionTrigger<?>> T callRegister(String id, T criterion) {
        throw new AssertionError("Failed to assert invoker method");
    }

}
