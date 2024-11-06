package com.cstav.genshinstrument.mixins.client.required;

import com.cstav.genshinstrument.forgeimpl.GICustomRenderStateFieldRegistry;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererInjector<E extends Entity, S extends EntityRenderState> {

    @SuppressWarnings("unchecked")
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void extractRenderStateInjector(E entity, S state, float packedLight, CallbackInfo ci) {
        final EntityRenderer<E, S> self = (EntityRenderer<E, S>)((Object)this);
        GICustomRenderStateFieldRegistry.initFields(self, entity, state, packedLight);
    }

}
