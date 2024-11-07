package com.cstav.genshinstrument.mixins.client.required;

import com.cstav.genshinstrument.event.ExctractRenderStateFieldsEvent;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererInjector<E extends Entity, S extends EntityRenderState> {

    @Final
    @Shadow
    private S reusedState;

    @Unique
    @SuppressWarnings("unchecked")
    private EntityRenderer<E, S> genshin_Instruments$getSelf() {
        return (EntityRenderer<E, S>)((Object)this);
    }

    @Inject(
        method = "createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;",
        at = @At("HEAD")
    )
    private void extractRenderStateInjectorHead(E entity, float packedLight, CallbackInfoReturnable<S> cir) {
        final EntityRenderer<E, S> self = genshin_Instruments$getSelf();
//        GICustomRenderStateFieldRegistry.initFields(self, entity, state, packedLight);
        MinecraftForge.EVENT_BUS.post(new ExctractRenderStateFieldsEvent.Pre(self, entity, reusedState, packedLight));
    }
    @Inject(
        method = "createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;",
        at = @At("TAIL")
    )
    private void extractRenderStateInjector(E entity, float packedLight, CallbackInfoReturnable<S> cir) {
        final EntityRenderer<E, S> self = genshin_Instruments$getSelf();
        MinecraftForge.EVENT_BUS.post(new ExctractRenderStateFieldsEvent.Post(self, entity, reusedState, packedLight));
    }

}
