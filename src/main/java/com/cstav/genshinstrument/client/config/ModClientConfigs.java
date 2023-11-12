package com.cstav.genshinstrument.client.config;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.config.enumType.InstrumentChannelType;
import com.cstav.genshinstrument.client.config.enumType.ZitherSoundType;
import com.cstav.genshinstrument.client.config.enumType.label.DrumNoteLabel;
import com.cstav.genshinstrument.client.config.enumType.label.NoteGridLabel;
import com.cstav.genshinstrument.client.gui.screen.instrument.drum.DominentDrumType;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.midi.MidiOptionsScreen;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = Bus.MOD, modid = GInstrumentMod.MODID, value = Dist.CLIENT)
public class ModClientConfigs {
    public static final ForgeConfigSpec CONFIGS;

    public static final IntValue PITCH, MIDI_DEVICE_INDEX, OCTAVE_SHIFT, MIDI_CHANNEL;
    public static final DoubleValue VOLUME;

    public static final EnumValue<NoteGridLabel> GRID_LABEL_TYPE;
    public static final EnumValue<InstrumentChannelType> CHANNEL_TYPE;
    public static final BooleanValue STOP_MUSIC_ON_PLAY, SHARED_INSTRUMENT,
        RENDER_BACKGROUND, ACCEPTED_GENSHIN_CONSENT, ACCURATE_NOTES,
        MIDI_ENABLED, EXTEND_OCTAVES, FIXED_TOUCH, ACCEPT_ALL_CHANNELS,
        NORMALIZE_VINTAGE_LYRE;

    public static final EnumValue<ZitherSoundType> ZITHER_SOUND_TYPE;
    public static final EnumValue<DrumNoteLabel> DRUM_LABEL_TYPE;
    public static final EnumValue<DominentDrumType> DOMINENT_DRUM_TYPE;


    static {
        final ForgeConfigSpec.Builder configBuilder = new Builder();
    

        PITCH = configBuilder.defineInRange("instrument_pitch",
            0, NoteSound.MIN_PITCH, NoteSound.MAX_PITCH
        );
        VOLUME = configBuilder.defineInRange("instrument_volume",
            1d, 0, 1
        );

        GRID_LABEL_TYPE = configBuilder.defineEnum("label_type", NoteGridLabel.KEYBOARD_LAYOUT);
        CHANNEL_TYPE = configBuilder.defineEnum("channel_type", InstrumentChannelType.MIXED);

        STOP_MUSIC_ON_PLAY = configBuilder.comment(
            "Stops all background music when you or someone else within "+NoteSound.STOP_SOUND_DISTANCE+" blocks of range plays an instrument"
        ).define("stop_music_on_play", true);
        SHARED_INSTRUMENT = configBuilder.comment("Defines whether you will see others playing on your instrument's screen")
            .define("display_other_players", true);

        RENDER_BACKGROUND = configBuilder.define("render_background", true);
        ACCURATE_NOTES = configBuilder.define("accurate_notes", true);

        NORMALIZE_VINTAGE_LYRE = configBuilder.define("normalize_vintage_lyre", true);

        ACCEPTED_GENSHIN_CONSENT = configBuilder.define("accepted_genshin_consent", false);


        ZITHER_SOUND_TYPE = configBuilder.defineEnum("zither_sound_type", ZitherSoundType.NEW);
        DRUM_LABEL_TYPE = configBuilder.defineEnum("drum_label_type", DrumNoteLabel.KEYBOARD_LAYOUT);


        MIDI_ENABLED = configBuilder.define("midi_enabled", false);
        MIDI_DEVICE_INDEX = configBuilder.defineInRange("midi_device_index", -1, -1, Integer.MAX_VALUE);

        EXTEND_OCTAVES = configBuilder.comment(
            "When a note that is higher/lower than the usual octave range is played, will automatically adjust the pitch to match your playings. Can only extend up to 1 octave per side: high and low C."
        ).define("extend_octaves", true);
        FIXED_TOUCH = configBuilder.comment(
            "Defines whether the velocity of a note press will not affect the instrument's volume"
        ).define("fixed_touch", false);
        ACCEPT_ALL_CHANNELS = configBuilder.define("accept_all_channels", true);

        OCTAVE_SHIFT = configBuilder.defineInRange("midi_octave_shift",
            0, MidiOptionsScreen.MIN_OCTAVE_SHIFT, MidiOptionsScreen.MAX_OCTAVE_SHIFT
        );
        MIDI_CHANNEL = configBuilder.defineInRange("midi_channel",
            0, MidiOptionsScreen.MIN_MIDI_CHANNEL, MidiOptionsScreen.MAX_MIDI_CHANNEL
        );


        DOMINENT_DRUM_TYPE = configBuilder.comment(
            "Defines the MIDI split behaviour of the Arataki's Great and Glorious Drum"
        ).defineEnum("dominent_drum_type", DominentDrumType.BOTH);


        CONFIGS = configBuilder.build();
    }


    @SubscribeEvent
    public static void registerConfigs(final FMLConstructModEvent event) {
        ModLoadingContext.get().registerConfig(Type.CLIENT, CONFIGS, "instrument_configs.toml");
    }
}
