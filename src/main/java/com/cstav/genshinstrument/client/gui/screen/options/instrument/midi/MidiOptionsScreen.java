package com.cstav.genshinstrument.client.gui.screen.options.instrument.midi;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.AbstractInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.gui.widget.SliderButton;
import com.cstav.genshinstrument.client.gui.widget.copied.GridWidget;
import com.cstav.genshinstrument.client.gui.widget.copied.GridWidget.RowHelper;
import com.cstav.genshinstrument.client.gui.widget.copied.SpacerWidget;
import com.cstav.genshinstrument.client.midi.MidiController;
import com.cstav.genshinstrument.client.util.ClientUtil;
import com.cstav.genshinstrument.util.CommonUtil;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@OnlyIn(Dist.CLIENT)
public class MidiOptionsScreen extends AbstractInstrumentOptionsScreen {
    public static final int
        MIN_OCTAVE_SHIFT = -5, MAX_OCTAVE_SHIFT = 5,
        MIN_MIDI_CHANNEL = 0, MAX_MIDI_CHANNEL = 15
    ;

    public MidiOptionsScreen(Component pTitle, Screen prevScreen, InstrumentScreen instrumentScreen) {
        super(pTitle, instrumentScreen, prevScreen);
    }
    public MidiOptionsScreen(Component pTitle, Screen prevScreen, Optional<InstrumentScreen> instrumentScreen) {
        super(pTitle, instrumentScreen, prevScreen);
    }


    @Override
    protected void init() {

        final GridWidget grid = new GridWidget();
        grid.defaultCellSetting()
            .padding(ClientUtil.GRID_HORZ_PADDING, ClientUtil.GRID_VERT_PADDING)
            .alignVertically(.5f)
            .alignHorizontallyCenter();

        initOptionsGrid(grid, grid.createRowHelper(2));
        ClientUtil.alignGrid(grid, width, height);
        addRenderableWidget(grid);
        
        
        final Button doneBtn = new Button(
            (width - 150)/2, ClientUtil.lowerButtonsY(grid.y, grid.getHeight(), height),
            150, getButtonHeight(),
            CommonComponents.GUI_DONE, (btn) -> onClose()
        );
        addRenderableWidget(doneBtn);

    }


    protected void initDeviceSection(final GridWidget grid, final RowHelper rowHelper) {
        final CycleButton<Boolean> midiEnabled = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(ModClientConfigs.MIDI_ENABLED.get())
            .create(0, 0,
                getBigButtonWidth(), getButtonHeight(),
                new TranslatableComponent("button.genshinstrument.midiEnabled"), this::onMidiEnabledChanged
            );
        rowHelper.addChild(midiEnabled, 2);

        final SliderButton inputSensitivity = new SliderButton(getSmallButtonWidth(),
                ModClientConfigs.MIDI_IN_SENSITIVITY.get(), 0, 1) {

            private static final DecimalFormat D_FORMAT = new DecimalFormat("0.0");

            @Override
            public Component getMessage() {
                return new TranslatableComponent("button.genshinstrument.inputSensitivity").append(": "
                    + D_FORMAT.format(ModClientConfigs.MIDI_IN_SENSITIVITY.get() * 100)+"%"
                );
            }

            @Override
            protected void applyValue() {
                onMidiSensitivityChanged(this, value);
            }
        };

        final CycleButton<Boolean> fixedTouch = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(ModClientConfigs.FIXED_TOUCH.get())
            .withTooltip(tooltip((value) -> new TranslatableComponent("button.genshinstrument.fixedTouch.tooltip")))
            .create(0, 0,
                getSmallButtonWidth(), getButtonHeight(),
                new TranslatableComponent("button.genshinstrument.fixedTouch"), (btn, val) -> {
                    onFixedTouchChanged(btn, val);
                    inputSensitivity.active = !val;
                }
            );

        inputSensitivity.active = !ModClientConfigs.FIXED_TOUCH.get();

        rowHelper.addChild(fixedTouch);
        rowHelper.addChild(inputSensitivity);

        MidiController.reloadDevices();

        final CycleButton<Integer> midiDevice = CycleButton.<Integer>builder((value) -> {
                if (value == -1)
                    return new TranslatableComponent("button.none");

                return new TranslatableComponent(
                    MidiController.infoAsString(MidiController.getInfoFromIndex(value))
                );
            })
                .withValues(getMidiDevicesRange())
                .withInitialValue(ModClientConfigs.MIDI_DEVICE_INDEX.get())
                .create(0, 0,
                    getBigButtonWidth(), getButtonHeight(),
                    new TranslatableComponent("button.genshinstrument.midiDevice"), this::onMidiDeviceChanged
                );
        rowHelper.addChild(midiDevice, 2);
    }
        
    protected void initThatOtherSection(final GridWidget grid, final RowHelper rowHelper) {
        final boolean canInstrumentOverflow = instrumentScreen.map((screen) -> screen.midiReceiver.allowMidiOverflow())
            .orElse(false);

        if (canInstrumentOverflow) {
            final CycleButton<Boolean> extendOctaves = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
                .withInitialValue(ModClientConfigs.EXTEND_OCTAVES.get())
                .withTooltip(tooltip((value) -> new TranslatableComponent("button.genshinstrument.extendOctaves.tooltip")))
                .create(0, 0,
                    getSmallButtonWidth(), getButtonHeight(),
                    new TranslatableComponent("button.genshinstrument.extendOctaves"), this::onExtendOctavesChanged
                );
            rowHelper.addChild(extendOctaves);
        }

        final SliderButton octaveShift = new SliderButton(
            canInstrumentOverflow ? getSmallButtonWidth() : getBigButtonWidth(),
            ModClientConfigs.OCTAVE_SHIFT.get(), MIN_OCTAVE_SHIFT, MAX_OCTAVE_SHIFT) {

                @Override
                public Component getMessage() {
                    return new TranslatableComponent("button.genshinstrument.midiOctaveShift").append(": "
                        + ModClientConfigs.OCTAVE_SHIFT.get()
                    );
                }
                
                @Override
                protected void applyValue() {
                    onOctaveShiftChanged(this, (int) getValueClamped());
                }
        };
        rowHelper.addChild(octaveShift, canInstrumentOverflow ? 1 : 2);


        final SliderButton midiChannel = new SliderButton(getSmallButtonWidth(),
            ModClientConfigs.MIDI_CHANNEL.get(), MIN_MIDI_CHANNEL, MAX_MIDI_CHANNEL) {

                @Override
                public Component getMessage() {
                    return new TranslatableComponent("button.genshinstrument.midiChannel").append(": "
                        + ModClientConfigs.MIDI_CHANNEL.get()
                    );
                }
                
                @Override
                protected void applyValue() {
                    onMidiChannelChanged(this, (int) getValueClamped());
                }
        };

        final CycleButton<Boolean> acceptAllChannels = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(ModClientConfigs.ACCEPT_ALL_CHANNELS.get())
            .create(0, 0,
                getSmallButtonWidth(), getButtonHeight(),
                new TranslatableComponent("button.genshinstrument.acceptAllChannels"), (btn, val) -> {
                    onAcceptAllChannelsChanged(btn, val);
                    midiChannel.active = !val;
                }
        );

        midiChannel.active = !acceptAllChannels.getValue();

        rowHelper.addChild(acceptAllChannels);
        rowHelper.addChild(midiChannel);
    }


    protected void initOptionsGrid(final GridWidget grid, final RowHelper rowHelper) {
        initDeviceSection(grid, rowHelper);

        rowHelper.addChild(SpacerWidget.height(7), 2);

        initThatOtherSection(grid, rowHelper);
    }

    /**
     * @return A list of available MIDI devices by their indexes, including -1 for None
     */
    public static List<Integer> getMidiDevicesRange() {
        return IntStream.range(-1, MidiController.DEVICES.size())
            .boxed().toList();
    }


    
    protected void onMidiEnabledChanged(final CycleButton<Boolean> button, final boolean value) {
        ModClientConfigs.MIDI_ENABLED.set(value);
        MidiController.loadByConfigs();
    }

    protected void onMidiDeviceChanged(final CycleButton<Integer> button, final int value) {
        ModClientConfigs.MIDI_DEVICE_INDEX.set(value);
        MidiController.loadByConfigs();
    }

    
    protected void onExtendOctavesChanged(final CycleButton<Boolean> button, final boolean value) {
        ModClientConfigs.EXTEND_OCTAVES.set(value);
    }
    protected void onOctaveShiftChanged(final AbstractSliderButton button, final int value) {
        if (ModClientConfigs.OCTAVE_SHIFT.get() != value)
            ModClientConfigs.OCTAVE_SHIFT.set(value);
    }

    protected void onFixedTouchChanged(final CycleButton<Boolean> button, final boolean value) {
        ModClientConfigs.FIXED_TOUCH.set(value);
    }
    protected void onMidiSensitivityChanged(final AbstractSliderButton button, double value) {
        value = CommonUtil.round(value, 3);

        if (ModClientConfigs.MIDI_IN_SENSITIVITY.get() != value)
            ModClientConfigs.MIDI_IN_SENSITIVITY.set(value);
    }
    protected void onAcceptAllChannelsChanged(final CycleButton<Boolean> button, final boolean value) {
        ModClientConfigs.ACCEPT_ALL_CHANNELS.set(value);
    }
    protected void onMidiChannelChanged(final AbstractSliderButton button, final int value) {
        if (ModClientConfigs.MIDI_CHANNEL.get() != value)
            ModClientConfigs.MIDI_CHANNEL.set(value);
    }
}
