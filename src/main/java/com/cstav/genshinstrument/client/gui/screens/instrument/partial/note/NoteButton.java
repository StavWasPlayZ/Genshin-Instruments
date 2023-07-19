package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note;

import java.awt.Point;
import java.util.ArrayList;

import com.cstav.genshinstrument.client.ClientUtil;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.animation.NoteAnimationController;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.buttonidentifier.DefaultNoteButtonIdentifier;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.instrument.InstrumentPacket;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.util.CommonUtil;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The abstract implementation of an instrument's note button.
 * @param <T> The type of this button's identifier
 */
@OnlyIn(Dist.CLIENT)
public abstract class NoteButton extends AbstractButton {

    public static final String NOTE_BG_FILENAME = "note_bg.png";

    private static final double
        FLAT_TEXTURE_HEIGHT_MULTIPLIER = 3.7f/1.3f,
        FLAT_TEXTURE_WIDTH_MULTIPLIER = 1.7f/1.3f,
        SHARP_MULTIPLIER = .8f,
        DOUBLE_SHARP_MULTIPLIER = .9f
    ;


    @SuppressWarnings("resource")
    public static int getSize() {
        final int guiScale = Minecraft.getInstance().options.guiScale().get();

        return switch (guiScale) {
            case 0 -> 40;
            case 1 -> 35;
            case 2 -> 46;
            case 3 -> 48;
            case 4 -> 40;
            default -> guiScale * 18;
        };
    }
    

    protected final Minecraft minecraft = Minecraft.getInstance();

    protected final NoteAnimationController noteAnimation = new NoteAnimationController(.15f, 9, this);
    protected final ArrayList<NoteRing> rings = new ArrayList<>();

    /**
     * <p>Returns the identifier of this button.</p>
     * You may use the {@link DefaultNoteButtonIdentifier default implementation} if you're too lazy.
     */
    public NoteButtonIdentifier getIdentifier() {
        return new DefaultNoteButtonIdentifier(getSound());
    }

    
    private NoteSound sound;
    public final AbstractInstrumentScreen instrumentScreen;

    protected final int noteTextureRow, rowsInNoteTexture;
    protected final ResourceLocation rootLocation,
        noteLocation, noteBgLocation, accidentalsLocation;


    private NoteLabelSupplier labelSupplier;
    private int noteTextureWidth = 56;
    //FIXME Actually figure out a formula instead of guessing
    private float randomAssMultiplier1 = .9f, randomAssMultiplier2 = 1.025f;
    
    public NoteButton(NoteSound sound,
      NoteLabelSupplier labelSupplier, int noteTextureRow, int rowsInNoteTexture,
      AbstractInstrumentScreen instrumentScreen) {
        super(0, 0, getSize(), getSize(), null);


        this.sound = sound;

        this.labelSupplier = labelSupplier;

        this.instrumentScreen = instrumentScreen;

        this.noteTextureRow = noteTextureRow;
        this.rowsInNoteTexture = rowsInNoteTexture;


        rootLocation = instrumentScreen.getResourceFromRoot("note");

        noteLocation = instrumentScreen.getNotesLocation();
        noteBgLocation = getResourceFromRoot(NOTE_BG_FILENAME);
        accidentalsLocation = getResourceFromRoot("accidentals.png");
    }
    public NoteButton(NoteSound sound,
            NoteLabelSupplier labelSupplier, int noteTextureRow, int rowsInNoteTexture,
            AbstractInstrumentScreen instrumentScreen, int noteTextureWidth,
            
      float randomAssMultiplier1, float randomAssMultiplier2) {
        this(sound, labelSupplier, noteTextureRow, rowsInNoteTexture, instrumentScreen);

        this.noteTextureWidth = noteTextureWidth;
        this.randomAssMultiplier1 = randomAssMultiplier1;
        this.randomAssMultiplier2 = randomAssMultiplier2;
    }

    public void setLabelSupplier(final NoteLabelSupplier labelSupplier) {
        this.labelSupplier = labelSupplier;
        updateNoteLabel();
    }
    public NoteLabelSupplier getLabelSupplier() {
        return labelSupplier;
    }
    public void updateNoteLabel() {
        setMessage(getLabelSupplier().get(this));
    }

    public NoteSound getSound() {
        return sound;
    }
    public void setSound(NoteSound sound) {
        this.sound = sound;

        // Update the sound for the sound (default) identifier
        if (getIdentifier() instanceof DefaultNoteButtonIdentifier)
            ((DefaultNoteButtonIdentifier)getIdentifier()).setSound(sound);
    }


    /**
     * @param path The resource to obtain from this note's directory
     * @see {@link AbstractInstrumentScreen#getResourceFrom(ResourceLocation, String)}
     */
    protected ResourceLocation getResourceFromRoot(final String path) {
        return CommonUtil.getResourceFrom(rootLocation, path);
    }

    private int initX, initY;
    private int  textX, textY;
    /**
     * Initializes the button's initial position.
     * This is done for the animations to work properly - for them to stick to the same position.
     */
    public void initPos() {
        initX = getX();
        initY = getY();

        textX = getX() + width/2;
        textY = getY() + height/2 + 7;
    }

    public int getInitX() {
        return initX;
    }
    public int getInitY() {
        return initY;
    }

    public Point getCenter() {
        return ClientUtil.getInitCenter(initX, initY, getSize(), width);
    }
    public void moveToCenter() {
        final Point center = getCenter();
        setPosition(center.x, center.y);
    }


    
    public NoteNotation getNotation() {
        return NoteNotation.NONE;
    }


    public void init() {
        initPos();
        setLabelSupplier(labelSupplier);
    }

    public boolean isPlaying() {
        return noteAnimation.isPlaying();
    }


    //#region Rendering

    @Override
    public void renderWidget(PoseStack stack, int mouseX, int mouseY, float partialTick) {
        rings.removeIf((ring) -> !ring.isPlaying());
        rings.forEach((ring) -> ring.render(stack));

        renderNoteButton(stack, instrumentScreen.getThemeLoader());

        noteAnimation.update();
    }
    protected void renderNoteButton(PoseStack stack, final InstrumentThemeLoader themeLoader) {
        renderNote(stack, themeLoader);
        renderSymbol(stack, themeLoader);
        renderLabel(stack, themeLoader);
        
        renderAccidentals(stack);
    }

    protected void renderNote(final PoseStack stack, final InstrumentThemeLoader themeLoader) {
        // width = full color, width * 2 = border, 0 = normal
        int blitOffset =
            isPlaying() ?
                foreignPlaying ?
                    (width * 2)
                : width
            : isHoveredOrFocused() ?
                (width * 2)
            : 0;

        ClientUtil.displaySprite(noteBgLocation);
        blit(stack,
            this.getX(), this.getY(),
            blitOffset, 0,
            width, height,
            width*3, height
        );
    }
    // "Symbol" so that I can call the above "Note"
    protected void renderSymbol(final PoseStack stack, final InstrumentThemeLoader themeLoader) {
        final int noteWidth = width/2, noteHeight = height/2;
        
        ClientUtil.setShaderColor((isPlaying() && !foreignPlaying)
            ? themeLoader.getPressedNoteTheme()
            : themeLoader.getLabelTheme()
        );

        ClientUtil.displaySprite(noteLocation);

        blit(stack,
            this.getX() + noteWidth/2, this.getY() + noteHeight/2,
            //NOTE: I have no clue whatsoever how on earth these 1.025 and .9 multipliers actually work.
            // Like seriously wtf why fkuaherjgaeorg i hate maths
            //NOTE: Moved said numbers to the randomAss vars
            noteWidth * noteTextureRow * randomAssMultiplier2, 0,
            noteWidth, noteHeight,
            (int)(noteWidth * (noteTextureWidth / rowsInNoteTexture) * randomAssMultiplier1), height/2
        );

        ClientUtil.resetShaderColor();
    }
    protected void renderLabel(final PoseStack stack, final InstrumentThemeLoader themeLoader) {
        //FIXME: All text rendered this way are making their way to the top of
        // the render stack, for some reason
        drawCenteredString(stack,
            minecraft.font, getMessage(),
            textX, textY,
            ((isPlaying() && !foreignPlaying)
                ? themeLoader.getPressedNoteTheme()
                : themeLoader.getNoteTheme()
            ).getRGB()
        );
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
            getX() - 9 + offsetX, getY() - 6 + offsetY,
            // Handle sharp imperfections
            isPlaying() ? textureWidth/2 : 0, (spritePartHeight) * index - index,
            textureWidth/2,  spritePartHeight + ((index == 1) ? 3 : 0),
            textureWidth - (((index != 0) && isPlaying()) ? 1 : 0), textureHeight
        );
    }

    //#endregion


    public boolean locked = false;
    public void play() {
        if (locked)
            return;
        
        sound.playLocally(instrumentScreen.getPitch());

        // Send sound packet to server
        ModPacketHandler.sendToServer(
            new InstrumentPacket(
                sound, instrumentScreen.getPitch(),
                instrumentScreen.interactionHand,
                instrumentScreen.getInstrumentId(), getIdentifier()
            )
        );

        playNoteAnimation(false);

        locked = true;
    }
    @Override
    public void onPress() {
        play();
    }

    private boolean foreignPlaying = false;

    public void playNoteAnimation(final boolean isForeign) {
        foreignPlaying = isForeign;

        noteAnimation.play(isForeign);
        if (ModClientConfigs.EMIT_RING_ANIMATION.get())
            rings.add(new NoteRing(this, isForeign));
    }


    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        setFocused(false);
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }


    @Override
    public void playDownSound(SoundManager pHandler) {}
    

    @Override
    protected void updateWidgetNarration(final NarrationElementOutput neo) {
        neo.add(NarratedElementType.TITLE, getMessage());
    }


    /**
     * <p>Check whether an object is equal to this {@link NoteButton}.</p>
     * 
     * An object will only be equal to this note if it is of type {@link NoteButton}
     * and both their identifiers {@link NoteButtonIdentifier#matches match}
     */
    @Override
    public boolean equals(Object obj) {
        return getIdentifier().matches(obj);
    }

}