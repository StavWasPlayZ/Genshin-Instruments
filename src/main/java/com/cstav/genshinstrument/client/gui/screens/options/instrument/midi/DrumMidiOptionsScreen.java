package com.cstav.genshinstrument.client.gui.screens.options.instrument.midi;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.drum.DominentDrumType;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.GridWidget;
import net.minecraft.client.gui.components.GridWidget.RowHelper;
import net.minecraft.client.gui.components.SpacerWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DrumMidiOptionsScreen extends MidiOptionsScreen {
    public static final String DDT_KEY = "button.genshinstrument.dominentDrumType";

    public DrumMidiOptionsScreen(Component pTitle, Screen prevScreen, AbstractInstrumentScreen instrumentScreen) {
        super(pTitle, prevScreen, instrumentScreen);
    }
    
    @Override
    protected void initOptionsGrid(GridWidget grid, RowHelper rowHelper) {
        super.initOptionsGrid(grid, rowHelper);

        rowHelper.addChild(SpacerWidget.height(15), 2);

        final CycleButton<DominentDrumType> dominentDrumType = CycleButton.<DominentDrumType>builder((type) -> Component.translatable(type.getKey()))
            .withValues(DominentDrumType.values())
            .withTooltip((type) -> Tooltip.create(Component.translatable(DDT_KEY+"."+type.name().toLowerCase()+".tooltip")))
            .withInitialValue(ModClientConfigs.DOMINENT_DRUM_TYPE.get())
            .create(0, 0,
                getSmallButtonWidth(), getButtonHeight(),
                Component.translatable(DDT_KEY), this::onDominentDrumTypeChanged
            );
        rowHelper.addChild(dominentDrumType);
    }

    protected void onDominentDrumTypeChanged(final CycleButton<DominentDrumType> button, final DominentDrumType value) {
        ModClientConfigs.DOMINENT_DRUM_TYPE.set(value);
    }

}
