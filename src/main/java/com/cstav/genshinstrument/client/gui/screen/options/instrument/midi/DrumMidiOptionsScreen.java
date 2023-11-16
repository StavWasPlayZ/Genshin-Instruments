package com.cstav.genshinstrument.client.gui.screen.options.instrument.midi;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.drum.DominantDrumType;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.widget.copied.GridWidget;
import com.cstav.genshinstrument.client.gui.widget.copied.GridWidget.RowHelper;
import com.cstav.genshinstrument.client.gui.widget.copied.SpacerWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DrumMidiOptionsScreen extends MidiOptionsScreen {
    public static final String DDT_KEY = "button.genshinstrument.dominentDrumType";

    public DrumMidiOptionsScreen(Component pTitle, Screen prevScreen, InstrumentScreen instrumentScreen) {
        super(pTitle, prevScreen, instrumentScreen);
    }
    
    @Override
    protected void initOptionsGrid(GridWidget grid, RowHelper rowHelper) {
        super.initOptionsGrid(grid, rowHelper);

        rowHelper.addChild(SpacerWidget.height(15), 2);

        final CycleButton<DominantDrumType> dominentDrumType = CycleButton.<DominantDrumType>builder((type) -> new TranslatableComponent(type.getKey()))
            .withValues(DominantDrumType.values())
            .withTooltip(tooltip((type) -> new TranslatableComponent(DDT_KEY+"."+type.name().toLowerCase()+".tooltip")))
            .withInitialValue(ModClientConfigs.DOMINANT_DRUM_TYPE.get())
            .create(0, 0,
                getSmallButtonWidth(), getButtonHeight(),
                new TranslatableComponent(DDT_KEY), this::onDominentDrumTypeChanged
            );
        rowHelper.addChild(dominentDrumType);
    }

    protected void onDominentDrumTypeChanged(final CycleButton<DominantDrumType> button, final DominantDrumType value) {
        ModClientConfigs.DOMINANT_DRUM_TYPE.set(value);
    }

}
