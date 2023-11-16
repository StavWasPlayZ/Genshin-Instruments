package com.cstav.genshinstrument.client.gui.screen.options.instrument;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.vintagelyre.VintageLyreScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.SingleButtonOptionsScreen;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;

public class VintageLyreOptionsScreen extends SingleButtonOptionsScreen {

    public VintageLyreOptionsScreen(final VintageLyreScreen screen) {
        super(screen);
    }

    @Override
    protected String optionsLabelKey() {
        return "label.genshinstrument.vintage_lyre_options";
    }


    @Override
    protected AbstractButton constructButton() {
        return CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(ModClientConfigs.NORMALIZE_VINTAGE_LYRE.get())
            .withTooltip(tooltip((value) -> new TranslatableComponent("button.genshinstrument.normalize_vintage_lyre.tooltip")))
            .create(0, 0,
                getBigButtonWidth(), getButtonHeight(),
                new TranslatableComponent("button.genshinstrument.normalize_vintage_lyre"), this::onNormalizeLyreChanged
            );
    }

    private void onNormalizeLyreChanged(CycleButton<Boolean> button, Boolean value) {
        ModClientConfigs.NORMALIZE_VINTAGE_LYRE.set(value);
    }
}