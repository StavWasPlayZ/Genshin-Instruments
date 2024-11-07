package com.cstav.genshinstrument.forgeimpl;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Represents a custom {@link EntityRenderState} field.
 * @param initValSupplier Called on {@link EntityRenderer#extractRenderState}. Supplies the initial value to be provided to this field.
 * @param entityType The entity type to have the field attached to
 * @param <T> The field type
 */
@OnlyIn(Dist.CLIENT)
public record CustomRenderStateField<T>(
    Class<? extends Entity> entityType,
    RenderStateSupplier<T> initValSupplier
) {
    @FunctionalInterface
    public static interface RenderStateSupplier<T> {
        /**
         * Supplies a value to be provided to a render state field.
         * @param entity The entity having this field attached to
         * @param state The state that the field will get attached to
         * @param packedLight
         * @return The value to be put in the state field
         */
        T get(EntityRenderer<?, ?> entityRenderer,
              Entity entity,
              EntityRenderState state,
              float packedLight);
    }
}