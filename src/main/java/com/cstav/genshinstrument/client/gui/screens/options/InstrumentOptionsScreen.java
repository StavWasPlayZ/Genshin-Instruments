package com.cstav.genshinstrument.client.gui.screens.options;

import javax.annotation.Nullable;

import com.cstav.genshinstrument.client.gui.screens.lyre.LyreScreen;
import com.cstav.genshinstrument.client.gui.screens.lyre.label.NoteLabel;
import com.cstav.genshinstrument.networking.packets.lyre.LyrePacket;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.FrameWidget;
import net.minecraft.client.gui.components.GridWidget;
import net.minecraft.client.gui.components.GridWidget.RowHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
    @Nullable LyreScreen screen;

    public InstrumentOptionsScreen(Component pTitle, final boolean isOverlay, @Nullable LyreScreen screen) {
        super(pTitle);
        this.isOverlay = isOverlay;
        active = !isOverlay;
        this.screen = screen;
    }

    @Override
    protected void init() {

        final GridWidget grid = new GridWidget();
        grid.defaultCellSetting().padding(4).alignVertically(.5f);
        final RowHelper rowHelper = grid.createRowHelper(2);


        final ForgeSlider pitchSlider = new PitchSlider();
        rowHelper.addChild(pitchSlider);
        
        final CycleButton<NoteLabel> labelType = CycleButton.<NoteLabel>builder((label) -> Component.translatable(label.getKey()))
            .withValues(NoteLabel.values())
            //TODO: Read from file
            .withInitialValue(NoteLabel.KEYBOARD_LAYOUT)
            .create(
                0, 0, getButtonWidth(), getButtonHeight(),
                Component.translatable("button.genshinstrument.label"), this::onLabelChanged
            );
        rowHelper.addChild(labelType);


        grid.pack();
        FrameWidget.alignInRectangle(grid, 0, 0, width, height, 0.5f, .1f);
        addRenderableWidget(grid);

    }

    // Option handlers
    class PitchSlider extends ForgeSlider {

        public PitchSlider() {
            super(0, 0, getButtonWidth(),
                23, Component.translatable("Pitch: "), Component.empty(),
                LyrePacket.MIN_PITCH, LyrePacket.MAX_PITCH,
                //TODO: Read from file
                1,
                0.1, 0,
                true
            );
        }

        @SuppressWarnings("null")
        @Override
        protected void applyValue() {
            if (screen != null)
                screen.noteGrid.forEach((note) ->
                    note.setPitch((float)getValue())
                );
        }

    }
    @SuppressWarnings("null")
    void onLabelChanged(final CycleButton<NoteLabel> button, final NoteLabel label) {
        if (screen != null)
            screen.noteGrid.forEach((note) -> note.setLabel(label.getLabelSupplier()));
    }


    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (!active)
            return;
        
        renderBackground(pPoseStack);
        // list.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }


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
    public void onClose() {
        if (isOverlay)
            active = false;
        else
            super.onClose();

        //TODO: Save preferences to file
    }
    
}