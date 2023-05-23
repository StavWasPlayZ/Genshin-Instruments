package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note;

import java.awt.Color;

import com.cstav.genshinstrument.client.gui.GuiAnimationController;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.packets.instrument.InstrumentPacket;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.util.CommonUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoteButton extends AbstractButton {
    public static final String
        // Local - in note resource directory
        NOTE_FILENAME = "note.png", NOTE_BG_FILENAME = "note_bg.png",
        // Global - in instrument directory
        RING_GLOB_FILENAME = "ring.png"
    ;


    @SuppressWarnings("resource")
    public static int getSize() {
        final int guiScale = Minecraft.getInstance().options.guiScale().get();

        return switch (guiScale) {
            case 0 -> 40;
            case 1 -> 35;
            case 2 -> 46;
            case 3 -> 48;
            case 4 -> 43;
            default -> guiScale * 18;
        };
    }
    

    protected final Minecraft minecraft = Minecraft.getInstance();
    private final GuiAnimationController noteAnimator = new NoteButtonAnimationController(.15f, 9, this);
    
    public NoteSound sound;
    public final AbstractInstrumentScreen instrumentScreen;

    protected final int noteTextureRow, rowsInNoteTexture;
    protected final Color colorTheme, pressedColorTheme;
    protected final ResourceLocation rootLocation,
        noteLocation, noteBgLocation, ringLocation;

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
        colorTheme = instrumentScreen.getThemeLoader().getNoteTheme();
        pressedColorTheme = instrumentScreen.getThemeLoader().getPressedNoteTheme();

        this.noteTextureRow = noteTextureRow;
        this.rowsInNoteTexture = rowsInNoteTexture;


        rootLocation = instrumentScreen.getResourceFromRoot("note");

        noteLocation = getResourceFromRoot(NOTE_FILENAME);
        noteBgLocation = getResourceFromRoot(NOTE_BG_FILENAME);
        ringLocation = instrumentScreen.getResourceFromGlob(RING_GLOB_FILENAME);
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
        setMessage(labelSupplier.get(this));
    }
    public NoteLabelSupplier getLabelSupplier() {
        return labelSupplier;
    }


    public NoteSound getSound() {
        return sound;
    }
    public void setSound(final NoteSound sound) {
        this.sound = sound;
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

    public void init() {
        initPos();
        setLabelSupplier(labelSupplier);
    }

    public boolean isPlaying() {
        return noteAnimator.isPlaying();
    }


    @Override
    public void renderWidget(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {

        // Ring
        RenderSystem.setShaderColor(
            colorTheme.getRed() / 255f,
            colorTheme.getGreen() / 255f,
            colorTheme.getBlue() / 255f,
            .75f
        );
        displaySprite(ringLocation);

        GuiComponent.blit(pPoseStack,
            initX, initY,
            0, 0,
            getSize(), getSize(),
            getSize(), getSize()
        );

        // Reset render state
        RenderSystem.setShaderColor(1, 1, 1, 1);

        
        // Button
        displaySprite(noteBgLocation);

        GuiComponent.blit(pPoseStack,
            this.getX(), this.getY(),
            isPlaying() ? (width) : isHoveredOrFocused() ? (width * 2) : 0, 0,
            width, height,
            width*3, height
        );

        // Note
        displaySprite(noteLocation);
        final int noteWidth = width/2, noteHeight = height/2;

        GuiComponent.blit(pPoseStack,
            this.getX() + noteWidth/2, this.getY() + noteHeight/2,
            //NOTE: I have no clue whatsoever how on earth these 1.025 and .9 multipliers actually work.
            // Like seriously wtf why fkuaherjgaeorg i hate maths
            //NOTE: Moved said numbers to the randomAss vars
            noteWidth * noteTextureRow * randomAssMultiplier2, isPlaying() ? noteHeight : 0,
            noteWidth, noteHeight,
            (int)(noteWidth * (noteTextureWidth / rowsInNoteTexture) * randomAssMultiplier1), height
        );

        // Label
        //FIXME: All text rendered this way are making their way to the top of
        // the render stack, for some reason
        drawCenteredString(
            pPoseStack, minecraft.font, getMessage(),
            textX, textY,
            (isPlaying() ? pressedColorTheme : colorTheme).getRGB()
        );

        // Reset render state
        RenderSystem.disableBlend();

        // Apply animations
        noteAnimator.update();
        // ringAnimationFrame();
    }
    protected static void displaySprite(final ResourceLocation location) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, location);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
    }



    public boolean locked = false;
    public void play() {
        if (locked)
            return;
        
        // Play sound locally
        minecraft.getSoundManager().play(new SimpleSoundInstance(
            sound.getByPreference().getLocation(), SoundSource.RECORDS,
            1, instrumentScreen.getPitch(), SoundInstance.createUnseededRandom(),
            false, 0, SoundInstance.Attenuation.NONE,
            0, 0, 0, true
        ));

        // Send sound packet to server
        ModPacketHandler.sendToServer(
            new InstrumentPacket(sound, instrumentScreen.getPitch(), instrumentScreen.interactionHand)
        );
        

        noteAnimator.start();

        locked = true;
    }
    @Override
    public void onPress() {
        play();
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

}