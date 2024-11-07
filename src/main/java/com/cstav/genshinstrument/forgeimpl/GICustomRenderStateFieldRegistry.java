package com.cstav.genshinstrument.forgeimpl;

import com.cstav.genshinstrument.mixins.client.util.ICustomRenderFieldProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
public class GICustomRenderStateFieldRegistry {

    private static final HashMap<ResourceLocation, CustomRenderStateField<?>> CUSTOM_RENDER_STATE_FIELDS = new HashMap<>();

    public static <T> CustomRenderStateField<T> register(final ResourceLocation id, final CustomRenderStateField<T> field) {
        CUSTOM_RENDER_STATE_FIELDS.put(id, field);
        return field;
    }

    public static <E extends Entity, S extends EntityRenderState> void initFields(EntityRenderer<E, S> entityRenderer,
                                                                                  E entity,
                                                                                  S state,
                                                                                  float packedLight) {
        CUSTOM_RENDER_STATE_FIELDS.values().stream()
            .filter((field) ->
                field.entityType().isInstance(entity) && field.renderStateType().isInstance(state)
            )
            .forEach((field) ->
                ((ICustomRenderFieldProvider)state).genshin_Instruments$setCustomField(
                    field,
                    field.initValSupplier().get(entityRenderer, entity, state, packedLight)
                )
            );
    }

}
