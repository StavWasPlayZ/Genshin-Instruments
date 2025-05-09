package com.cstav.genshinstrument.client.gui.screen.instrument.gloriousdrum;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.MidiOptionsScreen;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.GridLayout.RowHelper;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class GloriousDrumMidiOptionsScreen extends MidiOptionsScreen {

    public GloriousDrumMidiOptionsScreen(Component pTitle, Screen prevScreen, Optional<InstrumentScreen> instrumentScreen) {
        super(pTitle, prevScreen, instrumentScreen);
    }
    
    @Override
    protected void initOptionsGrid(GridLayout grid, RowHelper rowHelper) {
        super.initOptionsGrid(grid, rowHelper);

        rowHelper.addChild(SpacerElement.height(15), 2);

        final CycleButton<DominantGloriousDrumType> dominantDrumType = CycleButton.<DominantGloriousDrumType>builder((type) ->
            Component.translatable(type.getKey())
        )
            .withValues(DominantGloriousDrumType.values())
            .withTooltip((type) -> Tooltip.create(Component.translatable(type.getDescKey())))
            .withInitialValue(ModClientConfigs.DOMINANT_DRUM_TYPE.get())
            .create(0, 0,
                getBigButtonWidth(), getButtonHeight(),
                Component.translatable(DominantGloriousDrumType.DDT_KEY), this::onDominantDrumTypeChanged
            );
        rowHelper.addChild(dominantDrumType, 2);
    }

    protected void onDominantDrumTypeChanged(final CycleButton<DominantGloriousDrumType> button, final DominantGloriousDrumType value) {
        ModClientConfigs.DOMINANT_DRUM_TYPE.set(value);
    }

}
