package com.cstav.genshinstrument.sound.held.cached;

import com.cstav.genshinstrument.sound.held.HeldNoteSoundInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class storing all note sounds in the present level
 */
public abstract class HeldNoteSounds {
    /**
     * A map matching a held note sound identifier to a
     * map of pitches to active note sound instances.
     */
    private static final Map<HeldNoteSoundKey, Map<Integer, List<HeldNoteSoundInstance>>> SOUND_INSTANCES = new HashMap<>();

    /**
     * Adds a new {@link HeldNoteSoundInstance} to the sounds map.
     * @param key The identifier of this sound
     * @param notePitch The note pitch of the sound instance
     * @param instance The instance to insert
     */
    public static void put(HeldNoteSoundKey key, int notePitch, HeldNoteSoundInstance instance) {
        SOUND_INSTANCES
            .computeIfAbsent(key, (_k) -> new HashMap<>())
            .computeIfAbsent(notePitch, (_k) -> new ArrayList<>())
            .add(instance);
    }

    /**
     * @param key The key to check
     * @return Whether the provided key matches any entries
     * within the sounds map.
     */
    public static boolean hasInstances(final HeldNoteSoundKey key) {
        // Should always be empty when map is empty
        return SOUND_INSTANCES.containsKey(key);
    }

    /**
     * Releases all notes matching the provided {@code key}.
     */
    public static void release(final HeldNoteSoundKey key) {
        if (!SOUND_INSTANCES.containsKey(key))
            return;

        //TODO also notify in server

        SOUND_INSTANCES.get(key).values().forEach((heldSounds) -> heldSounds.forEach(HeldNoteSoundInstance::setReleased));
        SOUND_INSTANCES.remove(key);
    }
    /**
     * Releases all notes matching the provided {@code key} and {@code pitch}.
     */
    public static void release(final HeldNoteSoundKey key, int notePitch) {
        if (!SOUND_INSTANCES.containsKey(key))
            return;

        //TODO also notify in server

        final Map<Integer, List<HeldNoteSoundInstance>> p2i = SOUND_INSTANCES.get(key);
        p2i.get(notePitch).forEach(HeldNoteSoundInstance::setReleased);
        p2i.remove(notePitch);

        // No point in having a map to nothing.
        if (p2i.isEmpty())
            SOUND_INSTANCES.remove(key);
    }

}
