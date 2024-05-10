package com.cstav.genshinstrument.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.function.IntFunction;

public abstract class ChainableNoteSoundRegistrar<T, R extends AbstractNoteSoundRegistrar<T, R>> extends AbstractNoteSoundRegistrar<T, R> {
    public ChainableNoteSoundRegistrar(DeferredRegister<SoundEvent> soundRegistrar, ResourceLocation baseSoundLocation) {
        super(soundRegistrar, baseSoundLocation);
    }

    public abstract T[] register(final T[] noteSounds);

    protected final ArrayList<T> stackedSounds = new ArrayList<>();


    /**
     * @return The head of the stacked sounds
     */
    public T peek() {
        return stackedSounds.get(stackedSounds.size() - 1);
    }

    /**
     * Registers all NoteSounds added via chained {@link ChainedNoteSoundRegistrar#add}
     */
    public T[] registerAll() {
        return register(stackedSounds.toArray(noteArrayGenerator()));
    }
    protected abstract IntFunction<T[]> noteArrayGenerator();
}
