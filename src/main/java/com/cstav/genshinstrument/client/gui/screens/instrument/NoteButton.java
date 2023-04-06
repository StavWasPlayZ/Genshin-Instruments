package com.cstav.genshinstrument.client.gui.screens.instrument;

import java.util.function.Supplier;

import com.cstav.genshinstrument.client.gui.screens.instrument.label.NoteLabelSupplier;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.packets.lyre.InstrumentPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoteButton extends Button {
    public static final String NOTE_FILENAME = "note.png", NOTE_BG_FILENAME = "note_bg.png";
    private static final float PRESS_ANIM_SECS = .15f;
    private static final int TARGET_SCALE_AMOUNT = 8;
    
    public static int getNoteTextureWidth() {
        return 56;
    }

    @SuppressWarnings("resource")
    public static int getSize() {
        final int guiScale = Minecraft.getInstance().options.guiScale().get();

        return switch (guiScale) {
            case 1 -> 35;
            case 2 -> 46;
            case 3 -> 48;
            case 4 -> 43;
            default -> guiScale * 18;
        };
    }
    

    protected final Minecraft minecraft = Minecraft.getInstance();

    protected final SoundEvent sound;
    protected final int row, column;
    protected float pitch;
    protected final Supplier<Integer> colorThemeSupplier, pressedColorThemeSupplier;
    protected final ResourceLocation noteLocation, noteBgLocation;
    
    public NoteButton(int row, int column, SoundEvent sound, NoteLabelSupplier labelSupplier,
      ResourceLocation noteResourcesLocation, Supplier<Integer> colorTheme, Supplier<Integer> pressedThemeColor) {
        super(Button.builder(labelSupplier.get(row, column), (iAmADissapointmentAndAFailureToMyParents) -> {})
            .size(getSize(), getSize())
        );

        this.row = row;
        this.column = column;
        this.colorThemeSupplier = colorTheme;
        this.sound = sound;

        this.pressedColorThemeSupplier = pressedThemeColor;
        this.noteLocation = new ResourceLocation(noteResourcesLocation.getNamespace(),
            noteResourcesLocation.getPath()+"/"+NOTE_FILENAME);
        this.noteBgLocation = new ResourceLocation(noteResourcesLocation.getNamespace(),
            noteResourcesLocation.getPath()+"/"+NOTE_BG_FILENAME);

        //TODO: Load pitch from preferences
        pitch = 1;
    }
    public void setLabel(final NoteLabelSupplier labelSupplier) {
        setMessage(labelSupplier.get(row, column));
    }
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    private int initX, initY;
    public void initPos() {
        initX = getX();
        initY = getY();
    }


    private NoteAnimationState animState = NoteAnimationState.IDLE;
    private int animTime;

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        // Button
        displaySprite(noteBgLocation);

        pressAnimationFrame();

        GuiComponent.blit(pPoseStack,
            this.getX(), this.getY(),
            (animState == NoteAnimationState.IDLE) ? 0 : width, 0,
            width, height,
            width*2, height
        );

        // Note
        displaySprite(noteLocation);
        final int noteWidth = width/2, noteHeight = height/2;

        GuiComponent.blit(pPoseStack,
            this.getX() + noteWidth/2, this.getY() + noteHeight/2,
            //NOTE: I have no clue whatsoever how on earth these 1.025 and .9 multipliers actually work.
            // Like seriously wtf why fkuaherjgaeorg i hate maths
            noteWidth * row * 1.025f, (animState == NoteAnimationState.IDLE) ? 0 : noteHeight,
            noteWidth, noteHeight,
            (int)(noteWidth * (getNoteTextureWidth() / AbstractInstrumentScreen.ROWS) * .9f), height
        );
        //NOTE: No idea how to go about mapping textures.
        // Possibility to map a background and ring color for note, and theme color for
        // text and note.

        // Label
        drawCenteredString(
            pPoseStack, minecraft.font, getMessage(),
            getX() + width/2, getY() + height/2 + 7,
            (animState == NoteAnimationState.IDLE) ? colorThemeSupplier.get() : pressedColorThemeSupplier.get()
        );

        RenderSystem.disableBlend();
    }
    private static void displaySprite(final ResourceLocation location) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, location);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
    }

    private void pressAnimationFrame() {
        if (animState == NoteAnimationState.IDLE)
            return;

        final int fps = minecraft.getFps();
        final float targetTime = fps * PRESS_ANIM_SECS/2;
            
        if (animTime++ >= targetTime) {
            animTime = 0;

            if (animState == NoteAnimationState.UP) {
                resetAnimVars();
                animState = NoteAnimationState.IDLE;

                return;
            }
            animState = NoteAnimationState.UP;
        }
        else {
            final double scaleReduction = TARGET_SCALE_AMOUNT / targetTime;

            // Assuming the shape will always be a square
            if (animState == NoteAnimationState.UP)
                width = height += scaleReduction * 2;
            else
                width = height -= scaleReduction;
        }
        
        setPosition(
            (getSize() - width) / 2 + initX,
            (getSize() - width) / 2 + initY
        );
    }
    private void resetAnimVars() {
        animTime = 0;
        width = height = getSize();
        setPosition(initX, initY);
    }


    public boolean locked = false;
    public void play(final boolean playLocally) {
        if (locked)
            return;
        
        if (playLocally)
            playDownSound(minecraft.getSoundManager());
        
        ModPacketHandler.sendToServer(new InstrumentPacket(sound, pitch));
        
        locked = true;

        animState = NoteAnimationState.DOWN;
        resetAnimVars();
    }
    @Override
    public void onClick(double pMouseX, double pMouseY) {
        play(false);
    }
    @Override
    public void playDownSound(SoundManager pHandler) {
        pHandler.play(new SimpleSoundInstance(
            sound.getLocation(), SoundSource.RECORDS,
            1, pitch, SoundInstance.createUnseededRandom(),
            false, 0, SoundInstance.Attenuation.NONE,
            0, 0, 0, true
        ));
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        locked = false;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }


    @Override
    public boolean changeFocus(boolean pFocus) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    private static enum NoteAnimationState {
        UP, DOWN, IDLE
    }
}