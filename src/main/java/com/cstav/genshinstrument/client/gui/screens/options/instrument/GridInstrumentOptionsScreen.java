package com.cstav.genshinstrument.client.gui.screens.options.instrument;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.config.enumType.label.NoteGridLabel;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.AbstractGridInstrumentScreen;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.GridWidget;
import net.minecraft.client.gui.components.GridWidget.RowHelper;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GridInstrumentOptionsScreen extends AbstractInstrumentOptionsScreen {

    public GridInstrumentOptionsScreen(final AbstractGridInstrumentScreen screen) {
        super(screen);
    }


    @Override
    public NoteGridLabel[] getLabels() {
        return NoteGridLabel.values();
    }
    @Override
    public NoteGridLabel getCurrentLabel() {
        return ModClientConfigs.GRID_LABEL_TYPE.get();
    }


    @Override
    protected void initVisualsSection(GridWidget grid, RowHelper rowHelper) {
        final CycleButton<Boolean> renderBackground = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(ModClientConfigs.RENDER_BACKGROUND.get())
            .create(0, 0,
                getSmallButtonWidth(), getButtonHeight(),
                Component.translatable("button.genshinstrument.render_background"), this::onRenderBackgroundChanged
            );
        rowHelper.addChild(renderBackground);

        super.initVisualsSection(grid, rowHelper);
    }

    protected void onRenderBackgroundChanged(final CycleButton<Boolean> button, final boolean value) {
        ModClientConfigs.RENDER_BACKGROUND.set(value);
    }
    
}
