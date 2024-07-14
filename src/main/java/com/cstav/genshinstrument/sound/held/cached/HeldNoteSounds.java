package com.cstav.genshinstrument.sound.held.cached;

import com.cstav.genshinstrument.sound.held.HeldNoteSoundInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class storing all note sounds in the present level
 */
@OnlyIn(Dist.CLIENT)
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
     * @param soundInstance The instance to insert
     */
    public static void put(HeldNoteSoundKey key, int notePitch, HeldNoteSoundInstance soundInstance) {
        SOUND_INSTANCES
            .computeIfAbsent(key, (_k) -> new HashMap<>())
            .computeIfAbsent(notePitch, (_k) -> new ArrayList<>())
            .add(soundInstance);
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
     * Removes all note instances matching the provided {@code key}.
     */
    public static void remove(final HeldNoteSoundKey key) {
        if (!SOUND_INSTANCES.containsKey(key))
            return;

        SOUND_INSTANCES.get(key).values().forEach((heldSounds) -> heldSounds.forEach(HeldNoteSoundInstance::setReleased));
        SOUND_INSTANCES.remove(key);
    }
    /**
     * Removes all note instances matching the provided {@code key} and {@code pitch}.
     */
    public static void remove(final HeldNoteSoundKey key, int notePitch) {
        if (!SOUND_INSTANCES.containsKey(key))
            return;

        final Map<Integer, List<HeldNoteSoundInstance>> p2i = SOUND_INSTANCES.get(key);
        if (!p2i.containsKey(notePitch))
            return;

        p2i.get(notePitch).forEach(HeldNoteSoundInstance::setReleased);
        p2i.remove(notePitch);

        // No point in having a map to nothing.
        if (p2i.isEmpty())
            SOUND_INSTANCES.remove(key);
    }

    /**
     * Removes the specifically specified note sound.
     */
    public static void remove(final HeldNoteSoundKey key, int notePitch, HeldNoteSoundInstance soundInstance) {
        if (!SOUND_INSTANCES.containsKey(key))
            return;

        final Map<Integer, List<HeldNoteSoundInstance>> p2i = SOUND_INSTANCES.get(key);
        if (!p2i.containsKey(notePitch))
            return;

        List<HeldNoteSoundInstance> heldSounds = p2i.get(notePitch);
        heldSounds.remove(soundInstance);

        // Don't remove even if empty;
        // it gets removed later when needed.
    }

}
