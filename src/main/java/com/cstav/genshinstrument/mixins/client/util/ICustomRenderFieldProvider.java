package com.cstav.genshinstrument.mixins.client.util;

import com.cstav.genshinstrument.forgeimpl.CustomRenderStateField;

public interface ICustomRenderFieldProvider {
    <T> T genshin_Instruments$getCustomField(final CustomRenderStateField<T> field);
    // It won't allow me to specify T in field.
    <T> void genshin_Instruments$setCustomField(final CustomRenderStateField<?> field, final T value);
}
