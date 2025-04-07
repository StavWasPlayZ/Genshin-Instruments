package com.cstav.genshinstrument.client.gui.screen.instrument.ukelele;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.SingleButtonOptionsScreen;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UkuleleOptionsScreen extends SingleButtonOptionsScreen {

    public UkuleleOptionsScreen(final UkuleleScreen screen) {
        super(screen);
    }

    private UkuleleScreen screen() {
        return (UkuleleScreen) instrumentScreen.get();
    }

    @Override
    protected String optionsLabelKey() {
        return "label.genshinstrument.ukulele_options";
    }


    @Override
    protected AbstractButton constructButton() {
        return CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(screen().extend2ndOctave)
            .create(0, 0,
                getBigButtonWidth(), getButtonHeight(),
                Component.translatable("button.genshinstrument.ukulele_extend_2nd_octave"), this::onUkuleleOctaveChanged
            );
    }

    private void onUkuleleOctaveChanged(CycleButton<Boolean> button, Boolean value) {
        screen().extend2ndOctave = value;
        queueToSave("ukulele_extend_2nd_octave", () -> ModClientConfigs.UKULELE_EXTEND_2ND_OCTAVE.set(value));
    }
}