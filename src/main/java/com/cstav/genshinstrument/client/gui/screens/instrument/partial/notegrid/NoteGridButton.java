package com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteNotation;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.AbsGridLabels;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteGridButtonIdentifier;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoteGridButton extends NoteButton {
    private static final float
        FLAT_TEXTURE_HEIGHT_MULTIPLIER = 2.3f/1.3f,
        FLAT_TEXTURE_WIDTH_MULTIPLIER = 1.7f/1.3f
    ;

    private final ResourceLocation accidentalsLocation = getResourceFromRoot("accidentals.png");


    public final int row, column;

    public NoteGridButton(int row, int column, NoteSound sound, NoteLabelSupplier labelSupplier,
            AbstractGridInstrumentScreen instrumentScreen) {
        super(sound, labelSupplier, row, instrumentScreen.rows(), instrumentScreen);
        
        this.row = row;
        this.column = column;
    }

    @Override
    public NoteGridButtonIdentifier getIdentifier() {
        return new NoteGridButtonIdentifier(this);
    }


    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(gui, mouseX, mouseY, partialTick);
        renderAccidentals(gui);
    }

    protected void renderAccidentals(final GuiGraphics gui) {
        switch (getNotation()) {
            case NONE: break;

            case FLAT:
                renderAccidental(gui, false);
                break;
            case SHARP:
                renderAccidental(gui, true);
                break;
            case DOUBLE_FLAT:
                renderDoubleAccidental(gui, false);
                break;
            case DOUBLE_SHARP:
                renderDoubleAccidental(gui, true);
                break;

        }
    }

    protected void renderDoubleAccidental(final GuiGraphics gui, final boolean isSharp) {
        renderAccidental(gui, isSharp, -6, -3);
        renderAccidental(gui, isSharp, 5, 2);
    }
    
    protected void renderAccidental(final GuiGraphics gui, final boolean isSharp) {
        renderAccidental(gui, isSharp, 0, 0);
    }
    protected void renderAccidental(GuiGraphics gui, boolean isSharp, int offsetX, int offsetY) {
        final int textureWidth = (int)(width * FLAT_TEXTURE_WIDTH_MULTIPLIER),
        textureHeight = (int)(height * FLAT_TEXTURE_HEIGHT_MULTIPLIER);

        gui.blit(accidentalsLocation,
            getX() - 9 + offsetX, getY() - 6 + offsetY,
            // Handle sharp imperfections
            isPlaying() ? textureWidth/2 : 0, isSharp ? textureHeight/2-3 : 0,
            textureWidth/2,  textureHeight/2 + (isSharp ? 4 : 0),
            textureWidth - ((isSharp && isPlaying()) ? 1 : 0), textureHeight
        );
    }

    public NoteNotation getNotation() {
        return ModClientConfigs.ACCURATE_ACCIDENTALS.get()
            ? NoteNotation.getNotation(AbsGridLabels.getNoteName(this))
            : NoteNotation.NONE;
    }
}
