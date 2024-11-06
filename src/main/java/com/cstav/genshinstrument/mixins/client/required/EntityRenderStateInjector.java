package com.cstav.genshinstrument.mixins.client.required;

import com.cstav.genshinstrument.forgeimpl.CustomRenderStateField;
import com.cstav.genshinstrument.mixins.client.util.ICustomRenderFieldProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;

@Mixin(EntityRenderState.class)
public class EntityRenderStateInjector implements ICustomRenderFieldProvider {

    @Unique
    private final HashMap<CustomRenderStateField<?>, Object> genshin_Instruments$customStateFields = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Unique
    @Override
    public <T> T genshin_Instruments$getCustomField(CustomRenderStateField<T> field) {
        return (T) genshin_Instruments$customStateFields.get(field);
    }
    @Unique
    @Override
    public <T> void genshin_Instruments$setCustomField(CustomRenderStateField<?> field, T value) {
        genshin_Instruments$customStateFields.put(field, value);
    }

}
