package com.cstav.genshinstrument.client.gui.screen.instrument.ukelele;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.SingleButtonOptionsScreen;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.CycleButton;
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
        return CycleButton.<Ukulele3rdOctaveType>builder((value) ->
            Component.translatable(value.key)
        )
            .withValues(Ukulele3rdOctaveType.values())
            .withInitialValue(screen().octaveType)
            .withTooltip((value) -> {
                if (value == Ukulele3rdOctaveType.TREBLE) {
                    final Component text = Component.translatable(value.key + ".tooltip");
                    return minecraft.font.split(text, 200);
                }

                return null;
            })
            .create(0, 0,
                getBigButtonWidth(), getButtonHeight(),
                Component.translatable("button.genshinstrument.ukulele_3rd_octave"), this::onUkuleleOctaveChanged
            );
    }

    private void onUkuleleOctaveChanged(CycleButton<Ukulele3rdOctaveType> button, Ukulele3rdOctaveType value) {
        screen().octaveType = value;
        queueToSave("ukulele_3rd_octave_type", () -> ModClientConfigs.UKULELE_3RD_OCTAVE_TYPE.set(value));
    }
}