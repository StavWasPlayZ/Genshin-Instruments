package com.cstav.genshinstrument.client.gui.screen.instrument.ukelele;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.MidiOptionsScreen;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.GridLayout.RowHelper;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class UkuleleMidiOptionsScreen extends MidiOptionsScreen {

    public UkuleleMidiOptionsScreen(Component pTitle, Screen prevScreen, Optional<InstrumentScreen> instrumentScreen) {
        super(pTitle, prevScreen, instrumentScreen);
    }

    private UkuleleScreen screen() {
        return (UkuleleScreen) instrumentScreen.get();
    }

    
    @Override
    protected void initOptionsGrid(GridLayout grid, RowHelper rowHelper) {
        super.initOptionsGrid(grid, rowHelper);

        rowHelper.addChild(SpacerElement.height(15), 2);

        final CycleButton<Boolean> ukuleleOctave = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(screen().extend2ndOctave)
            .create(0, 0,
                getBigButtonWidth(), getButtonHeight(),
                Component.translatable("button.genshinstrument.ukulele_extend_2nd_octave"), this::onUkuleleOctaveChanged
            );
        rowHelper.addChild(ukuleleOctave, 2);
    }

    private void onUkuleleOctaveChanged(CycleButton<Boolean> button, Boolean value) {
        screen().extend2ndOctave = value;
        queueToSave("ukulele_extend_2nd_octave", () -> ModClientConfigs.UKULELE_EXTEND_2ND_OCTAVE.set(value));
    }

}
