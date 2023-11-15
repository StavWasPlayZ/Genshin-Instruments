package com.cstav.genshinstrument.client.gui.screen.options.instrument;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.config.enumType.ZitherSoundType;
import com.cstav.genshinstrument.client.gui.screen.instrument.floralzither.FloralZitherScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.GridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.SoundTypeOptionsScreen;
import com.cstav.genshinstrument.client.util.TogglablePedalSound;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FloralZitherOptionsScreen extends SoundTypeOptionsScreen<ZitherSoundType> {
    private static final String SOUND_TYPE_KEY = "button.genshinstrument.zither.soundType",
        OPTIONS_LABEL_KEY = "label.genshinstrument.zither_options";
    
    public FloralZitherOptionsScreen(final GridInstrumentScreen screen) {
        super(screen);
    }
    

    @Override
    protected String soundTypeButtonKey() {
        return SOUND_TYPE_KEY;
    }
    @Override
    protected String optionsLabelKey() {
        return OPTIONS_LABEL_KEY;
    }


    @Override
    protected ZitherSoundType getInitSoundType() {
        return ModClientConfigs.ZITHER_SOUND_TYPE.get();
    }

    @Override
    protected ZitherSoundType[] values() {
        return ZitherSoundType.values();
    }

    @Override
    public TogglablePedalSound<ZitherSoundType> midiPedalListener() {
        return new TogglablePedalSound<ZitherSoundType>(ZitherSoundType.NEW, ZitherSoundType.OLD);
    }



    @Override
    protected void saveSoundType(ZitherSoundType soundType) {
        ModClientConfigs.ZITHER_SOUND_TYPE.set(soundType);
    }

    @Override
    protected boolean isValidForSet(InstrumentScreen screen) {
        return screen instanceof FloralZitherScreen;
    }
}
