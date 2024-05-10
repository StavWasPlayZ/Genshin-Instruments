package com.cstav.genshinstrument.sound;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;

/**
 * Defined a chainable registrar. All extra parameters
 * should be implemented via {@link ChainableNoteSoundRegistrar#paramsMap}.
 * @param <T> The registered sound type
 * @param <R> The registrar type
 */
public abstract class ChainableNoteSoundRegistrar<T, R extends AbstractNoteSoundRegistrar<T, R>> extends AbstractNoteSoundRegistrar<T, R> {
    public ChainableNoteSoundRegistrar(ResourceLocation baseSoundLocation) {
        super(baseSoundLocation);
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


    // Params map implementations
    protected final Map<String, Object> paramsMap = new HashMap<>();

    protected static boolean getBool(Map<String, Object> paramsMap, String param) {
        return getBool(paramsMap, param, false);
    }
    protected static boolean getBool(Map<String, Object> paramsMap, String param, boolean def) {
        return (boolean) paramsMap.getOrDefault(param, def);
    }
}
