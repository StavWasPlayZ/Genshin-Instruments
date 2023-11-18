package com.cstav.genshinstrument.client.gui.screen.options.instrument.partial;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.config.enumType.InstrumentChannelType;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.label.INoteLabel;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.GridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.midi.MidiOptionsScreen;
import com.cstav.genshinstrument.client.gui.widget.SliderButton;
import com.cstav.genshinstrument.client.gui.widget.copied.GridWidget;
import com.cstav.genshinstrument.client.gui.widget.copied.GridWidget.RowHelper;
import com.cstav.genshinstrument.client.gui.widget.copied.LinearLayoutWidget;
import com.cstav.genshinstrument.client.gui.widget.copied.LinearLayoutWidget.Orientation;
import com.cstav.genshinstrument.client.gui.widget.copied.SpacerWidget;
import com.cstav.genshinstrument.client.util.ClientUtil;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.util.LabelUtil;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.text.DecimalFormat;

@OnlyIn(Dist.CLIENT)
public abstract class InstrumentOptionsScreen extends AbstractInstrumentOptionsScreen {
    public static final MutableComponent MIDI_OPTIONS = new TranslatableComponent("label.genshinstrument.midiOptions");

    private static final String SOUND_CHANNEL_KEY = "button.genshinstrument.audioChannels",
        STOP_MUSIC_KEY = "button.genshinstrument.stop_music_on_play";


    public abstract INoteLabel[] getLabels();
    /**
     * @return The current note label for this instrument's notes
     */
    public abstract INoteLabel getCurrentLabel();
    

    protected final @Nullable INoteLabel[] labels;
    protected @Nullable INoteLabel currLabel;

    /**
     * Override to {@code false} tp disable the pitch slider from the options.
     * @apiNote SSTI-type instruments do not want a pitch slider. They tend to max out from beginning to end.
     */
    public boolean isPitchSliderEnabled() {
        return true;
    }


    public InstrumentOptionsScreen(@Nullable InstrumentScreen screen) {
        super(new TranslatableComponent("button.genshinstrument.instrumentOptions"), screen);
        labels = getLabels();
    }
    public InstrumentOptionsScreen(final Screen lastScreen) {
        super(new TranslatableComponent("button.genshinstrument.instrumentOptions"), lastScreen);
        labels = getLabels();
    }

    @Override
    protected void init() {
        currLabel = getCurrentLabel();

        final GridWidget grid = ClientUtil.createSettingsGrid();
        initOptionsGrid(grid, grid.createRowHelper(2));

        ClientUtil.alignGrid(grid, width, height);
        addRenderableWidget(grid);


        final int buttonsY = ClientUtil.lowerButtonsY(grid.y, grid.getHeight(), height);
        final int buttonsWidth = 150;

        final Button doneBtn = new Button(0, buttonsY,
            buttonsWidth, getButtonHeight(),
            CommonComponents.GUI_DONE, (btn) -> onClose()
        );

        // Add MIDI options button for MIDI instruments
        if (!isOverlay || instrumentScreen.isMidiInstrument()) {
            final LinearLayoutWidget buttonLayout = new LinearLayoutWidget(
                grid.x + getSmallButtonWidth() - buttonsWidth + ClientUtil.GRID_HORZ_PADDING, buttonsY,
                (buttonsWidth + ClientUtil.GRID_HORZ_PADDING) * 2, getButtonHeight(),
                Orientation.HORIZONTAL
            );

            final Button midiOptions = new Button(0, 0,
                buttonsWidth, getButtonHeight(),
                MIDI_OPTIONS.copy().append("..."), (btn) -> openMidiOptions()
            );

            buttonLayout.addChild(midiOptions);
            buttonLayout.addChild(doneBtn);

            buttonLayout.pack();
            addRenderableWidget(buttonLayout);
        } else {
            doneBtn.x = (width - doneBtn.getWidth())/2;
            addRenderableWidget(doneBtn);
        }

    }

    protected void initAudioSection(final GridWidget grid, final RowHelper rowHelper) {
        final CycleButton<InstrumentChannelType> instrumentChannel = CycleButton.<InstrumentChannelType>builder((soundType) ->
            new TranslatableComponent(SOUND_CHANNEL_KEY +"."+ soundType.toString().toLowerCase())
        )
            .withValues(InstrumentChannelType.values())
            .withInitialValue(ModClientConfigs.CHANNEL_TYPE.get())

            .withTooltip(tooltip((soundType) -> switch (soundType) {
                case MIXED -> translatableArgs(SOUND_CHANNEL_KEY+".mixed.tooltip", NoteSound.STEREO_RANGE);
                case STEREO -> new TranslatableComponent(SOUND_CHANNEL_KEY+".stereo.tooltip");
                default -> TextComponent.EMPTY;
            }))
            .create(0, 0,
                getBigButtonWidth(), 20, new TranslatableComponent(SOUND_CHANNEL_KEY), this::onChannelTypeChanged);
        rowHelper.addChild(instrumentChannel, 2);

        if (isPitchSliderEnabled()) {
            final SliderButton pitchSlider = new SliderButton(getSmallButtonWidth(),
                getPitch(), NoteSound.MIN_PITCH, NoteSound.MAX_PITCH) {
    
                private static final DecimalFormat D_FORMAT = new DecimalFormat("0.00");
                {
                    pitch = getPitch();
                }

    
                private int pitch;
    
                @Override
                public Component getMessage() {
                    return new TranslatableComponent("button.genshinstrument.pitch").append(": "
                        + LabelUtil.formatNoteName(
                            LabelUtil.getNoteName(pitch, GridInstrumentScreen.NOTE_LAYOUT, 0),
                            false
                        )
                        + " ("+D_FORMAT.format(NoteSound.getPitchByNoteOffset(pitch))+")"
                    );
                }

                @Override
                protected void applyValue() {
                    pitch = (int) getValueClamped();
                    onPitchChanged(this, pitch);
                }
            };
            rowHelper.addChild(pitchSlider);
        }

        final SliderButton volumeSlider = new SliderButton(getSmallButtonWidth(), ModClientConfigs.VOLUME.get(), 0, 1) {

            @Override
            public Component getMessage() {
                return new TranslatableComponent("button.genshinstrument.volume").append(": "
                    + ((int)(value * 100))+"%"
                );
            }
            
            @Override
            protected void applyValue() {
                onVolumeChanged(this, value);
            }
        };
        rowHelper.addChild(volumeSlider);

    }

    protected void initVisualsSection(final GridWidget grid, final RowHelper rowHelper) {

        // Not visual, but no space
        final CycleButton<Boolean> stopMusic = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(ModClientConfigs.STOP_MUSIC_ON_PLAY.get())
            .withTooltip(tooltip((value) -> new TranslatableComponent(STOP_MUSIC_KEY+".tooltip", NoteSound.STOP_SOUND_DISTANCE)))
            .create(0, 0,
                getSmallButtonWidth(), getButtonHeight(),
                new TranslatableComponent(STOP_MUSIC_KEY), this::onMusicStopChanged
            );
        rowHelper.addChild(stopMusic);

        final CycleButton<Boolean> sharedInstrument = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(ModClientConfigs.SHARED_INSTRUMENT.get())
            .withTooltip(tooltip((value) -> new TranslatableComponent("button.genshinstrument.shared_instrument.tooltip")))
            .create(0, 0,
                getSmallButtonWidth(), getButtonHeight(),
                new TranslatableComponent("button.genshinstrument.shared_instrument"), this::onSharedInstrumentChanged
            );
        rowHelper.addChild(sharedInstrument);

        final CycleButton<Boolean> accurateNotes = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(ModClientConfigs.ACCURATE_NOTES.get())
            .withTooltip(tooltip((value) -> new TranslatableComponent("button.genshinstrument.accurate_notes.tooltip")))
            .create(0, 0,
                getSmallButtonWidth(), getButtonHeight(),
                new TranslatableComponent("button.genshinstrument.accurate_notes"), this::onAccurateNotesChanged
            );
        rowHelper.addChild(accurateNotes);


        if (labels != null) {
            final CycleButton<INoteLabel> labelType = CycleButton.<INoteLabel>builder((label) -> new TranslatableComponent(label.getKey()))
                .withValues(labels)
                .withInitialValue(currLabel)
                .create(0, 0,
                    getBigButtonWidth(), getButtonHeight(),
                    new TranslatableComponent("button.genshinstrument.label"), this::onLabelChanged
                );
            rowHelper.addChild(labelType, 2);
        }
    }


    /**
     * Fills the settings grid with all the necessary widgets, buttons and such
     * @param grid The settings grid to add the widgets to
     * @param rowHelper A row helper for the specified {@code grid}
     */
    protected void initOptionsGrid(final GridWidget grid, final RowHelper rowHelper) {
        initAudioSection(grid, rowHelper);

        rowHelper.addChild(SpacerWidget.height(7), 2);
        
        initVisualsSection(grid, rowHelper);
    }

    private int getPitch() {
        return (isOverlay)
            ? instrumentScreen.getPitch()
            : ModClientConfigs.PITCH.get().intValue();
    }


    // Change handlers
    protected void onLabelChanged(final CycleButton<INoteLabel> button, final INoteLabel label) {
        if (isOverlay)
            instrumentScreen.notesIterable().forEach((note) -> note.setLabelSupplier(label.getLabelSupplier()));

        queueToSave("note_label", () -> saveLabel(label));
    }
    protected abstract void saveLabel(final INoteLabel newLabel);

    protected void onPitchChanged(final AbstractSliderButton slider, final int pitch) {
        if (isOverlay) {
            // This is a double slide, hence conversions to int would
            // make unnecessary calls
            if (instrumentScreen.getPitch() == pitch)
                return;

            // Directly save the pitch if we're on an instrument
            // Otherwise transpositions will reset to their previous pitch
            instrumentScreen.setPitch(pitch);
            savePitch(pitch);
        } else
            queueToSave("pitch", () -> savePitch(pitch));
    }
    protected void savePitch(final int newPitch) {
        ModClientConfigs.PITCH.set(newPitch);
    }

    protected void onVolumeChanged(final AbstractSliderButton slider, final double volume) {
        final int newVolume = (int)(volume * 100);

        if (isOverlay)
            instrumentScreen.volume = newVolume;

        queueToSave("volume", () -> saveVolume(newVolume / 100d));
    }
    protected void saveVolume(final double newVolume) {
        ModClientConfigs.VOLUME.set(newVolume);
    }

    // These values derive from the config directly, so just update them on-spot
    protected void onChannelTypeChanged(CycleButton<InstrumentChannelType> button, InstrumentChannelType type) {
        ModClientConfigs.CHANNEL_TYPE.set(type);
    }
    protected void onMusicStopChanged(final CycleButton<Boolean> button, final boolean value) {
        ModClientConfigs.STOP_MUSIC_ON_PLAY.set(value);
    }
    protected void onSharedInstrumentChanged(final CycleButton<Boolean> button, final boolean value) {
        ModClientConfigs.SHARED_INSTRUMENT.set(value);
    }
    protected void onAccurateNotesChanged(final CycleButton<Boolean> button, final boolean value) {
        ModClientConfigs.ACCURATE_NOTES.set(value);

        if (isOverlay)
            instrumentScreen.notesIterable().forEach(NoteButton::updateNoteLabel);
    }


    protected void openMidiOptions() {
        if (isOverlay) {
            minecraft.popGuiLayer();
            minecraft.pushGuiLayer(midiOptionsScreen());
        } else
            minecraft.setScreen(midiOptionsScreen());
    }

    protected MidiOptionsScreen midiOptionsScreen() {
        return new MidiOptionsScreen(MIDI_OPTIONS, this, instrumentScreen);
    }


    @Override
    public void onClose() {
        super.onClose();
        if (isOverlay)
            instrumentScreen.onOptionsClose();
    }


    /**
     * Tooltip is being annoying and not replacing my args.
     * So, fine, I'll do it myself.
     * @param key The translation key
     * @param arg The thing to replace with %s
     * @return What should've been return by {@link Component#translatable(String, Object...)}
     */
    private static MutableComponent translatableArgs(final String key, final Object arg) {
        return new TranslatableComponent(
            new TranslatableComponent(key).getString().replace("%s", arg.toString())
        );
    }
}