package com.cstav.genshinstrument.client.gui.screens.options.instrument;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.cstav.genshinstrument.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.label.NoteLabel;
import com.cstav.genshinstrument.networking.packets.lyre.InstrumentPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.FrameWidget;
import net.minecraft.client.gui.components.GridWidget;
import net.minecraft.client.gui.components.GridWidget.RowHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ScreenUtils;
import net.minecraftforge.client.gui.widget.ForgeSlider;

@OnlyIn(Dist.CLIENT)
public class InstrumentOptionsScreen extends Screen {
    private static final float BUTTONS_WIDTH_PER = .4f;
    
    public int getButtonWidth() {
        return (int)(width * BUTTONS_WIDTH_PER/2);
    }
    public int getButtonHeight() {
        return 20;
    }
    

    final boolean isOverlay;
    public boolean active;
    @Nullable AbstractInstrumentScreen screen;

    public InstrumentOptionsScreen(Component pTitle, final boolean isOverlay, @Nullable AbstractInstrumentScreen screen) {
        super(pTitle);
        this.isOverlay = isOverlay;
        active = !isOverlay;
        this.screen = screen;
    }

    @Override
    protected void init() {

        final GridWidget grid = new GridWidget();
        grid.defaultCellSetting()
            .padding(4)
            .alignVertically(.5f)
            .alignHorizontallyCenter();
            
        final RowHelper rowHelper = grid.createRowHelper(2);

        initOptionsGrid(grid, rowHelper);
        rowHelper.addChild(
            Button.builder(CommonComponents.GUI_DONE, (btn) -> onClose())
                .width(getButtonWidth())
                .build()
        , 2, rowHelper.newCellSettings().paddingTop(64));

        grid.pack();
        FrameWidget.alignInRectangle(grid, 0, 0, width, height, 0.5f, .1f);
        addRenderableWidget(grid);

    }
    protected void initOptionsGrid(final GridWidget grid, final RowHelper rowHelper) {
        final ForgeSlider pitchSlider = new PitchSlider();
        rowHelper.addChild(pitchSlider);
        
        final CycleButton<NoteLabel> labelType = CycleButton.<NoteLabel>builder((label) -> Component.translatable(label.getKey()))
            .withValues(NoteLabel.values())
            .withInitialValue(ModClientConfigs.LABEL_TYPE.get())
            .create(
                0, 0, getButtonWidth(), getButtonHeight(),
                Component.translatable("button.genshinstrument.label"), this::onLabelChanged
            );
        
        rowHelper.addChild(labelType);
    }

    // Option handlers
    protected class PitchSlider extends ForgeSlider {

        public PitchSlider() {
            super(0, 0, getButtonWidth(),
                23, Component.translatable("button.genshinstrument.pitch").append(": "), Component.empty(),
                InstrumentPacket.MIN_PITCH, InstrumentPacket.MAX_PITCH, ModClientConfigs.PITCH.get(),
                0.1, 0,
                true
            );
        }

        @Override
        protected void applyValue() {
            onPitchChanged(this, getValue());
        }
        // Forge's very, very clever overflow implementation makes clients
        // (primarily Optifine clients) crash
        // For some reason the ellipsize method is undefined
        // Beats me idk
        @Override
        public void renderButton(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick)
        {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);

            final Minecraft mc = Minecraft.getInstance();
            final int bgYImage = this.getYImage(this.isHoveredOrFocused());
            ScreenUtils.blitWithBorder(poseStack,
                this.getX(), this.getY(),
                0, 46 + bgYImage * 20,
                this.width, this.height,
                200, 20,
                2, 3,
                2, 2
            , this.getBlitOffset());

            final int sliderYImage = (this.isHoveredOrFocused() ? 2 : 1) * 20;
            ScreenUtils.blitWithBorder(poseStack,
                this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(),
                0, 46 + sliderYImage,
                8, this.height,
                200, 20,
                2, 3, 2, 2
            , this.getBlitOffset());

            // final FormattedText message = mc.font.ellipsize(getMessage(), this.width - 6);
            drawCenteredString(poseStack, mc.font, getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, getFGColor());
        }

    }
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
            screen.noteGrid.forEach((note) -> note.setPitch((float)pitch));
    }


    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (!active)
            return;
        
        renderBackground(pPoseStack);
        // list.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
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
        else
            super.onClose();
        
        onSave();
        onCloseRunnable.run();
    }
    private Runnable onCloseRunnable;
    public void setOnCloseRunnable(Runnable onCloseRunnable) {
        this.onCloseRunnable = onCloseRunnable;
    }

    protected void onSave() {
        if (newLabel != null)
            ModClientConfigs.LABEL_TYPE.set(newLabel);
        if (newPitch != -1)
            ModClientConfigs.PITCH.set(newPitch);
    }
    
}