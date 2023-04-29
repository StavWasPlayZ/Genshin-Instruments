package com.cstav.genshinstrument.client.gui.screens.options.instrument;

import java.util.HashMap;

import javax.annotation.Nullable;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.config.enumType.InstrumentChannelType;
import com.cstav.genshinstrument.client.config.enumType.label.NoteGridLabel;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.INoteLabel;
import com.cstav.genshinstrument.client.gui.screens.options.widget.BetterSlider;
import com.cstav.genshinstrument.sounds.NoteSound;
import com.cstav.genshinstrument.util.RGBColor;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.FrameWidget;
import net.minecraft.client.gui.components.GridWidget;
import net.minecraft.client.gui.components.GridWidget.RowHelper;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.widget.ForgeSlider;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractInstrumentOptionsScreen extends Screen {

    private static final String SOUND_CHANNEL_KEY = "button.genshinstrument.audioChannels",
        STOP_MUSIC_KEY = "button.genshinstrument.stop_music_on_play";

    protected final HashMap<String, Runnable> APPLIED_OPTIONS = new HashMap<>();
    protected void queueToSave(final String optionKey, final Runnable saveRunnable) {
        if (APPLIED_OPTIONS.containsKey(optionKey))
            APPLIED_OPTIONS.replace(optionKey, saveRunnable);
        else
            APPLIED_OPTIONS.put(optionKey, saveRunnable);
    }

    

    protected int getHorzPadding() {
        return 4;
    }
    protected int getVertPadding() {
        return 2;
    }

    protected int getSmallButtonWidth() {
        return 190;
    }
    protected int getBigButtonWidth() {
        return (getSmallButtonWidth() + getHorzPadding()) * 2;
    }
    protected int getButtonHeight() {
        return 20;
    }

    public abstract INoteLabel[] getLabels();
    /**
     * @return The current note label for this instrument's notes
     */
    public abstract INoteLabel getCurrentLabel();
    

    protected final Screen lastScreen;
    protected final boolean isOverlay;
    public boolean active;
    private Runnable onCloseRunnable;

    protected final @Nullable INoteLabel[] labels;
    protected final @Nullable INoteLabel currLabel;
    
    protected final @Nullable AbstractInstrumentScreen screen;

    public AbstractInstrumentOptionsScreen(@Nullable AbstractInstrumentScreen screen) {
        super(Component.translatable("button.genshinstrument.instrumentOptions"));
        
        this.isOverlay = true;
        active = false;
        this.screen = screen;
        lastScreen = null;

        labels = getLabels();
        currLabel = getCurrentLabel();
    }
    public AbstractInstrumentOptionsScreen(final Screen lastScreen) {
        super(Component.translatable("button.genshinstrument.instrumentOptions"));
        this.isOverlay = false;
        active = true;
        
        this.screen = null;
        this.lastScreen = lastScreen;

        // Default to NoteGridLabel's values
        labels = NoteGridLabel.values();
        currLabel = ModClientConfigs.GRID_LABEL_TYPE.get();
    }

    public void setOnCloseRunnable(Runnable onCloseRunnable) {
        this.onCloseRunnable = onCloseRunnable;
    }

    @Override
    protected void init() {

        final GridWidget grid = new GridWidget();
        grid.defaultCellSetting()
            .padding(getHorzPadding(), getVertPadding())
            .alignVertically(.5f)
            .alignHorizontallyCenter();
        final RowHelper rowHelper = grid.createRowHelper(2);


        initOptionsGrid(grid, rowHelper);

        FrameWidget.alignInRectangle(grid, 0, 0, width, height, 0.5f, 0);
        addRenderableWidget(grid);
        
        grid.setY(40);


        final Button doneBtn = Button.builder(CommonComponents.GUI_DONE, (btn) -> onClose())
            .width(getSmallButtonWidth())
            .pos((width - getSmallButtonWidth())/2, Math.min(grid.getY() + grid.getHeight() + 60, height - getButtonHeight() - 15))
            .build();
        addRenderableWidget(doneBtn);
        
    }
    protected void initOptionsGrid(final GridWidget grid, final RowHelper rowHelper) {

        final CycleButton<InstrumentChannelType> instrumentChannel = CycleButton.<InstrumentChannelType>builder((soundType) ->
            Component.translatable(SOUND_CHANNEL_KEY +"."+ soundType.toString().toLowerCase())
        )
            .withValues(InstrumentChannelType.values())
            .withInitialValue(ModClientConfigs.CHANNEL_TYPE.get())

            .withTooltip((soundType) -> Tooltip.create(switch (soundType) {
                case MIXED -> translatableArgs(SOUND_CHANNEL_KEY+".mixed.tooltip", NoteSound.STEREO_RANGE);
                case STEREO -> Component.translatable(SOUND_CHANNEL_KEY+".stereo.tooltip");
                default -> Component.empty();
            }))
            .create(0, 0,
                getBigButtonWidth(), 20, Component.translatable(SOUND_CHANNEL_KEY), this::onChannelTypeChanged);
        rowHelper.addChild(instrumentChannel, 2);

        final BetterSlider pitchSlider = new BetterSlider(0, 0, getSmallButtonWidth(), 23,
            Component.translatable("button.genshinstrument.pitch").append(": "), Component.empty(),
            NoteSound.MIN_PITCH, NoteSound.MAX_PITCH, ModClientConfigs.PITCH.get(), 0.05,
            this::onPitchChanged
        );
        rowHelper.addChild(pitchSlider);
        
        if (labels != null) {
            final CycleButton<INoteLabel> labelType = CycleButton.<INoteLabel>builder((label) -> Component.translatable(label.getKey()))
                .withValues(labels)
                .withInitialValue(currLabel)
                .create(0, 0,
                    getSmallButtonWidth(), getButtonHeight(),
                    Component.translatable("button.genshinstrument.label"), this::onLabelChanged
                );
            rowHelper.addChild(labelType);
        }

        final CycleButton<Boolean> stopMusic = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(ModClientConfigs.STOP_MUSIC_ON_PLAY.get())
            .withTooltip((value) -> Tooltip.create(Component.translatable(STOP_MUSIC_KEY+".tooltip", NoteSound.STOP_SOUND_DISTANCE)))
            .create(0, 0,
                getBigButtonWidth(), getButtonHeight(),
                Component.translatable(STOP_MUSIC_KEY), this::onMusicStopChanged
            );
        rowHelper.addChild(stopMusic, 2, rowHelper.newCellSettings().paddingTop(15));

        grid.pack();
    }

    // Option handlers
    protected void onLabelChanged(final CycleButton<INoteLabel> button, final INoteLabel label) {
        if (screen != null)
            screen.noteIterable().forEach((note) -> note.setLabelSupplier(label.getLabelSupplier()));

        queueToSave("note_label", () -> saveLabel(label));
    }
    protected void saveLabel(final INoteLabel newLabel) {
        if (newLabel instanceof NoteGridLabel)
            ModClientConfigs.GRID_LABEL_TYPE.set((NoteGridLabel)newLabel);
    }

    protected void onPitchChanged(final ForgeSlider slider, final double pitch) {
        if (screen != null)
            screen.noteIterable().forEach((note) -> note.getSound().setPitch((float)pitch));

        queueToSave("pitch", () -> savePitch(pitch));
    }
    protected void savePitch(final double newPitch) {
        ModClientConfigs.PITCH.set(newPitch);
    }

    // These values derive from the config directly, so update them on-spot
    protected void onChannelTypeChanged(CycleButton<InstrumentChannelType> button, InstrumentChannelType type) {
        ModClientConfigs.CHANNEL_TYPE.set(type);
    }
    protected void onMusicStopChanged(final CycleButton<Boolean> button, final boolean value) {
        ModClientConfigs.STOP_MUSIC_ON_PLAY.set(value);
    }



    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (!active)
            return;
        
        renderBackground(pPoseStack);
        drawCenteredString(pPoseStack, font, title, width/2, 20, RGBColor.WHITE.getNumeric());
        
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }


    //#region registration stuff
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (!active)
            return false;
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (!active)
            return false;
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }
    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (!active)
            return false;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }
    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (!active)
            return false;
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
    //#endregion


    @Override
    public void onClose() {
        if (isOverlay)
            active = false;
        else if (lastScreen != null)
            Minecraft.getInstance().setScreen(lastScreen);
        else
            super.onClose();
        
        onSave();

        if (onCloseRunnable != null)
            onCloseRunnable.run();
    }
    protected void onSave() {
        for (final Runnable runnable : APPLIED_OPTIONS.values())
            runnable.run();
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