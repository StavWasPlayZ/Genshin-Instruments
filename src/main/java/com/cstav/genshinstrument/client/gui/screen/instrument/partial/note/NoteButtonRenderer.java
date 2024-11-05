package com.cstav.genshinstrument.client.gui.screen.instrument.partial.note;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.animation.NoteAnimationController;
import com.cstav.genshinstrument.client.util.ClientUtil;
import com.cstav.genshinstrument.util.CommonUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class NoteButtonRenderer {
    protected static final Minecraft MINECRAFT = Minecraft.getInstance();

    private static final double SHARP_MULTIPLIER = .9;
    protected static final double NOTE_DUR = .15, NOTE_TARGET_VAL = 9;
    
    public final NoteButton noteButton;
    protected final InstrumentScreen instrumentScreen;

    // Resources
    protected final ResourceLocation rootLocation, accidentalsLocation;

    protected final ResourceLocation notePressedLocation, noteReleasedLocation, noteHoverLocation;

    protected Supplier<ResourceLocation> noteTextureProvider;

    // Animations
    public final NoteAnimationController noteAnimation;
    public boolean foreignPlaying = false;
    protected final ArrayList<NoteRing> rings = new ArrayList<>();

    protected NoteAnimationController initNoteAnimation() {
        return new NoteAnimationController(NOTE_DUR, NOTE_TARGET_VAL, noteButton);
    }


    public NoteButtonRenderer(NoteButton noteButton, Supplier<ResourceLocation> noteTextureProvider) {
        this.noteButton = noteButton;
        this.noteTextureProvider = noteTextureProvider;
        this.instrumentScreen = noteButton.instrumentScreen;

        noteAnimation = initNoteAnimation();

        
        rootLocation = instrumentScreen.getResourceFromRoot("note");
        accidentalsLocation = getResourceFromRoot("accidentals.png");

        notePressedLocation = getResourceFromRoot("note/pressed.png");
        noteReleasedLocation = getResourceFromRoot("note/released.png");
        noteHoverLocation = getResourceFromRoot("note/hovered.png");
    }



    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick, InstrumentThemeLoader themeLoader) {
        RenderSystem.enableBlend();

        rings.removeIf((ring) -> !ring.isPlaying());
        rings.forEach((ring) -> ring.render(gui, themeLoader));


        renderNoteButton(gui, themeLoader);
        renderNote(gui, themeLoader);
        renderLabel(gui, themeLoader);
        
        renderAccidentals(gui, themeLoader);


        noteAnimation.update();
    }

    protected void renderNoteButton(final GuiGraphics gui, final InstrumentThemeLoader themeLoader) {
        ResourceLocation noteLocation;

        if (noteButton.isPlaying()) {

            if (foreignPlaying)
                noteLocation = noteHoverLocation;
            else
                noteLocation = notePressedLocation;

        } else if (noteButton.isHoveredOrFocused())
            noteLocation = noteHoverLocation;
        else
            noteLocation = noteReleasedLocation;
            
        
        gui.blit(ClientUtil::guiRT,
            noteLocation,
            noteButton.getX(), noteButton.getY(),
            0, 0,

            noteButton.getWidth(), noteButton.getHeight(),
            noteButton.getWidth(), noteButton.getHeight()
        );
    }

    // "Note" here refers to those symbols in the middle of a note button
    protected void renderNote(final GuiGraphics gui, final InstrumentThemeLoader themeLoader) {
        final int noteWidth = noteButton.getWidth()/2, noteHeight = noteButton.getHeight()/2;
        
        ClientUtil.setShaderColor((noteButton.isPlaying() && !foreignPlaying)
            ? themeLoader.notePressed()
            : themeLoader.noteReleased()
        );

        gui.blit(ClientUtil::guiRT,
            noteTextureProvider.get(),
            noteButton.getX() + noteWidth/2, noteButton.getY() + noteHeight/2,
            0, 0,

            noteWidth, noteHeight,
            noteWidth, noteButton.getHeight()/2
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

    protected void renderLabel(final GuiGraphics gui, final InstrumentThemeLoader themeLoader) {
        gui.drawCenteredString(
            MINECRAFT.font, noteButton.getMessage(),
            labelX, labelY,
            ((noteButton.isPlaying() && !foreignPlaying)
                ? themeLoader.labelPressed()
                : themeLoader.labelReleased()
            ).getRGB()
        );
    }


    protected void renderAccidentals(final GuiGraphics gui, final InstrumentThemeLoader themeLoader) {
        RenderSystem.enableBlend();
        
        switch (noteButton.getNotation()) {
            case NONE: break;

            case FLAT:
                renderAccidental(gui, 0);
                break;
            case SHARP:
                renderAccidental(gui, 1);
                break;
            case DOUBLE_FLAT:
                renderAccidental(gui, 0, -6, -3);
                renderAccidental(gui, 0, 5, 2);
                break;
            case DOUBLE_SHARP:
                renderAccidental(gui, 2, -1, 0);
                break;

        }
    }
    
    protected void renderAccidental(final GuiGraphics gui, int index) {
        renderAccidental(gui, index, 0, 0);
    }
    protected void renderAccidental(GuiGraphics gui, int index, int offsetX, int offsetY) {
        final double textureMultiplier = noteButton.getWidth() * (
            // Handle sharp size
            (index == 1) ? SHARP_MULTIPLIER : 1
        ) * 2;

        final int textureWidth = (int)(textureMultiplier),
            textureHeight = (int)(textureMultiplier) - 1;

        final int spritePartWidth = textureWidth/3 + 1;


        gui.blit(ClientUtil::guiRT,
            accidentalsLocation,
            noteButton.getX() - 9 + offsetX, noteButton.getY() - 5 + offsetY,
            spritePartWidth * index, noteButton.isPlaying() ? (textureHeight + 1)/2 : 0,
            
            spritePartWidth - 1, textureHeight/2,
            textureWidth, textureHeight
        );
    }


    public void playNoteAnimation(final boolean isForeign) {
        foreignPlaying = isForeign;

        noteAnimation.play(isForeign);
        addRing();
    }
    public void addRing() {
        final NoteRing ring = new NoteRing(noteButton, foreignPlaying);
        rings.add(ring);
        ring.playAnim();
    }

    public void resetAnimations() {
        rings.clear();
        noteAnimation.stop();
    }


    /**
     * Obtains a resource from this instrument's directory.
     * @param path The resource to obtain from this note's directory
     * @see CommonUtil#getResourceFrom(ResourceLocation, String)
     */
    protected ResourceLocation getResourceFromRoot(final String path) {
        return CommonUtil.getResourceFrom(rootLocation, path);
    }

}
