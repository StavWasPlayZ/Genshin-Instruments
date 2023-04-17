package com.cstav.genshinstrument;

import com.cstav.genshinstrument.client.gui.screens.instrument.drum.DrumNoteLabel;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteGridLabel;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.InstrumentChannelType;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.ZitherSoundType;
import com.cstav.genshinstrument.sounds.NoteSound;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;

public class ModClientConfigs {
    public static final ForgeConfigSpec CONFIGS;

    public static final DoubleValue PITCH;
    public static final EnumValue<NoteGridLabel> GRID_LABEL_TYPE;
    public static final EnumValue<InstrumentChannelType> CHANNEL_TYPE;
    public static final BooleanValue STOP_MUSIC_ON_PLAY;

    public static final EnumValue<ZitherSoundType> ZITHER_SOUND_TYPE;
    public static final EnumValue<DrumNoteLabel> DRUM_LABEL_TYPE;


    static {
        final ForgeConfigSpec.Builder configBuilder = new Builder();
    

        PITCH = configBuilder.defineInRange("instrument_pitch",
            1, doubleMe(NoteSound.MIN_PITCH), doubleMe(NoteSound.MAX_PITCH)
        );
        GRID_LABEL_TYPE = configBuilder.defineEnum("label_type", NoteGridLabel.KEYBOARD_LAYOUT);
        CHANNEL_TYPE = configBuilder.defineEnum("channel_type", InstrumentChannelType.MIXED);
        STOP_MUSIC_ON_PLAY = configBuilder.comment(
            "Stops all background music when you play or someone else within "+NoteSound.STOP_SOUND_DISTANCE+" blocks of range"
        ).define("stop_music_on_play", true);

        ZITHER_SOUND_TYPE = configBuilder.defineEnum("zither_sound_type", ZitherSoundType.NEW);
        DRUM_LABEL_TYPE = configBuilder.defineEnum("drum_label_type", DrumNoteLabel.KEYBOARD_LAYOUT);


        CONFIGS = configBuilder.build();
    }

    private static double doubleMe(final float num) {
        return Double.valueOf(Float.toString(num));
    }

}
