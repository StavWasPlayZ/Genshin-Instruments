package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note;

import java.util.ArrayList;

import com.cstav.genshinstrument.client.ClientUtil;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.animation.NoteAnimationController;
import com.cstav.genshinstrument.util.CommonUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;

public class NoteButtonRenderer {
    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    private static final double
        FLAT_TEXTURE_HEIGHT_MULTIPLIER = 3.7f/1.3f,
        FLAT_TEXTURE_WIDTH_MULTIPLIER = 1.7f/1.3f,
        SHARP_MULTIPLIER = .8f,
        DOUBLE_SHARP_MULTIPLIER = .9f
    ;
    
    public final NoteButton noteButton;
    protected final AbstractInstrumentScreen instrumentScreen;

    // Resources
    protected final ResourceLocation rootLocation,
        noteLocation, noteBgLocation, accidentalsLocation;

    // Texture properties
    protected final int noteTextureRow, rowsInNoteTexture;
    private final int noteTextureWidth;
    //FIXME Actually figure out a formula instead of guessing
    private final float randomAssMultiplier1, randomAssMultiplier2;

    // Animations
    public final NoteAnimationController noteAnimation;
    private boolean foreignPlaying = false;
    protected final ArrayList<NoteRing> rings = new ArrayList<>();


    public NoteButtonRenderer(NoteButton noteButton, int noteTextureRow, int rowsInNoteTexture,
            int noteTextureWidth, float randomAssMultiplier1, float randomAssMultiplier2) {
        this.noteButton = noteButton;
        instrumentScreen = noteButton.instrumentScreen;

        noteAnimation = new NoteAnimationController(.15f, 9, noteButton);


        this.noteTextureRow = noteTextureRow;
        this.rowsInNoteTexture = rowsInNoteTexture;


        rootLocation = instrumentScreen.getResourceFromRoot("note");

        noteLocation = instrumentScreen.getNotesLocation();
        noteBgLocation = getResourceFromRoot("note_bg.png");
        accidentalsLocation = getResourceFromRoot("accidentals.png");



        this.noteTextureWidth = noteTextureWidth;
        this.randomAssMultiplier1 = randomAssMultiplier1;
        this.randomAssMultiplier2 = randomAssMultiplier2;
    }



    public void render(PoseStack stack, int mouseX, int mouseY, float partialTick, InstrumentThemeLoader themeLoader) {
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        rings.removeIf((ring) -> !ring.isPlaying());
        rings.forEach((ring) -> ring.render(stack));


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
            noteButton.getX(), noteButton.getY(),
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
            noteButton.getX() + noteWidth/2, noteButton.getY() + noteHeight/2,
            //NOTE: I have no clue whatsoever how on earth these 1.025 and .9 multipliers actually work.
            // Like seriously wtf why fkuaherjgaeorg i hate maths
            //NOTE: Moved said numbers to the randomAss vars
            noteWidth * noteTextureRow * randomAssMultiplier2, 0,
            noteWidth, noteHeight,
            (int)(noteWidth * (noteTextureWidth / rowsInNoteTexture) * randomAssMultiplier1), noteButton.getHeight()/2
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
        // Handle sharp imperfections
        final int textureWidth = (int)(noteButton.getWidth() * FLAT_TEXTURE_WIDTH_MULTIPLIER * (
            (index == 1) ? SHARP_MULTIPLIER : (index == 2) ? DOUBLE_SHARP_MULTIPLIER : 1
        )),
        textureHeight = (int)(noteButton.getHeight() * FLAT_TEXTURE_HEIGHT_MULTIPLIER * (
            (index == 1) ? SHARP_MULTIPLIER : (index == 2) ? DOUBLE_SHARP_MULTIPLIER : 1
        ));

        final int spritePartHeight = textureHeight/3;


        ClientUtil.displaySprite(accidentalsLocation);

        GuiComponent.blit(stack,
            noteButton.getX() - 9 + offsetX, noteButton.getY() - 6 + offsetY,
            noteButton.isPlaying() ? textureWidth/2 : 0, (spritePartHeight) * index - index,
            
            textureWidth/2,  spritePartHeight + ((index == 1) ? 3 : 0),
            textureWidth - (((index != 0) && noteButton.isPlaying()) ? 1 : 0), textureHeight
        );
    }


    public void playNoteAnimation(final boolean isForeign) {
        foreignPlaying = isForeign;

        noteAnimation.play(isForeign);
        if (ModClientConfigs.EMIT_RING_ANIMATION.get())
            rings.add(new NoteRing(noteButton, isForeign));
    }


    /**
     * @param path The resource to obtain from this note's directory
     * @see {@link AbstractInstrumentScreen#getResourceFrom(ResourceLocation, String)}
     */
    protected ResourceLocation getResourceFromRoot(final String path) {
        return CommonUtil.getResourceFrom(rootLocation, path);
    }

}
