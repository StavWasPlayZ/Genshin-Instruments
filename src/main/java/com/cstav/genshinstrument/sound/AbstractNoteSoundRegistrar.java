package com.cstav.genshinstrument.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;

public abstract class AbstractNoteSoundRegistrar<T, R extends AbstractNoteSoundRegistrar<T, R>> {
    public static final String STEREO_SUFFIX = "_stereo";


    /* ----------- Registration Builder ----------- */

    protected final DeferredRegister<SoundEvent> soundRegistrar;
    protected final ResourceLocation baseSoundLocation;

    protected boolean hasStereo = false;

    public AbstractNoteSoundRegistrar(DeferredRegister<SoundEvent> soundRegistrar, ResourceLocation baseSoundLocation) {
        this.soundRegistrar = soundRegistrar;
        this.baseSoundLocation = baseSoundLocation;
    }

    public abstract R getThis();


    /**
     * Defines that this note sound will support stereo.
     * Stereo sounds are suffixed with {@code "_stereo"}.
     */
    public R stereo() {
        hasStereo = true;
        return getThis();
    }

}
