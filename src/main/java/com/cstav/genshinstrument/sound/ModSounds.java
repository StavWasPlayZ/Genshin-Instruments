package com.cstav.genshinstrument.sound;

import static com.cstav.genshinstrument.sound.NoteSoundRegistrer.createInstrumentNotes;
import static com.cstav.genshinstrument.sound.NoteSoundRegistrer.registerNote;

import com.cstav.genshinstrument.Main;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModSounds {
    
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Main.MODID);
    public static void register(final IEventBus bus) {
        SOUNDS.register(bus);
    }


    public static final NoteSound[]
        WINDSONG_LYRE_NOTE_SOUNDS = createInstrumentNotes(SOUNDS, loc("windsong_lyre"), true),
        VINTAGE_LYRE_NOTE_SOUNDS = createInstrumentNotes(SOUNDS, loc("vintage_lyre")),

        ZITHER_NEW_NOTE_SOUNDS = createInstrumentNotes(SOUNDS, loc("floral_zither_new")),
        ZITHER_OLD_NOTE_SOUNDS = createInstrumentNotes(SOUNDS, loc("floral_zither_old")),

        GLORIOUS_DRUM = new NoteSound[] {
            registerNote(SOUNDS, loc("glorious_drum_don")),
            registerNote(SOUNDS, loc("glorious_drum_ka"))
        }
    ;

    /**
     * Shorthand for {@code new ResourceLocation(Main.MODID, name)}
     */
    private static ResourceLocation loc(final String name) {
        return new ResourceLocation(Main.MODID, name);
    }

}
