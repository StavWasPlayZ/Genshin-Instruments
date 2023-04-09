package com.cstav.genshinstrument.client.gui.screens.options.instrument;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.label.NoteLabel;
import com.cstav.genshinstrument.client.gui.screens.options.widget.BetterSlider;
import com.cstav.genshinstrument.sounds.NoteSound;
import com.cstav.genshinstrument.util.RGBColor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.FrameWidget;
import net.minecraft.client.gui.components.GridWidget;
import net.minecraft.client.gui.components.GridWidget.RowHelper;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = Main.MODID, bus = Bus.FORGE)
public class InstrumentOptionsScreen extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String SOUND_CHANNEL_KEY = "button.genshinstrument.audioChannels",
        STOP_MUSIC_KEY = "button.genshinstrument.stop_music_on_play";
    

    protected int getHorzPadding() {
        return 4;
    }
    protected int getVertPadding() {
        return 2;
    }

    protected int getSmallButtonWidth() {
        return 150;
    }
    protected int getBigButtonWidth() {
        return (getSmallButtonWidth() + getHorzPadding()) * 2;
    }
    protected int getButtonHeight() {
        return 20;
    }
    

    final Screen lastScreen;
    final boolean isOverlay;
    public boolean active;
    private Runnable onCloseRunnable;
    final @Nullable AbstractInstrumentScreen screen;

    public InstrumentOptionsScreen(@Nullable AbstractInstrumentScreen screen) {
        super(Component.translatable("button.genshinstrument.instrumentOptions"));
        
        this.isOverlay = true;
        active = false;
        this.screen = screen;
        lastScreen = null;
    }
    public InstrumentOptionsScreen(final Screen lastScreen) {
        super(Component.translatable("button.genshinstrument.instrumentOptions"));
        this.isOverlay = false;
        active = true;
        
        this.screen = null;
        this.lastScreen = lastScreen;
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
            NoteSound.MIN_PITCH, NoteSound.MAX_PITCH, ModClientConfigs.PITCH.get(), 0.1,
            this::onPitchChanged
        );
        rowHelper.addChild(pitchSlider);
        
        final CycleButton<NoteLabel> labelType = CycleButton.<NoteLabel>builder((label) -> Component.translatable(label.getKey()))
            .withValues(NoteLabel.values())
            .withInitialValue(ModClientConfigs.LABEL_TYPE.get())
            .create(0, 0,
                getSmallButtonWidth(), getButtonHeight(),
                Component.translatable("button.genshinstrument.label"), this::onLabelChanged
            );
        rowHelper.addChild(labelType);

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
    protected NoteLabel newLabel = null;
    protected void onLabelChanged(final CycleButton<NoteLabel> button, final NoteLabel label) {
        newLabel = label;
        if (screen != null)
            screen.noteGrid.forEach((note) -> note.setLabel(label.getLabelSupplier()));
    }

    protected double newPitch = -1;
    protected void onPitchChanged(final ForgeSlider slider, final double pitch) {
        newPitch = pitch;
        if (screen != null)
            screen.noteGrid.forEach((note) -> note.getSound().setPitch((float)pitch));
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
    //TODO: Better implementation
    protected void onSave() {
        if (newLabel != null)
            ModClientConfigs.LABEL_TYPE.set(newLabel);
        if (newPitch != -1)
            ModClientConfigs.PITCH.set(newPitch);
    }
    
    // Register a button to open this GUI on the Options screen
    @SubscribeEvent
    public static void onScreenRendered(final ScreenEvent.Init.Post event) {
        final Screen screen = event.getScreen();
        if (!(screen instanceof OptionsScreen))
            return;

        GridWidget grid = null;
        for (final GuiEventListener listener : event.getListenersList())
            if (listener instanceof GridWidget) {
                grid = (GridWidget)listener;
                break;
            }
        if (grid == null)
            LOGGER.info("No Grid found on Options screen, aborting button insertion");

        
        // Assuming 2 columns
        final int size = grid.children().size(),
            column = size % 2, row = size / 2;

        final Minecraft minecraft = Minecraft.getInstance();
        grid.addChild(
            Button.builder(Component.translatable("button.genshinstrument.instrumentOptions"), (btn) ->
                minecraft.setScreen(new InstrumentOptionsScreen(minecraft.screen))
            ).build()
        , row, column);

        grid.pack();
        
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