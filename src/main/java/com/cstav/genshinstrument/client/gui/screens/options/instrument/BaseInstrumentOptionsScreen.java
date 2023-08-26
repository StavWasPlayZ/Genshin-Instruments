package com.cstav.genshinstrument.client.gui.screens.options.instrument;

import java.awt.Color;
import java.text.DecimalFormat;

import javax.annotation.Nullable;

import com.cstav.genshinstrument.client.ClientUtil;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.config.enumType.InstrumentChannelType;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.INoteLabel;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.AbstractGridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.options.MidiOptionsScreen;
import com.cstav.genshinstrument.client.gui.screens.options.ModOptionsScreen;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.util.LabelUtil;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.GridLayout.RowHelper;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.LinearLayout.Orientation;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class BaseInstrumentOptionsScreen extends ModOptionsScreen {
    public static final MutableComponent MIDI_OPTIONS = Component.translatable("label.genshinstrument.midiOptions");

    private static final String SOUND_CHANNEL_KEY = "button.genshinstrument.audioChannels",
        STOP_MUSIC_KEY = "button.genshinstrument.stop_music_on_play";


    public abstract INoteLabel[] getLabels();
    /**
     * @return The current note label for this instrument's notes
     */
    public abstract INoteLabel getCurrentLabel();
    

    protected final Screen lastScreen;

    protected final @Nullable INoteLabel[] labels;
    protected @Nullable INoteLabel currLabel;

    /**
     * Override to {@code false} tp disable the pitch slider from the options.
     * @apiNote SSTI-type instruments do not want a pitch slider. They tend to max out from beginning to end.
     */
    public boolean isPitchSliderEnabled() {
        return true;
    }


    public BaseInstrumentOptionsScreen(@Nullable AbstractInstrumentScreen screen) {
        super(Component.translatable("button.genshinstrument.instrumentOptions"), screen);
        
        lastScreen = null;
        labels = getLabels();
    }
    public BaseInstrumentOptionsScreen(final Screen lastScreen) {
        super(Component.translatable("button.genshinstrument.instrumentOptions"), null);
        
        this.lastScreen = lastScreen;
        labels = getLabels();
    }

    @Override
    protected void init() {
        currLabel = getCurrentLabel();

        final GridLayout grid = ClientUtil.createSettingsGrid();

        initOptionsGrid(grid, grid.createRowHelper(2));
        grid.arrangeElements();

        ClientUtil.alignGrid(grid, width, height);
        grid.visitWidgets(this::addRenderableWidget);


        final int buttonsY = ClientUtil.lowerButtonsY(grid.getY(), grid.getHeight(), height);

        final Button doneBtn = Button.builder(CommonComponents.GUI_DONE, (btn) -> onClose())
            .width(150)
            .build();

        // Add MIDI options button for MIDI instruments
        if (!isOverlay || instrumentScreen.isMidiInstrument()) {
            final LinearLayout buttonLayout = new LinearLayout(
                grid.getX() + 40, buttonsY,
                getBigButtonWidth() - 80, getButtonHeight(),
                Orientation.HORIZONTAL
            );

            final Button midiOptions = Button.builder(MIDI_OPTIONS.copy().append("..."), (btn) -> openMidiOptions())
                .width(150)
                .build();
    
            buttonLayout.addChild(midiOptions);
            buttonLayout.addChild(doneBtn);

            buttonLayout.arrangeElements();
            buttonLayout.visitWidgets(this::addRenderableWidget);
        } else {
            doneBtn.setPosition((width - doneBtn.getWidth())/2, buttonsY);
            addRenderableWidget(doneBtn);
        }
            
    }

    protected void initAudioSection(final GridLayout grid, final RowHelper rowHelper) {
        final CycleButton<InstrumentChannelType> instrumentChannel = CycleButton.<InstrumentChannelType>builder((soundType) ->
            Component.translatable(SOUND_CHANNEL_KEY +"."+ soundType.toString().toLowerCase())
        )
            .withValues(InstrumentChannelType.values())
            .withInitialValue(ModClientConfigs.CHANNEL_TYPE.get())

            .withTooltip((soundType) -> Tooltip.create(switch (soundType) {
                case MIXED -> translatableArgs(SOUND_CHANNEL_KEY+".mixed.tooltip", NoteSound.STEREO_RANGE);
                case STEREO -> Component.translatable(SOUND_CHANNEL_KEY+".stereo.tooltip");
                default -> CommonComponents.EMPTY;
            }))
            .create(0, 0,
                getBigButtonWidth(), 20, Component.translatable(SOUND_CHANNEL_KEY), this::onChannelTypeChanged);
        rowHelper.addChild(instrumentChannel, 2);

        if (isPitchSliderEnabled()) {
            final AbstractSliderButton pitchSlider = new AbstractSliderButton(0, 0, getSmallButtonWidth(), 20,
                CommonComponents.EMPTY,
                Mth.clampedMap(getPitch(), NoteSound.MIN_PITCH, NoteSound.MAX_PITCH, 0, 1)) {
    
                final DecimalFormat format = new DecimalFormat("0.00");
                {
                    pitch = getPitch();
                    updateMessage();
                }
    
                private int pitch;
    
                @Override
                protected void updateMessage() {
                    this.setMessage(
                        Component.translatable("button.genshinstrument.pitch").append(": "
                            + LabelUtil.getNoteName(pitch, AbstractGridInstrumentScreen.NOTE_LAYOUT, 0)
                            + " ("+format.format(NoteSound.getPitchByNoteOffset(pitch))+")"
                        )
                    );
                }
                
                @Override
                protected void applyValue() {
                    pitch = (int)Mth.clampedLerp(NoteSound.MIN_PITCH, NoteSound.MAX_PITCH, value);
                    onPitchChanged(this, pitch);
                }
            };
            rowHelper.addChild(pitchSlider);
        }

        final CycleButton<Boolean> stopMusic = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(ModClientConfigs.STOP_MUSIC_ON_PLAY.get())
            .withTooltip((value) -> Tooltip.create(Component.translatable(STOP_MUSIC_KEY+".tooltip", NoteSound.STOP_SOUND_DISTANCE)))
            .create(0, 0,
                getSmallButtonWidth(), getButtonHeight(),
                Component.translatable(STOP_MUSIC_KEY), this::onMusicStopChanged
            );
        rowHelper.addChild(stopMusic);
    }

    protected void initVisualsSection(final GridLayout grid, final RowHelper rowHelper) {

        final CycleButton<Boolean> emitRing = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(ModClientConfigs.EMIT_RING_ANIMATION.get())
            .create(0, 0,
                getSmallButtonWidth(), getButtonHeight(),
                Component.translatable("button.genshinstrument.emit_ring"), this::onEmitRingChanged
            );
        rowHelper.addChild(emitRing);

        final CycleButton<Boolean> sharedInstrument = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(ModClientConfigs.SHARED_INSTRUMENT.get())
            .withTooltip((value) -> Tooltip.create(Component.translatable("button.genshinstrument.shared_instrument.tooltip")))
            .create(0, 0,
                getSmallButtonWidth(), getButtonHeight(),
                Component.translatable("button.genshinstrument.shared_instrument"), this::onSharedInstrumentChanged
            );
        rowHelper.addChild(sharedInstrument);

        final CycleButton<Boolean> accurateNotes = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(ModClientConfigs.ACCURATE_NOTES.get())
            .withTooltip((value) -> Tooltip.create(Component.translatable("button.genshinstrument.accurate_notes.tooltip")))
            .create(0, 0,
                getSmallButtonWidth(), getButtonHeight(),
                Component.translatable("button.genshinstrument.accurate_notes"), this::onAccurateNotesChanged
            );
        rowHelper.addChild(accurateNotes);


        if (labels != null) {
            final CycleButton<INoteLabel> labelType = CycleButton.<INoteLabel>builder((label) -> Component.translatable(label.getKey()))
                .withValues(labels)
                .withInitialValue(currLabel)
                .create(0, 0,
                    getBigButtonWidth(), getButtonHeight(),
                    Component.translatable("button.genshinstrument.label"), this::onLabelChanged
                );
            rowHelper.addChild(labelType, 2);
        }
    }


    /**
     * Fills the settings grid with all the necessary widgets, buttons and such
     * @param grid The settings grid to add the widgets to
     * @param rowHelper A row helper for the specified {@code grid}
     */
    protected void initOptionsGrid(final GridLayout grid, final RowHelper rowHelper) {
        initAudioSection(grid, rowHelper);

        rowHelper.addChild(SpacerElement.height(7), 2);
        
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
            // This is a double slide, hence conversions to int would make
            // this method be called for no reason
            if (instrumentScreen.getPitch() == pitch)
                return;

            // Directly save the pitch if we're on an instrument
            // Otherwise tranpositions will reset to their previous pitch
            instrumentScreen.setPitch(pitch);
            savePitch(pitch);
        } else
            queueToSave("pitch", () -> savePitch(pitch));
    }
    protected void savePitch(final int newPitch) {
        ModClientConfigs.PITCH.set(newPitch);
    }

    // These values derive from the config directly, so just update them on-spot
    protected void onChannelTypeChanged(CycleButton<InstrumentChannelType> button, InstrumentChannelType type) {
        ModClientConfigs.CHANNEL_TYPE.set(type);
    }
    protected void onMusicStopChanged(final CycleButton<Boolean> button, final boolean value) {
        ModClientConfigs.STOP_MUSIC_ON_PLAY.set(value);
    }
    protected void onEmitRingChanged(final CycleButton<Boolean> button, final boolean value) {
        ModClientConfigs.EMIT_RING_ANIMATION.set(value);
    }
    protected void onSharedInstrumentChanged(final CycleButton<Boolean> button, final boolean value) {
        ModClientConfigs.SHARED_INSTRUMENT.set(value);
    }
    protected void onAccurateNotesChanged(final CycleButton<Boolean> button, final boolean value) {
        ModClientConfigs.ACCURATE_NOTES.set(value);

        if (isOverlay)
            instrumentScreen.notesIterable().forEach(NoteButton::updateNoteLabel);
    }



    @Override
    public void render(GuiGraphics gui, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(gui);
        
        gui.drawCenteredString(font, title, width/2, 15, Color.WHITE.getRGB());
        
        super.render(gui, pMouseX, pMouseY, pPartialTick);
    }


    protected void openMidiOptions() {
        minecraft.popGuiLayer();
        minecraft.pushGuiLayer(new MidiOptionsScreen(MIDI_OPTIONS, this, instrumentScreen));
    }


    @Override
    public void onClose() {
        onSave();
        
        super.onClose();
        if (isOverlay)
            instrumentScreen.onOptionsClose();
    }


    @Override
    public boolean isPauseScreen() {
        return instrumentScreen == null;
    }


    /**
     * Tooltip is being annoying and not rpelacing my args.
     * So, fine, I'll do it myself.
     * @param key The translation key
     * @param arg The thing to replace with %s
     * @return What should've been return by {@link Component#translatable(String, Object...)}
     */
    private static MutableComponent translatableArgs(final String key, final Object arg) {
        return Component.literal(
            Component.translatable(key).getString().replace("%s", arg.toString())
        );
    }
}