package com.cstav.genshinstrument.client.gui.screens.options.instrument;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.zither.ZitherScreen;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.GridWidget;
import net.minecraft.client.gui.components.GridWidget.RowHelper;
import net.minecraft.client.gui.components.LayoutSettings.LayoutSettingsImpl;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = Bus.MOD, modid = Main.MODID)
public class ZitherOptionsScreen extends InstrumentOptionsScreen {
    private static final String SOUND_TYPE_KEY = "button.genshinstrument.zither.soundType";

    public ZitherOptionsScreen(final Component pTitle, final boolean isOverlay, final AbstractInstrumentScreen screen) {
        super(pTitle, isOverlay, screen);
    }

    private ZitherSoundType perferredSoundType = ModClientConfigs.ZITHER_TYPE.get();
    public ZitherSoundType getPerferredSoundType() {
        return perferredSoundType;
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

        rowHelper.addChild(soundTypeButton, 2, rowHelper.newCellSettings().paddingTop(5));
    }

    private ZitherSoundType newSoundType = null;
    private void onSoundTypeChange(final CycleButton<ZitherSoundType> btn, final ZitherSoundType soundType) {
        newSoundType = soundType;
        if ((screen != null) && (screen instanceof ZitherScreen))
            ((ZitherScreen)screen).noteGrid.setSoundArr(soundType.soundArr().get());
    }

    @Override
    protected void onSave() {
        super.onSave();

        if (newSoundType != null)
            ModClientConfigs.ZITHER_TYPE.set(newSoundType);
    }
}
