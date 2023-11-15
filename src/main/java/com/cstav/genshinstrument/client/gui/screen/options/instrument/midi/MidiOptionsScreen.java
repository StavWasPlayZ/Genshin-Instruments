package com.cstav.genshinstrument.client.gui.screen.options.instrument.midi;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.AbstractInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.gui.widget.SliderButton;
import com.cstav.genshinstrument.client.midi.MidiController;
import com.cstav.genshinstrument.client.util.ClientUtil;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.GridLayout.RowHelper;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MidiOptionsScreen extends AbstractInstrumentOptionsScreen {
    public static final int
        MIN_OCTAVE_SHIFT = -5, MAX_OCTAVE_SHIFT = 5,
        MIN_MIDI_CHANNEL = 0, MAX_MIDI_CHANNEL = 15
    ;

    public MidiOptionsScreen(Component pTitle, Screen prevScreen, InstrumentScreen instrumentScreen) {
        super(pTitle, instrumentScreen, prevScreen);
    }


    @Override
    protected void init() {

        final GridLayout grid = new GridLayout();
        grid.defaultCellSetting()
            .padding(ClientUtil.GRID_HORZ_PADDING, ClientUtil.GRID_VERT_PADDING)
            .alignVertically(.5f)
            .alignHorizontallyCenter();

        initOptionsGrid(grid, grid.createRowHelper(2));
        ClientUtil.alignGrid(grid, width, height);
        grid.visitWidgets(this::addRenderableWidget);
        
        
        final Button doneBtn = Button.builder(CommonComponents.GUI_DONE, (btn) -> onClose())
            .width(150)
            .pos((width - 150)/2, ClientUtil.lowerButtonsY(grid.getY(), grid.getHeight(), height))
            .build();

        addRenderableWidget(doneBtn);

    }


    protected void initDeviceSection(final GridLayout grid, final RowHelper rowHelper) {
        final CycleButton<Boolean> midiEnabled = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(ModClientConfigs.MIDI_ENABLED.get())
            .create(0, 0,
                getSmallButtonWidth(), getButtonHeight(),
                Component.translatable("button.genshinstrument.midiEnabled"), this::onMidiEnabledChanged
            );
        rowHelper.addChild(midiEnabled);

        final CycleButton<Boolean> fixedTouch = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(ModClientConfigs.FIXED_TOUCH.get())
            .withTooltip((value) -> Tooltip.create(Component.translatable("button.genshinstrument.fixedTouch.tooltip")))
            .create(0, 0,
                getSmallButtonWidth(), getButtonHeight(),
                Component.translatable("button.genshinstrument.fixedTouch"), this::onFixedTouchChanged
            );
        rowHelper.addChild(fixedTouch);


        MidiController.reloadDevices();

        final CycleButton<Integer> midiDevice = CycleButton.<Integer>builder((value) -> {
                if (value == -1)
                    return Component.translatable("button.none");

                return Component.literal(
                    MidiController.infoAsString(MidiController.getInfoFromIndex(value))
                );
            })
                .withValues(MidiController.getValuesForOption())
                .withInitialValue(ModClientConfigs.MIDI_DEVICE_INDEX.get())
                .create(0, 0,
                    getBigButtonWidth(), getButtonHeight(),
                    Component.translatable("button.genshinstrument.midiDevice"), this::onMidiDeviceChanged
                );
        rowHelper.addChild(midiDevice, 2);
    }
        
    protected void initThatOtherSection(final GridLayout grid, final RowHelper rowHelper) {
        final boolean canInstrumentOverflow = !isOverlay || instrumentScreen.midiReceiver.allowMidiOverflow();

        if (canInstrumentOverflow) {
            final CycleButton<Boolean> extendOctaves = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
                .withInitialValue(ModClientConfigs.EXTEND_OCTAVES.get())
                .withTooltip((value) -> Tooltip.create(Component.translatable("button.genshinstrument.extendOctaves.tooltip")))
                .create(0, 0,
                    getSmallButtonWidth(), getButtonHeight(),
                    Component.translatable("button.genshinstrument.extendOctaves"), this::onExtendOctavesChanged
                );
            rowHelper.addChild(extendOctaves);
        }

        final SliderButton octaveShift = new SliderButton(
            canInstrumentOverflow ? getSmallButtonWidth() : getBigButtonWidth(),
            ModClientConfigs.OCTAVE_SHIFT.get(), MIN_OCTAVE_SHIFT, MAX_OCTAVE_SHIFT) {

                @Override
                public Component getMessage() {
                    return Component.translatable("button.genshinstrument.midiOctaveShift").append(": "
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
                    return Component.translatable("button.genshinstrument.midiChannel").append(": "
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
                Component.translatable("button.genshinstrument.acceptAllChannels"), (btn, val) -> {
                    onAcceptAllChannelsChanged(btn, val);
                    midiChannel.active = !val;
                }
        );

        midiChannel.active = !acceptAllChannels.getValue();

        rowHelper.addChild(acceptAllChannels);
        rowHelper.addChild(midiChannel);
    }


    protected void initOptionsGrid(final GridLayout grid, final RowHelper rowHelper) {
        initDeviceSection(grid, rowHelper);

        rowHelper.addChild(SpacerElement.height(7), 2);

        initThatOtherSection(grid, rowHelper);
    }


    
    protected void onMidiEnabledChanged(final CycleButton<Boolean> button, final boolean value) {
        if (!value)
            MidiController.unloadDevice();
        else
            MidiController.openForListen();

        ModClientConfigs.MIDI_ENABLED.set(value);
    }

    protected void onMidiDeviceChanged(final CycleButton<Integer> button, final int value) {
        if (value == -1)
            MidiController.unloadDevice();
        else {
            MidiController.loadDevice(value);
            if (ModClientConfigs.MIDI_ENABLED.get())
                MidiController.openForListen();
        }
        
        queueToSave("midi_device_index", () -> saveMidiDeviceIndex(value));
    }
    protected void saveMidiDeviceIndex(final int index) {
        ModClientConfigs.MIDI_DEVICE_INDEX.set(index);
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
    protected void onAcceptAllChannelsChanged(final CycleButton<Boolean> button, final boolean value) {
        ModClientConfigs.ACCEPT_ALL_CHANNELS.set(value);
    }
    protected void onMidiChannelChanged(final AbstractSliderButton button, final int value) {
        if (ModClientConfigs.MIDI_CHANNEL.get() != value)
            ModClientConfigs.MIDI_CHANNEL.set(value);
    }
}
