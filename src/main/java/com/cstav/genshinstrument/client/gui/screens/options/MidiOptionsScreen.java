package com.cstav.genshinstrument.client.gui.screens.options;

import com.cstav.genshinstrument.client.ClientUtil;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.ModOptionsScreen;
import com.cstav.genshinstrument.client.midi.MidiController;

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
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MidiOptionsScreen extends ModOptionsScreen {
    public static final int MIN_OCTAVE_SHIFT = -4, MAX_OCTAVE_SHIFT = 4;

    protected final Screen prevScreen;

    public MidiOptionsScreen(Component pTitle, Screen prevScreen, AbstractInstrumentScreen instrumentScreen) {
        super(pTitle, instrumentScreen);
        this.prevScreen = prevScreen;
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

    protected void initOptionsGrid(final GridLayout grid, final RowHelper rowHelper) {
        final CycleButton<Boolean> midiEnabled = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(ModClientConfigs.MIDI_ENABLED.get())
            .create(0, 0,
                getSmallButtonWidth(), getButtonHeight(),
                Component.translatable("button.genshinstrument.midiEnabled"), this::onMidiEnabledChanged
            );
        rowHelper.addChild(midiEnabled);

        if (!isOverlay || instrumentScreen.allowMidiOverflow()) {
            final CycleButton<Boolean> extendOctaves = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
                .withInitialValue(ModClientConfigs.EXTEND_OCTAVES.get())
                .withTooltip((value) -> Tooltip.create(Component.translatable("button.genshinstrument.extendOctaves.tooltip")))
                .create(0, 0,
                    getSmallButtonWidth(), getButtonHeight(),
                    Component.translatable("button.genshinstrument.extendOctaves"), this::onExtendOctavesChanged
                );
            rowHelper.addChild(extendOctaves);
        }


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


        rowHelper.addChild(SpacerElement.height(7), 2);

        final AbstractSliderButton octaveShift = new AbstractSliderButton(0, 0, getBigButtonWidth(), 20,
            CommonComponents.EMPTY,
            Mth.clampedMap(ModClientConfigs.OCTAVE_SHIFT.get(), MIN_OCTAVE_SHIFT, MAX_OCTAVE_SHIFT, 0, 1)) {

            {
                updateMessage();
            }

            @Override
            protected void updateMessage() {
                this.setMessage(
                    Component.translatable("button.genshinstrument.midiOctaveShift").append(": "
                        + ModClientConfigs.OCTAVE_SHIFT.get()
                    )
                );
            }
            
            @Override
            protected void applyValue() {
                onOctaveShiftChanged(this, (int)Mth.clampedLerp(MIN_OCTAVE_SHIFT, MAX_OCTAVE_SHIFT, value));
            }
        };
        rowHelper.addChild(octaveShift, 2);
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
    

    @Override
    public boolean isPauseScreen() {
        return prevScreen == null;
    }

    @Override
    public void onClose() {
        onSave();

        super.onClose();
        if (prevScreen != null)
            minecraft.pushGuiLayer(prevScreen);
    }

}
