package com.cstav.genshinstrument;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.label.NoteLabel;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.InstrumentChannelType;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.ZitherSoundType;
import com.cstav.genshinstrument.networking.packets.lyre.InstrumentPacket;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;

public class ModClientConfigs {
    public static final ForgeConfigSpec CONFIGS;

    public static final DoubleValue PITCH;
    public static final EnumValue<NoteLabel> LABEL_TYPE;
    public static final EnumValue<InstrumentChannelType> CHANNEL_TYPE;
    public static final EnumValue<ZitherSoundType> ZITHER_TYPE;

    static {
        final ForgeConfigSpec.Builder configBuilder = new Builder();
    
        PITCH = configBuilder.defineInRange("instrument_pitch",
            1d, InstrumentPacket.MIN_PITCH, InstrumentPacket.MAX_PITCH
        );
        LABEL_TYPE = configBuilder.defineEnum("label_type", NoteLabel.KEYBOARD_LAYOUT);
        CHANNEL_TYPE = configBuilder.defineEnum("channel_type", InstrumentChannelType.MIXED);
        ZITHER_TYPE = configBuilder.defineEnum("zither_type", ZitherSoundType.NEW);

        CONFIGS = configBuilder.build();
    }

}
