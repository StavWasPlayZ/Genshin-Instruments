package com.cstav.genshinstrument.sound;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.GridInstrumentScreen;
import com.cstav.genshinstrument.sound.held.HeldNoteSound;
import com.cstav.genshinstrument.sound.registrar.HeldNoteSoundRegistrar;
import com.cstav.genshinstrument.sound.registrar.NoteSoundRegistrar;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class GISounds {
    
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
            .chain(loc("glorious_drum_don")).add()
            .chain(loc("glorious_drum_ka")).stereo().add()
        .registerAll()
    ;

    public static final HeldNoteSound[]
        NIGHTWIND_HORN = hnsr(loc("nightwind_horn"))
            .chainedHoldDelay(-1.35f)
            .releaseFadeOut(.035f)
            .fullHoldFadeoutTime(40)
            .decays(.025f)
            .buildSoundsForAll((builder) ->
                builder.stereo().registerGrid(GridInstrumentScreen.DEF_ROWS, 2)
            )
        .register(1.85f)
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
        return new NoteSoundRegistrar(GISounds.SOUNDS, instrumentId);
    }
    /**
     * Shorthand for {@code new HeldNoteSoundRegistrar(soundRegistrar, instrumentId)}
     */
    private static HeldNoteSoundRegistrar hnsr(ResourceLocation instrumentId) {
        return new HeldNoteSoundRegistrar(GISounds.SOUNDS, instrumentId);
    }

}
