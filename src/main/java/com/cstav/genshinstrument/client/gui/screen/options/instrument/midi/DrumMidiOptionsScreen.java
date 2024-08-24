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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class DrumMidiOptionsScreen extends MidiOptionsScreen {

    public DrumMidiOptionsScreen(Component pTitle, Screen prevScreen, Optional<InstrumentScreen> instrumentScreen) {
        super(pTitle, prevScreen, instrumentScreen);
    }
    
    @Override
    protected void initOptionsGrid(GridWidget grid, RowHelper rowHelper) {
        super.initOptionsGrid(grid, rowHelper);

        rowHelper.addChild(SpacerWidget.height(15), 2);

        final CycleButton<DominantDrumType> dominantDrumType = CycleButton.<DominantDrumType>builder((type) ->
            Component.translatable(type.getKey())
        )
            .withValues(DominantDrumType.values())
            .withTooltip(tooltip((type) -> Component.translatable(type.getDescKey())))
            .withInitialValue(ModClientConfigs.DOMINANT_DRUM_TYPE.get())
            .create(0, 0,
                getSmallButtonWidth(), getButtonHeight(),
                Component.translatable(DominantDrumType.DDT_KEY), this::onDominantDrumTypeChanged
            );
        rowHelper.addChild(dominantDrumType);
    }

    protected void onDominantDrumTypeChanged(final CycleButton<DominantDrumType> button, final DominantDrumType value) {
        ModClientConfigs.DOMINANT_DRUM_TYPE.set(value);
    }

}
