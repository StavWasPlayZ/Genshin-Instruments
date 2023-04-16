package com.cstav.genshinstrument.client.gui.screens.instrument.vintageLyre;

import java.util.function.Supplier;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractGridInstrument;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.NoteGridButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.label.NoteLabelSupplier;
import com.cstav.genshinstrument.sounds.NoteSound;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VintageNoteButton extends NoteGridButton {
    private static final float TEXTURE_MULTIPLIER = 1/1.3f;

    private final ResourceLocation thingyLocation = getResourceFromRoot("thing.png");

    public VintageNoteButton(int row, int column, NoteSound sound, NoteLabelSupplier labelSupplier,
      ResourceLocation noteResourcesLocation, Supplier<Integer> colorTheme,
      Supplier<Integer> pressedThemeColor) {
        // We know for sure that the lyre is a 7x3
        super(row, column, sound, labelSupplier, noteResourcesLocation, AbstractGridInstrument.DEF_ROWS, colorTheme, pressedThemeColor);
    }

    
    private boolean shouldRenderThingy() {
        return (row == 6) || (row == 2) ||
            ((row == 1) && (column == 0)) || ((row == 5) && (column == 0));
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);

        if (!shouldRenderThingy())
            return;

        final int textureWidth = (int)(width * TEXTURE_MULTIPLIER),
            textureHeight = (int)(height * TEXTURE_MULTIPLIER);

        displaySprite(thingyLocation);
        GuiComponent.blit(pPoseStack,
            getX(), getY() - 5,
            isPlaying() ? textureWidth/2 : 0, 0,
            textureWidth/2,  textureHeight,
            textureWidth, textureHeight
        );
    }
    
}
