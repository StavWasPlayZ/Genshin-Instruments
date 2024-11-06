package com.cstav.genshinstrument.forgeimpl;

import com.cstav.genshinstrument.GInstrumentMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.*;
import net.minecraftforge.registries.DeferredRegister.RegistryHolder;

import java.util.function.Supplier;

public class GIForgeRegistries {

    public static ResourceKey<Registry<CustomRenderStateField<?>>> KEY_CUSTOM_RENDER_STATES = ResourceKey.createRegistryKey(
        GInstrumentMod.loc("custom_render_states")
    );
    public static RegistryHolder<CustomRenderStateField<?>> CUSTOM_RENDER_STATES = DeferredRegister
        .create(KEY_CUSTOM_RENDER_STATES, GInstrumentMod.MODID)
        .makeRegistry(() ->
            // GameData#makeUnsavedAndUnsynced
            RegistryBuilder.<CustomRenderStateField<?>>of().disableSaving().disableSync()
        );

}
