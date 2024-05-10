package com.cstav.genshinstrument.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;

public abstract class AbstractNoteSoundRegistrar<T, R extends AbstractNoteSoundRegistrar<T, R>> {
    protected final ResourceLocation baseSoundLocation;
    protected final DeferredRegister<SoundEvent> soundRegistrar;

    public AbstractNoteSoundRegistrar(DeferredRegister<SoundEvent> soundRegistrar, ResourceLocation baseSoundLocation) {
        this.baseSoundLocation = baseSoundLocation;
        this.soundRegistrar = soundRegistrar;
    }

    public abstract R getThis();
}
