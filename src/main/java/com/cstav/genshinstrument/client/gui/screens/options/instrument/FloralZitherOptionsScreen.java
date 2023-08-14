package com.cstav.genshinstrument.client.gui.screens.options.instrument;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.config.enumType.ZitherSoundType;
import com.cstav.genshinstrument.client.gui.screens.instrument.floralzither.FloralZitherScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.AbstractGridInstrumentScreen;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FloralZitherOptionsScreen extends SoundTypeOptionsScreen<ZitherSoundType> {
    private static final String SOUND_TYPE_KEY = "button.genshinstrument.zither.soundType",
        OPTIONS_LABEL_KEY = "label.genshinstrument.zither_options";
    
    public FloralZitherOptionsScreen(final AbstractGridInstrumentScreen screen) {
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
    protected void saveSoundType(ZitherSoundType soundType) {
        ModClientConfigs.ZITHER_SOUND_TYPE.set(soundType);
    }

    @Override
    protected boolean isValidForSet(AbstractGridInstrumentScreen screen) {
        return screen instanceof FloralZitherScreen;
    }
}
