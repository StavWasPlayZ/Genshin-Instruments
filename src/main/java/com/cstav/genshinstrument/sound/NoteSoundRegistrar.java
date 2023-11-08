package com.cstav.genshinstrument.sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.AbstractGridInstrumentScreen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;

public class NoteSoundRegistrar {
    private static final HashMap<ResourceLocation, NoteSound[]> SOUNDS_REGISTRY = new HashMap<>();
    public static final String STEREO_SUFFIX = "_stereo";

    public static NoteSound[] getSounds(final ResourceLocation baseSoundName) {
        return SOUNDS_REGISTRY.get(baseSoundName);
    }


    /* ----------- Registration Builder ----------- */

    private final DeferredRegister<SoundEvent> soundRegistrar;
    private final ResourceLocation baseSoundLocation;

    private boolean hasStereo = false;

    public NoteSoundRegistrar(DeferredRegister<SoundEvent> soundRegistrar, ResourceLocation baseSoundLocation) {
        this.soundRegistrar = soundRegistrar;
        this.baseSoundLocation = baseSoundLocation;
    }

    /**
     * Defines that this note sound will support stereo.
     * Stereo sounds are suffixed with {@code "_stereo"}.
     */
    public NoteSoundRegistrar stereo() {
        hasStereo = true;
        return this;
    }


    public NoteSound[] register(final NoteSound[] noteSounds) {
        SOUNDS_REGISTRY.put(baseSoundLocation, noteSounds);
        return noteSounds;
    }


    /* ----------- Registration Methods ----------- */

    // Grid registrar
    /**
     * Registers a matrix of sounds for a grid instrument.
     */
    public NoteSound[] registerGrid(final int rows, final int columns) {
        final NoteSound[] sounds = new NoteSound[rows * columns];

        for (int i = 0; i < sounds.length; i++)
            sounds[i] = createNote(i);

        return register(sounds);
    }

    /**
     * Registers a matrix of sounds for a grid instrument, with the
     * default amount of {@link AbstractGridInstrumentScreen#DEF_ROWS rows} and {@link AbstractGridInstrumentScreen#DEF_COLUMNS columns}.
     */
    public NoteSound[] registerGrid() {
        return registerGrid(AbstractGridInstrumentScreen.DEF_ROWS, AbstractGridInstrumentScreen.DEF_COLUMNS);
    }


    // Singles registrar
    private final ArrayList<NoteSound> stackedSounds = new ArrayList<>();
    public NoteSoundRegistrar add(ResourceLocation soundLocation, boolean hasStereo) {
        stackedSounds.add(createNote(soundLocation, hasStereo, stackedSounds.size()));
        return this;
    }
    public NoteSoundRegistrar add(ResourceLocation soundLocation) {
        return add(soundLocation, hasStereo);
    }

    public NoteSound peek() {
        return stackedSounds.get(stackedSounds.size() - 1);
    }

    /**
     * Registers all NoteSounds added via {@link NoteSoundRegistrar#add}
     */
    public NoteSound[] registerAll() {
        return register(stackedSounds.toArray(NoteSound[]::new));
    }

    // Single register
    /**
     * Creates a singular {@link NoteSound} with null sounds, that will get filled
     * upon registration.
     */
    public NoteSound registerNote() {
        return createNote(baseSoundLocation, 0);
    }


    /**
     * Creates a singular {@link NoteSound} with null sounds, that will get filled
     * upon registration.
     */
    private NoteSound createNote(ResourceLocation soundLocation, boolean hasStereo, int index) {
        final NoteSound sound = new NoteSound(index, baseSoundLocation);

        soundRegistrar.register(soundLocation.getPath(), () ->
            sound.mono = createSoundUnsafe(soundLocation)
        );

        if (hasStereo)
            soundRegistrar.register(soundLocation.getPath() + STEREO_SUFFIX, () ->
                (sound.stereo = Optional.of(createSoundUnsafe(soundLocation.withSuffix(STEREO_SUFFIX)))).get()
            );
        else
            sound.stereo = Optional.empty();

        return sound;
    }
    
    /**
     * Creates a singular {@link NoteSound} with null sounds, that will get filled
     * upon registration.
     */
    private NoteSound createNote(ResourceLocation soundLocation, int index) {
        return createNote(soundLocation, hasStereo, index);
    }
    /**
     * Creates and registers a {@link NoteSound} with null sounds, that will get filled
     * upon registration.
     * The name of the registered sound entry will be suffixed by "_note{@code noteIndex}".
     * @param noteIndex The index of the note
     */
    public NoteSound createNote(int noteIndex) {
        return createNote(baseSoundLocation.withSuffix("_note_"+noteIndex), noteIndex);
    }
    

    private static SoundEvent createSoundUnsafe(final ResourceLocation location) {
        return SoundEvent.createVariableRangeEvent(location);
    }
}
