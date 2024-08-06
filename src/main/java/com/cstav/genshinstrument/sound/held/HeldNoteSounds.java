package com.cstav.genshinstrument.sound.held;

import com.cstav.genshinstrument.util.MultTuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        Map<InitiatorID,
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
     * Adds a new {@link HeldNoteSoundInstance} to the sounds map.
     * @param key The identifier of this sound
     * @param notePitch The note pitch of the sound instance
     * @param soundInstance The instance to insert
     */
    public static void put(InitiatorID initiatorId, HeldNoteSound key, int notePitch, HeldNoteSoundInstance soundInstance) {
        SOUND_INSTANCES
            .computeIfAbsent(initiatorId, (_k) -> new HashMap<>())
            .computeIfAbsent(key, (_k) -> new HashMap<>())
            .computeIfAbsent(notePitch, (_k) -> new ArrayList<>())
            .add(soundInstance);
    }

    /**
     * @param sound The sound to check
     * @return Whether the provided sound matches any entries
     * within the sounds map.
     */
    public static boolean hasInstances(final HeldNoteSound sound) {
        // Should always be empty when map is empty
        return SOUND_INSTANCES.values().stream()
            .anyMatch((k2p2i) -> k2p2i.containsKey(sound));
    }


    /**
     * A uniqueness of a sound instance is defined by its
     * sound type, instrument ID and note pitch.
     * @return A collection of all unique sound instances in the
     * provided list.
     */
    public static Collection<HeldNoteSoundInstance> getUnique(final List<HeldNoteSoundInstance> sounds) {
        return sounds.stream()
            .collect(Collectors.toMap(
                // Get unique by the following params:
                (sound) -> new MultTuple(sound.heldSoundContainer, sound.instrumentId, sound.notePitch),
                Function.identity(),
                (curr, repl) -> curr
            ))
            .values();
    }


    /**
     * Releases the given {@link HeldNoteSoundInstance}s and adds them all to {@code toAdd}
     * @param instances The instances to release and add
     * @param toAdd The list to add the aforementioned instances to
     */
    private static void releaseNAdd(List<HeldNoteSoundInstance> instances, List<HeldNoteSoundInstance> toAdd) {
        instances.forEach(HeldNoteSoundInstance::setReleased);
        toAdd.addAll(instances);
    }


    // Functions for marking held notes as released

    /**
     * Releases all sound instances
     * @return The released sounds
     */
    public static List<HeldNoteSoundInstance> releaseAll() {
        final List<HeldNoteSoundInstance> released = new ArrayList<>();

        SOUND_INSTANCES.values().forEach((k2p2i) ->
            k2p2i.values().forEach((p2i) ->
                p2i.values().forEach((instances) ->
                    releaseNAdd(instances, released)
                )
            )
        );

        SOUND_INSTANCES.clear();
        return Collections.unmodifiableList(released);
    }

    /**
     * Releases all note instances produced by the provided initiator.
      @return The released sounds
     */
    public static List<HeldNoteSoundInstance> release(InitiatorID initiatorId) {
        if (!SOUND_INSTANCES.containsKey(initiatorId))
            return List.of();

        final List<HeldNoteSoundInstance> released = new ArrayList<>();

        SOUND_INSTANCES.get(initiatorId).values().forEach((p2i) ->
            p2i.values().forEach((instances) ->
                releaseNAdd(instances, released)
            )
        );

        SOUND_INSTANCES.remove(initiatorId);
        return Collections.unmodifiableList(released);
    }

    /**
     * Releases all note instances matching the provided {@code sound}.
      @return The released sounds
     */
    public static List<HeldNoteSoundInstance> release(InitiatorID initiatorId, HeldNoteSound sound) {
        if (!SOUND_INSTANCES.containsKey(initiatorId))
            return List.of();

        final Map<HeldNoteSound, Map<Integer, List<HeldNoteSoundInstance>>> k2p2i = SOUND_INSTANCES.get(initiatorId);
        if (!k2p2i.containsKey(sound))
            return List.of();

        final List<HeldNoteSoundInstance> released = new ArrayList<>();

        k2p2i.get(sound).values().forEach((instances) ->
            releaseNAdd(instances, released)
        );
        k2p2i.remove(sound);

        return Collections.unmodifiableList(released);
    }

    /**
     * Releases all note instances matching the provided {@code sound} and {@code pitch}.
      @return The released sounds
     */
    public static List<HeldNoteSoundInstance> release(InitiatorID initiatorId, HeldNoteSound sound, int notePitch) {
        if (!SOUND_INSTANCES.containsKey(initiatorId))
            return List.of();

        final Map<HeldNoteSound, Map<Integer, List<HeldNoteSoundInstance>>> k2p2i = SOUND_INSTANCES.get(initiatorId);
        if (!k2p2i.containsKey(sound))
            return List.of();

        final Map<Integer, List<HeldNoteSoundInstance>> p2i = k2p2i.get(sound);
        if (!p2i.containsKey(notePitch))
            return List.of();

        final List<HeldNoteSoundInstance> released = new ArrayList<>();
        releaseNAdd(p2i.get(notePitch), released);
        p2i.remove(notePitch);

        // No point in having a map to nothing.
        if (p2i.isEmpty())
            k2p2i.remove(sound);

        return Collections.unmodifiableList(released);
    }

    /**
     * Removes the specified note sound.
      @return The released sounds
     */
    public static List<HeldNoteSoundInstance> release(InitiatorID initiatorId, HeldNoteSound sound, int notePitch,
                                                      HeldNoteSoundInstance soundInstance) {
        if (!SOUND_INSTANCES.containsKey(initiatorId))
            return List.of();

        final Map<HeldNoteSound, Map<Integer, List<HeldNoteSoundInstance>>> k2p2i = SOUND_INSTANCES.get(initiatorId);
        if (!k2p2i.containsKey(sound))
            return List.of();

        final Map<Integer, List<HeldNoteSoundInstance>> p2i = k2p2i.get(sound);
        if (!p2i.containsKey(notePitch))
            return List.of();

        final List<HeldNoteSoundInstance> heldSoundInstances = p2i.get(notePitch);
        heldSoundInstances.remove(soundInstance);

        return List.of(soundInstance);
    }

}
