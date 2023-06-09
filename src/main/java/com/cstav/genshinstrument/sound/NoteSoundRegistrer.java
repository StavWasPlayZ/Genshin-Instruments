package com.cstav.genshinstrument.sound;

import java.util.Optional;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractGridInstrumentScreen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public abstract class NoteSoundRegistrer {
    
    public static RegistryObject<SoundEvent> register(DeferredRegister<SoundEvent> soundRegistrer, final String name) {
        return soundRegistrer.register(name, () -> createSoundUnsafe(name));
    }
    /**
     * Creates a {@link NoteSound} with null sounds, that will get filled
     * upon registration.
     * @param name The name of the sound's entry. Appends "_note{{@code note}}" to the mono entry
     * as well as "_stereo" to the stereo entry.
     * @param note The index of the note
     * @param hasStereo If this note has a stereo version
     * 
     * @return The new instrument sound instance
     * @see ModSounds#registerInstrument(String, boolean)
     */
    public static NoteSound registerInstrument(DeferredRegister<SoundEvent> soundRegistrer, String name, int note, boolean hasStereo) {
        return registerInstrument(soundRegistrer, name+"_note_"+note, hasStereo);
    }
    /**
     * Creates a {@link NoteSound} with null sounds, that will get filled
     * upon registration.
     * @param name The name of the sound's entry. Appends "_stereo" to the stereo entry, if exists.
     * @param hasStereo If this note has a stereo version
     * @return The new instrument sound instance
     */
    public static NoteSound registerInstrument(DeferredRegister<SoundEvent> soundRegistrer, String name, boolean hasStereo) {
        final NoteSound sound = new NoteSound();

        soundRegistrer.register(name, () ->
            sound.mono = createSoundUnsafe(name)
        );
        if (hasStereo) {
            final String stereoName = name+"_stereo";
            soundRegistrer.register(stereoName, () ->
                (sound.stereo = Optional.of(createSoundUnsafe(stereoName))).get()
            );
        }

        return sound;
    }

    public static SoundEvent createSoundUnsafe(final String name) {
        return SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, name));
    }



    public static NoteSound[] createInstrumentNotes(DeferredRegister<SoundEvent> soundRegistrer,
      String namePrefix, boolean hasStereo, int rows, int columns) {

        final NoteSound[] sounds = new NoteSound[rows * columns];

        for (int i = 0; i < sounds.length; i++)
            sounds[i] = registerInstrument(soundRegistrer, namePrefix, i, hasStereo);

        return sounds;
    }

    public static NoteSound[] createInstrumentNotes(DeferredRegister<SoundEvent> soundRegistrer,
      String namePrefix, boolean hasStereo) {
        return createInstrumentNotes(soundRegistrer, namePrefix, hasStereo, AbstractGridInstrumentScreen.DEF_ROWS, AbstractGridInstrumentScreen.DEF_COLUMNS);
    }
    public static NoteSound[] createInstrumentNotes(DeferredRegister<SoundEvent> soundRegistrer,
      final String namePrefix) {
        return createInstrumentNotes(soundRegistrer, namePrefix, false);
    }
}
