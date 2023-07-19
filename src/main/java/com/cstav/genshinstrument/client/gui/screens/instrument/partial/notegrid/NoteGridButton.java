package com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid;

import com.cstav.genshinstrument.client.ClientUtil;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteNotation;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.AbsGridLabels;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteGridButtonIdentifier;
import com.cstav.genshinstrument.sound.NoteSound;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoteGridButton extends NoteButton {
    private static final double
        FLAT_TEXTURE_HEIGHT_MULTIPLIER = 3.7f/1.3f,
        FLAT_TEXTURE_WIDTH_MULTIPLIER = 1.7f/1.3f,
        SHARP_MULTIPLIER = .8f,
        DOUBLE_SHARP_MULTIPLIER = .9f
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
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTick) {
        super.renderButton(stack, mouseX, mouseY, partialTick);
        renderAccidentals(stack);
    }

    protected void renderAccidentals(final PoseStack stack) {
        switch (getNotation()) {
            case NONE: break;

            case FLAT:
                renderAccidental(stack, 0);
                break;
            case SHARP:
                renderAccidental(stack, 1);
                break;
            case DOUBLE_FLAT:
                renderAccidental(stack, 0, -6, -3);
                renderAccidental(stack, 0, 5, 2);
                break;
            case DOUBLE_SHARP:
                renderAccidental(stack, 2, -1, 0);
                break;

        }
    }
    
    protected void renderAccidental(final PoseStack stack, int index) {
        renderAccidental(stack, index, 0, 0);
    }
    protected void renderAccidental(PoseStack stack, int index, int offsetX, int offsetY) {
        final int textureWidth = (int)(width * FLAT_TEXTURE_WIDTH_MULTIPLIER * (
            (index == 1) ? SHARP_MULTIPLIER : (index == 2) ? DOUBLE_SHARP_MULTIPLIER : 1
        )),
        textureHeight = (int)(height * FLAT_TEXTURE_HEIGHT_MULTIPLIER * (
            (index == 1) ? SHARP_MULTIPLIER : (index == 2) ? DOUBLE_SHARP_MULTIPLIER : 1
        ));

        final int spritePartHeight = textureHeight/3;

        
        ClientUtil.displaySprite(accidentalsLocation);

        blit(stack,
            x - 9 + offsetX, y - 6 + offsetY,
            // Handle sharp imperfections
            isPlaying() ? textureWidth/2 : 0, (spritePartHeight) * index - index,
            textureWidth/2,  spritePartHeight + ((index == 1) ? 3 : 0),
            textureWidth - (((index != 0) && isPlaying()) ? 1 : 0), textureHeight
        );
    }

    public NoteNotation getNotation() {
        return ModClientConfigs.ACCURATE_ACCIDENTALS.get()
            ? NoteNotation.getNotation(AbsGridLabels.getNoteName(this))
            : NoteNotation.NONE;
    }
}
