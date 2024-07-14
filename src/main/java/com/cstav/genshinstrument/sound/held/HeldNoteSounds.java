package com.cstav.genshinstrument.sound.held;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class storing all note sounds in the present level.
 * Used for later releasing them.
 */
@OnlyIn(Dist.CLIENT)
public abstract class HeldNoteSounds {
    /**
     * A map matching a sound initiator to a held note sound identifier to a
     * map of pitches to active note sound instances.
     */
    private static final
        // Initiator ID
        Map<String,
            // Sound key
            Map<HeldNoteSound,
                // Note pitch
                Map<Integer,
                    // List of held sound instances
                    List<HeldNoteSoundInstance>
                >
            >
        >
        SOUND_INSTANCES = new HashMap<>();

    /**
     * @return The initiator ID of the provided object.
     * UUID for an entity, toString for any other,
     */
    public static String getInitiatorId(@NotNull Object initiator) {
        return (initiator instanceof Entity entity)
            ? entity.getStringUUID()
            : initiator.toString();
    }


    /**
     * Adds a new {@link HeldNoteSoundInstance} to the sounds map.
     * @param key The identifier of this sound
     * @param notePitch The note pitch of the sound instance
     * @param soundInstance The instance to insert
     */
    public static void put(String initiatorId, HeldNoteSound key, int notePitch, HeldNoteSoundInstance soundInstance) {
        SOUND_INSTANCES
            .computeIfAbsent(initiatorId, (_k) -> new HashMap<>())
            .computeIfAbsent(key, (_k) -> new HashMap<>())
            .computeIfAbsent(notePitch, (_k) -> new ArrayList<>())
            .add(soundInstance);
    }

    /**
     * @param key The key to check
     * @return Whether the provided key matches any entries
     * within the sounds map.
     */
    public static boolean hasInstances(final HeldNoteSound key) {
        // Should always be empty when map is empty
        return SOUND_INSTANCES.containsKey(key);
    }


    // Functions for marking held notes as released

    /**
     * Releases all sound instances
     */
    public static void releaseAll() {
        SOUND_INSTANCES.values().forEach((k2p2i) ->
            k2p2i.values().forEach((p2i) ->
                p2i.values().forEach((instances) ->
                    instances.forEach(HeldNoteSoundInstance::setReleased)
                )
            )
        );

        SOUND_INSTANCES.clear();
    }

    /**
     * Releases all note instances produced by the provided initiator.
     */
    public static void release(String initiatorId) {
        if (!SOUND_INSTANCES.containsKey(initiatorId))
            return;

        SOUND_INSTANCES.get(initiatorId).values().forEach((p2i) ->
            p2i.values().forEach((instances) ->
                instances.forEach(HeldNoteSoundInstance::setReleased)
            )
        );

        SOUND_INSTANCES.remove(initiatorId);
    }

    /**
     * Releases all note instances matching the provided {@code key}.
     */
    public static void release(String initiatorId, HeldNoteSound key) {
        if (!SOUND_INSTANCES.containsKey(initiatorId))
            return;

        final Map<HeldNoteSound, Map<Integer, List<HeldNoteSoundInstance>>> k2p2i = SOUND_INSTANCES.get(initiatorId);
        if (!k2p2i.containsKey(key))
            return;

        k2p2i.get(key).values().forEach((heldSounds) ->
            heldSounds.forEach(HeldNoteSoundInstance::setReleased)
        );
        k2p2i.remove(key);
    }

    /**
     * Releases all note instances matching the provided {@code key} and {@code pitch}.
     */
    public static void release(String initiatorId, HeldNoteSound key, int notePitch) {
        if (!SOUND_INSTANCES.containsKey(initiatorId))
            return;

        final Map<HeldNoteSound, Map<Integer, List<HeldNoteSoundInstance>>> k2p2i = SOUND_INSTANCES.get(initiatorId);
        if (!k2p2i.containsKey(key))
            return;

        final Map<Integer, List<HeldNoteSoundInstance>> p2i = k2p2i.get(key);
        if (!p2i.containsKey(notePitch))
            return;

        p2i.get(notePitch).forEach(HeldNoteSoundInstance::setReleased);
        p2i.remove(notePitch);

        // No point in having a map to nothing.
        if (p2i.isEmpty())
            k2p2i.remove(key);
    }

    /**
     * Removes the specified note sound.
     */
    public static void release(String initiatorId, HeldNoteSound key, int notePitch, HeldNoteSoundInstance soundInstance) {
        if (!SOUND_INSTANCES.containsKey(initiatorId))
            return;

        final Map<HeldNoteSound, Map<Integer, List<HeldNoteSoundInstance>>> k2p2i = SOUND_INSTANCES.get(initiatorId);
        if (!k2p2i.containsKey(key))
            return;

        final Map<Integer, List<HeldNoteSoundInstance>> p2i = k2p2i.get(key);
        if (!p2i.containsKey(notePitch))
            return;

        final List<HeldNoteSoundInstance> heldSoundInstances = p2i.get(notePitch);
        heldSoundInstances.remove(soundInstance);
    }

}
