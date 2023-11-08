package com.cstav.genshinstrument.sound;

import com.cstav.genshinstrument.GInstrumentMod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModSounds {
    
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, GInstrumentMod.MODID);
    public static void register(final IEventBus bus) {
        SOUNDS.register(bus);
    }


    public static final NoteSound[]
        WINDSONG_LYRE_NOTE_SOUNDS = nsr(loc("windsong_lyre")).stereo().registerGrid(),
        VINTAGE_LYRE_NOTE_SOUNDS = nsr(loc("vintage_lyre")).registerGrid(),

        ZITHER_NEW_NOTE_SOUNDS = nsr(loc("floral_zither_new")).registerGrid(),
        ZITHER_OLD_NOTE_SOUNDS = nsr(loc("floral_zither_old")).registerGrid(),

        GLORIOUS_DRUM = nsr(loc("glorious_drum"))
            .add(loc("glorious_drum_don"))
            .add(loc("glorious_drum_ka"), true)
        .registerAll()
    ;

    /**
     * Shorthand for {@code new ResourceLocation(Main.MODID, name)}
     */
    private static ResourceLocation loc(final String name) {
        return new ResourceLocation(GInstrumentMod.MODID, name);
    }
    /**
     * Shorthand for {@code new NoteSoundRegistrar(soundRegistrar, instrumentId)}
     */
    private static NoteSoundRegistrar nsr(ResourceLocation instrumentId) {
        return new NoteSoundRegistrar(ModSounds.SOUNDS, instrumentId);
    }

}
