package com.cstav.genshinstrument.client.gui.screen.instrument.ukelele;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButtonRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class UkuleleNoteButtonRenderer extends NoteButtonRenderer {
    protected final ResourceLocation
        topColumnNotePressedLocation,
        topColumnNoteReleasedLocation,
        topColumnNoteHoverLocation
    ;

    public UkuleleNoteButtonRenderer(NoteButton noteButton, Supplier<ResourceLocation> labelTextureProvider) {
        super(noteButton, labelTextureProvider);

        topColumnNotePressedLocation = getResourceFromRoot("note/top_pressed.png");
        topColumnNoteReleasedLocation = getResourceFromRoot("note/top_released.png");
        topColumnNoteHoverLocation = getResourceFromRoot("note/top_hovered.png");
    }


    private UkuleleNoteButton getButton() {
        return (UkuleleNoteButton) noteButton;
    }


    @Override
    protected ResourceLocation getNoteReleasedLocation() {
        return getTopColumnOverride(topColumnNoteReleasedLocation, super.getNoteReleasedLocation());
    }
    @Override
    protected ResourceLocation getNotePressedLocation() {
        return getTopColumnOverride(topColumnNotePressedLocation, super.getNotePressedLocation());
    }
    @Override
    protected ResourceLocation getNoteHoverLocation() {
        return getTopColumnOverride(topColumnNoteHoverLocation, super.getNoteHoverLocation());
    }

    private ResourceLocation getTopColumnOverride(final ResourceLocation newLocation, final ResourceLocation superLocation) {
        if (getButton().ukuleleScreen().isTopRegular())
            return superLocation;

        if (getButton().column == 0) {
            return newLocation;
        }

        return superLocation;
    }


    @Override
    protected void renderNote(PoseStack stack, InstrumentThemeLoader themeLoader) {
        if (getButton().ukuleleScreen().isTopRegular()) {
            super.renderNote(stack, themeLoader);
            return;
        }

        if (getButton().column != 0) {
            super.renderNote(stack, themeLoader);
            return;
        }

        final int noteWidth = noteButton.getWidth(), noteHeight = noteButton.getHeight();
        final int noteX = noteButton.x, noteY = noteButton.y;

        final float scaleMultiplier = noteButton.getWidth() / ((float)noteButton.instrumentScreen.getNoteSize()/2);

        stack.pushPose();
        stack.scale(scaleMultiplier, scaleMultiplier, scaleMultiplier);

        GuiComponent.drawCenteredString(stack,
            MINECRAFT.font, getButton().getChordNameOfRow(),
            (int)((noteX + noteWidth/2f) / scaleMultiplier),
            (int)((noteY + noteHeight/4f + 2) / scaleMultiplier),

            ((noteButton.isPlaying() && !foreignPlaying)
                ? themeLoader.labelPressed(noteButton)
                : themeLoader.labelReleased(noteButton)
            ).getRGB()
        );

        stack.popPose();
    }
}
