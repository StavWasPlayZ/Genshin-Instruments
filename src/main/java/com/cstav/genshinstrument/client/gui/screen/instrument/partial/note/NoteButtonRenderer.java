package com.cstav.genshinstrument.client.gui.screen.instrument.partial.note;

import java.util.ArrayList;

import com.cstav.genshinstrument.client.ClientUtil;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.animation.NoteAnimationController;
import com.cstav.genshinstrument.util.CommonUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;

public class NoteButtonRenderer {
    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    private static final double SHARP_MULTIPLIER = .9;
    
    public final NoteButton noteButton;
    protected final AbstractInstrumentScreen instrumentScreen;

    // Resources
    protected final ResourceLocation rootLocation,
        noteLocation, noteBgLocation, accidentalsLocation;

    // Texture properties
    public int noteTextureRow;
    protected final int rowsInNoteTexture;

    // Animations
    public final NoteAnimationController noteAnimation;
    private boolean foreignPlaying = false;
    protected final ArrayList<NoteRing> rings = new ArrayList<>();


    public NoteButtonRenderer(NoteButton noteButton, int noteTextureRow, int rowsInNoteTexture) {
        this.noteButton = noteButton;
        instrumentScreen = noteButton.instrumentScreen;

        noteAnimation = new NoteAnimationController(.15f, 9, noteButton);


        this.noteTextureRow = noteTextureRow;
        this.rowsInNoteTexture = rowsInNoteTexture;


        rootLocation = instrumentScreen.getResourceFromRoot("note");

        noteLocation = instrumentScreen.getNoteSymbolsLocation();
        noteBgLocation = getResourceFromRoot("note_bg.png");
        accidentalsLocation = getResourceFromRoot("accidentals.png");
    }



    public void render(PoseStack stack, int mouseX, int mouseY, float partialTick, InstrumentThemeLoader themeLoader) {
        RenderSystem.enableBlend();

        rings.removeIf((ring) -> !ring.isPlaying());
        rings.forEach((ring) -> ring.render(stack, themeLoader));


        renderNoteButton(stack, themeLoader);
        renderNote(stack, themeLoader);
        renderLabel(stack, themeLoader);
        
        renderAccidentals(stack, themeLoader);


        noteAnimation.update();
    }

    protected void renderNoteButton(final PoseStack stack, final InstrumentThemeLoader themeLoader) {
        // width = full color, width * 2 = border, 0 = normal
        int blitOffset =
            noteButton.isPlaying() ?
                foreignPlaying ?
                    (noteButton.getWidth() * 2)
                : noteButton.getWidth()
            : noteButton.isHoveredOrFocused() ?
                (noteButton.getWidth() * 2)
            : 0;


        ClientUtil.displaySprite(noteBgLocation);
        
        GuiComponent.blit(stack,
            noteButton.x, noteButton.y,
            blitOffset, 0,
            noteButton.getWidth(), noteButton.getHeight(),
            noteButton.getWidth()*3, noteButton.getHeight()
        );
    }

    protected void renderNote(final PoseStack stack, final InstrumentThemeLoader themeLoader) {
        final int noteWidth = noteButton.getWidth()/2, noteHeight = noteButton.getHeight()/2;
        
        ClientUtil.setShaderColor((noteButton.isPlaying() && !foreignPlaying)
            ? themeLoader.getPressedNoteTheme()
            : themeLoader.getLabelTheme()
        );


        ClientUtil.displaySprite(noteLocation);

        GuiComponent.blit(stack,
            noteButton.x + noteWidth/2, noteButton.y + noteHeight/2,
            noteWidth * noteTextureRow, 0,
            
            noteWidth, noteHeight,
            noteWidth * rowsInNoteTexture, noteButton.getHeight()/2
        );

        ClientUtil.resetShaderColor();
    }


    // Labels act junky when the notes are pressed, so just cache their initial and fixed location
    private int labelX, labelY;
    public void setLabelX(int labelX) {
        this.labelX = labelX;
    }
    public void setLabelY(int labelY) {
        this.labelY = labelY;
    }

    protected void renderLabel(final PoseStack stack, final InstrumentThemeLoader themeLoader) {
        GuiComponent.drawCenteredString(stack,
            MINECRAFT.font, noteButton.getMessage(),
            labelX, labelY,
            ((noteButton.isPlaying() && !foreignPlaying)
                ? themeLoader.getPressedNoteTheme()
                : themeLoader.getNoteTheme()
            ).getRGB()
        );
    }


    protected void renderAccidentals(final PoseStack stack, final InstrumentThemeLoader themeLoader) {
        RenderSystem.enableBlend();
        
        switch (noteButton.getNotation()) {
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
        final double textureMultiplier = noteButton.getWidth() * (
            // Handle sharp size
            (index == 1) ? SHARP_MULTIPLIER : 1
        ) * 2;

        final int textureWidth = (int)(textureMultiplier),
            textureHeight = (int)(textureMultiplier) - 1;

        final int spritePartWidth = textureWidth/3 + 1;


        ClientUtil.displaySprite(accidentalsLocation);

        GuiComponent.blit(stack,
            noteButton.x - 9 + offsetX, noteButton.y - 5 + offsetY,
            spritePartWidth * index, noteButton.isPlaying() ? (textureHeight + 1)/2 : 0,
            
            spritePartWidth - 1, textureHeight/2,
            textureWidth, textureHeight
        );
    }


    public void playNoteAnimation(final boolean isForeign) {
        foreignPlaying = isForeign;

        noteAnimation.play(isForeign);
        if (ModClientConfigs.EMIT_RING_ANIMATION.get())
            rings.add(new NoteRing(noteButton, isForeign));
    }


    /**
     * Obtains a resource from this instrument's directory.
     * @param path The resource to obtain from this note's directory
     * @see {@link AbstractInstrumentScreen#getResourceFrom(ResourceLocation, String)}
     */
    protected ResourceLocation getResourceFromRoot(final String path) {
        return CommonUtil.getResourceFrom(rootLocation, path);
    }

}