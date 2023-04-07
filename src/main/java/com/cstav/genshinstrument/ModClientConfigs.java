package com.cstav.genshinstrument;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.label.NoteLabel;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.ZitherOptionsScreen.ZitherSoundType;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;

public class ModClientConfigs {
    public static final ForgeConfigSpec CONFIGS;

    public static final DoubleValue PITCH;
    public static final EnumValue<NoteLabel> LABEL_TYPE;
    public static final EnumValue<ZitherSoundType> ZITHER_TYPE;

    static {
        final ForgeConfigSpec.Builder configBuilder = new Builder();
    
        PITCH = configBuilder.defineInRange("instrument_pitch", 1d, 0, 1d);
        LABEL_TYPE = configBuilder.defineEnum("label_type", NoteLabel.KEYBOARD_LAYOUT);
        ZITHER_TYPE = configBuilder.defineEnum("zither_type", ZitherSoundType.NEW);

        CONFIGS = configBuilder.build();
    }

}
