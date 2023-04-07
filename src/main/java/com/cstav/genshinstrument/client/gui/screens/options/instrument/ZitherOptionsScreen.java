package com.cstav.genshinstrument.client.gui.screens.options.instrument;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.zither.ZitherScreen;
import com.cstav.genshinstrument.sounds.ModSounds;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.GridWidget;
import net.minecraft.client.gui.components.GridWidget.RowHelper;
import net.minecraft.client.gui.components.LayoutSettings.LayoutSettingsImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZitherOptionsScreen extends InstrumentOptionsScreen {
    private static final String SOUND_TYPE_KEY = "button.genshinstrument.zither.soundType";

    public ZitherOptionsScreen(final Component pTitle, final boolean isOverlay, final AbstractInstrumentScreen screen) {
        super(pTitle, isOverlay, screen);
    }

    protected static ZitherSoundType perferredSoundType = ZitherSoundType.NEW;
    public static ZitherSoundType getPerferredSoundType() {
        return perferredSoundType;
    }
    //TODO: Call on perfer loading
    public static void setPerferredSoundType(final ZitherSoundType perferredSoundType) {
        ZitherOptionsScreen.perferredSoundType = perferredSoundType;
    }


    @Override
    protected void initOptionsGrid(GridWidget grid, RowHelper rowHelper) {
        super.initOptionsGrid(grid, rowHelper);
        final LayoutSettingsImpl exposed = grid.defaultCellSetting().getExposed();

        final CycleButton<ZitherSoundType> soundTypeButton = CycleButton.<ZitherSoundType>builder((type) ->
            Component.translatable(SOUND_TYPE_KEY+"."+type.toString().toLowerCase())
        )
            .withValues(ZitherSoundType.values())
            .withInitialValue(getPerferredSoundType())
            .create(0, 0,
                getButtonWidth()*2 + exposed.paddingLeft + exposed.paddingRight, getButtonHeight()
            , Component.translatable(SOUND_TYPE_KEY), this::onSoundTypeChange);

        rowHelper.addChild(soundTypeButton, 2, grid.defaultCellSetting().paddingTop(9));
    }
    protected void onSoundTypeChange(final CycleButton<ZitherSoundType> btn, final ZitherSoundType soundType) {
        if ((screen != null) && (screen instanceof ZitherScreen))
            ((ZitherScreen)screen).noteGrid.setSoundArr(soundType.getSoundArr());

        //TODO: Save to preferences
    }

    
    public static enum ZitherSoundType {
        OLD(ModSounds.getSoundsFromArr(ModSounds.ZITHER_OLD_NOTE_SOUNDS)),
        NEW(ModSounds.getSoundsFromArr(ModSounds.ZITHER_NEW_NOTE_SOUNDS));

        private SoundEvent[] soundArr;
        private ZitherSoundType(final SoundEvent[] soundType) {
            this.soundArr = soundType;
        }

        public SoundEvent[] getSoundArr() {
            return soundArr;
        }
    }
}
