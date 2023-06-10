package com.cstav.genshinstrument.sound;

import java.util.Optional;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractGridInstrumentScreen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public abstract class NoteSoundRegistrer {
    public static final String STEREO_SUFFIX = "_stereo";
    
    public static RegistryObject<SoundEvent> register(DeferredRegister<SoundEvent> soundRegistrer, ResourceLocation soundLocation) {
        return soundRegistrer.register(soundLocation.getPath(), () ->
            createSoundUnsafe(soundLocation)
        );
    }
    /**
     * Creates a {@link NoteSound} with null sounds, that will get filled
     * upon registration.
     * @param name The name of the sound's entry. Appends "_note{{@code note}}" to the mono entry
     * as well as {@link NoteSoundRegistrer#STEREO_SUFFIX "_stereo"} to the stereo entry.
     * @param note The index of the note
     * @param hasStereo If this note has a stereo version
     * 
     * @return The new instrument sound instance
     * @see ModSounds#registerNote(String, boolean)
     */
    public static NoteSound registerInstrument(DeferredRegister<SoundEvent> soundRegistrer,
      ResourceLocation soundLocation, int note, boolean hasStereo) {

        return registerNote(soundRegistrer, soundLocation.withSuffix("_note_"+note), hasStereo);
    }
    /**
     * Creates a {@link NoteSound} with null sounds, that will get filled
     * upon registration.
     * @param name The name of the sound's entry. Appends {@link NoteSoundRegistrer#STEREO_SUFFIX "_stereo"} to the stereo entry, if exists.
     * @param hasStereo If this note has a stereo version
     * @return The new instrument sound instance
     */
    public static NoteSound registerNote(DeferredRegister<SoundEvent> soundRegistrer,
      ResourceLocation soundLocation, boolean hasStereo) {
        final NoteSound sound = new NoteSound();

        soundRegistrer.register(soundLocation.getPath(), () ->
            sound.mono = createSoundUnsafe(soundLocation)
        );

        if (hasStereo)
            soundRegistrer.register(soundLocation.getPath() + STEREO_SUFFIX, () ->
                (sound.stereo = Optional.of(createSoundUnsafe(soundLocation.withSuffix(STEREO_SUFFIX)))).get()
            );
        else
            sound.stereo = Optional.empty();

        return sound;
    }
    public static NoteSound registerNote(DeferredRegister<SoundEvent> soundRegistrer, ResourceLocation soundLocation) {
        return registerNote(soundRegistrer, soundLocation, false);
    }

    public static SoundEvent createSoundUnsafe(final ResourceLocation location) {
        return SoundEvent.createVariableRangeEvent(location);
    }


    /**
     * Registers a series of notes for a grid instrument.
     * @param soundRegistrer The registrer to register the sounds to
     * @param baseNoteLocation The base location of which to have the sounds in
     * @param hasStereo Does this instrument have stereo support?
     * @return An array of {@link NoteSound NoteSounds} consisting of all the
     * sounds of the described instrument
     * 
     * @see NoteSoundRegistrer#registerInstrument(DeferredRegister, ResourceLocation, int, boolean)
     */
    public static NoteSound[] createInstrumentNotes(DeferredRegister<SoundEvent> soundRegistrer,
      ResourceLocation baseNoteLocation, boolean hasStereo, int rows, int columns) {

        final NoteSound[] sounds = new NoteSound[rows * columns];

        for (int i = 0; i < sounds.length; i++)
            sounds[i] = registerInstrument(soundRegistrer, baseNoteLocation, i, hasStereo);

        return sounds;
    }

    public static NoteSound[] createInstrumentNotes(DeferredRegister<SoundEvent> soundRegistrer,
      ResourceLocation baseNoteLocation, boolean hasStereo) {
        return createInstrumentNotes(soundRegistrer, baseNoteLocation, hasStereo, AbstractGridInstrumentScreen.DEF_ROWS, AbstractGridInstrumentScreen.DEF_COLUMNS);
    }
    public static NoteSound[] createInstrumentNotes(DeferredRegister<SoundEvent> soundRegistrer,
      final ResourceLocation baseNoteLocation) {
        return createInstrumentNotes(soundRegistrer, baseNoteLocation, false);
    }
}
