package com.cstav.genshinstrument.mixins.client.required;

import com.cstav.genshinstrument.client.gui.GIRenderStates;
import com.cstav.genshinstrument.forgeimpl.GIForgeRegistries;
import com.cstav.genshinstrument.mixins.client.util.ICustomRenderFieldProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererInjector<E extends Entity, S extends EntityRenderState> {

    @SuppressWarnings("unchecked")
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void extractRenderStateInjector(E entity, S state, float tickDelta, CallbackInfo ci) {
        final EntityRenderer<E, S> self = (EntityRenderer<E, S>)((Object)this);

        GIForgeRegistries.CUSTOM_RENDER_STATES.get().getValues().stream()
            .filter((field) -> field.entityType().isInstance(entity))
            .forEach((field) ->
                ((ICustomRenderFieldProvider)state).genshin_Instruments$setCustomField(
                    field,
                    field.supplier().get(self, entity, state, tickDelta)
                )
            );
    }

}
