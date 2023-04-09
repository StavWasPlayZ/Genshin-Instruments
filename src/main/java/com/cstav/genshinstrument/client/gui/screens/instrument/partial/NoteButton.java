package com.cstav.genshinstrument.client.gui.screens.instrument.partial;

import java.util.UUID;
import java.util.function.Supplier;

import com.cstav.genshinstrument.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.label.NoteLabelSupplier;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.packets.lyre.InstrumentPacket;
import com.cstav.genshinstrument.sounds.NoteSound;
import com.cstav.genshinstrument.util.Util;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoteButton extends Button {
    public static final String NOTE_FILENAME = "note.png", NOTE_BG_FILENAME = "note_bg.png";
    private static final float PRESS_ANIM_SECS = .15f;
    private static final int TARGET_SCALE_AMOUNT = 9;
    
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
            default -> (guiScale == 0) ? 40 : (guiScale * 18);
        };
    }
    

    protected final Minecraft minecraft = Minecraft.getInstance();

    protected NoteSound sound;
    protected final int row, column;
    protected final Supplier<Integer> colorThemeSupplier, pressedColorThemeSupplier;
    protected final ResourceLocation rootLocation, noteLocation, noteBgLocation;
    
    public NoteButton(int row, int column, NoteSound sound, NoteLabelSupplier labelSupplier,
      ResourceLocation noteResourcesLocation, Supplier<Integer> colorTheme, Supplier<Integer> pressedThemeColor) {
        super(Button.builder(labelSupplier.get(row, column), (iAmADissapointmentAndAFailureToMyParents) -> {})
            .size(getSize(), getSize())
        );

        this.row = row;
        this.column = column;
        this.colorThemeSupplier = colorTheme;
        this.sound = sound;

        this.pressedColorThemeSupplier = pressedThemeColor;
        rootLocation = noteResourcesLocation;
        this.noteLocation = getResourceFromRoot(NOTE_FILENAME);
        this.noteBgLocation = getResourceFromRoot(NOTE_BG_FILENAME);
    }
    public void setLabel(final NoteLabelSupplier labelSupplier) {
        setMessage(labelSupplier.get(row, column));
    }

    public void setSound(final NoteSound sound) {
        this.sound = sound;
    }
    public NoteSound getSound() {
        return sound;
    }

    /**
     * @param path The resource to obtain from this note's directory
     * @see {@link AbstractInstrumentScreen#getResourceFrom(ResourceLocation, String)}
     */
    protected ResourceLocation getResourceFromRoot(final String path) {
        return Util.getResourceFrom(rootLocation, path);
    }

    protected int initX, initY;
    private int  textX, textY;
    public void initPos() {
        initX = getX();
        initY = getY();

        textX = getX() + width/2;
        textY = getY() + height/2 + 7;
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
            isPlaying() ? width : 0, 0,
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
            noteWidth * row * 1.025f, isPlaying() ? noteHeight : 0,
            noteWidth, noteHeight,
            (int)(noteWidth * (getNoteTextureWidth() / AbstractInstrumentScreen.ROWS) * .9f), height
        );
        //NOTE: No idea how to go about mapping textures.
        // Possibility to map a background and ring color for note, and theme color for
        // text and note.

        // Label
        //FIXME: All text rendered this way are making their way to the top of
        // the render stack, for some reason
        drawCenteredString(
            pPoseStack, minecraft.font, getMessage(),
            textX, textY,
            (isPlaying() ? pressedColorThemeSupplier : colorThemeSupplier).get()
        );

        RenderSystem.disableBlend();
    }
    protected static void displaySprite(final ResourceLocation location) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, location);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
    }
    protected boolean isPlaying() {
        return animState != NoteAnimationState.IDLE;
    }

    private double dSize;
    private void pressAnimationFrame() {
        if (!isPlaying())
            return;

        final int fps = minecraft.getFps();
        final float targetTime = fps * PRESS_ANIM_SECS;
            
        if (animTime++ >= targetTime/2) {
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
                dSize = dSize += scaleReduction * 1.5;
            else
                dSize = dSize -= scaleReduction * 1.5;
        }
        
        width = height = (int)dSize;
        setPosition(
            (getSize() - width) / 2 + initX,
            (getSize() - width) / 2 + initY
        );
    }
    private void resetAnimVars() {
        animTime = 0;
        dSize = width = height = getSize();
        setPosition(initX, initY);
    }


    public boolean locked = false;
    public void play(final boolean playLocally) {
        if (locked)
            return;
        
        if (playLocally)
            playDownSound(minecraft.getSoundManager());
        
        ModPacketHandler.sendToServer(new InstrumentPacket(sound));
        
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
        minecraft.getMusicManager().stopPlaying();

        pHandler.play(new SimpleSoundInstance(
            sound.getByPreference().getLocation(), SoundSource.RECORDS,
            1, sound.getPitch(), SoundInstance.createUnseededRandom(),
            false, 0, SoundInstance.Attenuation.NONE,
            0, 0, 0, true
        ));
    }
        /**
     * A method for packets to use for playing this note on the client's end.
     * If {@link Minecraft#player this player} is the same as the gives player,
     * the method will only stop the client's background music per preference.
     * @param playerUUID The UUID of the player who initiated the sound
     * @param pos The position at which the sound was fired from
     */
    public static void playNoteAtPos(final NoteSound sound, final UUID playerUUID, final BlockPos pos) {
        final Minecraft minecraft = Minecraft.getInstance();
        final Player player = minecraft.player;

        final double distanceFromPlayer = Math.sqrt(pos.distToCenterSqr((Position)player.position()));
        
        if (ModClientConfigs.STOP_MUSIC_ON_PLAY.get() && (distanceFromPlayer < NoteSound.STOP_SOUND_DISTANCE))
            minecraft.getMusicManager().stopPlaying();
        

        if (player.getUUID().equals(playerUUID))
            return;
            
        final Level level = minecraft.level;
        level.playLocalSound(pos,
            sound.getByPreference(distanceFromPlayer), SoundSource.RECORDS,
            1, sound.getPitch()
        , false);
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