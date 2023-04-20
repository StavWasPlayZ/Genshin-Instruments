package com.cstav.genshinstrument.sounds;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractGridInstrumentScreen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Main.MODID);
    public static void register(final IEventBus bus) {
        SOUNDS.register(bus);
    }

    public static RegistryObject<SoundEvent> register(final String name) {
        return SOUNDS.register(name, () -> createSoundUnsafe(name));
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
    public static NoteSound registerInstrument(final String name, final int note, final boolean hasStereo) {
        return registerInstrument(name+"_note_"+note, hasStereo);
    }
    /**
     * Creates a {@link NoteSound} with null sounds, that will get filled
     * upon registration.
     * @param name The name of the sound's entry. Appends "_stereo" to the stereo entry, if exists.
     * @param hasStereo If this note has a stereo version
     * @return The new instrument sound instance
     */
    public static NoteSound registerInstrument(final String name, final boolean hasStereo) {
        final NoteSound sound = new NoteSound();

        SOUNDS.register(name, () ->
            sound.mono = createSoundUnsafe(name)
        );
        if (hasStereo) {
            final String stereoName = name+"_stereo";
            SOUNDS.register(stereoName, () ->
                sound.stereo = createSoundUnsafe(stereoName)
            );
        }

        return sound;
    }

    public static SoundEvent createSoundUnsafe(final String name) {
        return SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, name));
    }



    public static final NoteSound[]
        WINDSONG_LYRE_NOTE_SOUNDS = createInstrumentNotes("windsong_lyre", true),
        VINTAGE_LYRE_NOTE_SOUNDS = createInstrumentNotes("vintage_lyre"),

        ZITHER_NEW_NOTE_SOUNDS = createInstrumentNotes("floral_zither_new"),
        ZITHER_OLD_NOTE_SOUNDS = createInstrumentNotes("floral_zither_old"),

        GLORIOUS_DRUM = new NoteSound[] {
            registerInstrument("glorious_drum_don", false),
            registerInstrument("glorious_drum_ka", false)
        }
    ;


    public static NoteSound[] createInstrumentNotes(String namePrefix, boolean hasStereo, int rows, int columns) {
        final NoteSound[] sounds = new NoteSound[rows * columns];

        for (int i = 0; i < sounds.length; i++)
            sounds[i] = registerInstrument(namePrefix, i, hasStereo);

        return sounds;
    }
    public static NoteSound[] createInstrumentNotes(String namePrefix, boolean hasStereo) {
        return createInstrumentNotes(namePrefix, hasStereo, AbstractGridInstrumentScreen.DEF_ROWS, AbstractGridInstrumentScreen.DEF_COLUMNS);
    }
    
    public static NoteSound[] createInstrumentNotes(final String namePrefix) {
        return createInstrumentNotes(namePrefix, false);
    }

}
