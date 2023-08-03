package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note;

import java.awt.Point;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.ClientUtil;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.buttonidentifier.DefaultNoteButtonIdentifier;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.instrument.InstrumentPacket;
import com.cstav.genshinstrument.sound.NoteSound;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The abstract implementation of an instrument's note button.
 * @param <T> The type of this button's identifier
 */
@OnlyIn(Dist.CLIENT)
public abstract class NoteButton extends AbstractButton {    

    protected final Minecraft minecraft = Minecraft.getInstance();

    /**
     * Returns the UI identifier of this button.
     */
    public NoteButtonIdentifier getIdentifier() {
        return new DefaultNoteButtonIdentifier(getSound(), getPitch(), false);
    }


    public final AbstractInstrumentScreen instrumentScreen;
    
    private NoteSound sound;
    private NoteLabelSupplier labelSupplier;

    protected NoteButtonRenderer noteRenderer;
    protected abstract NoteButtonRenderer initNoteRenderer();


    public NoteButton(NoteSound sound,
            NoteLabelSupplier labelSupplier, AbstractInstrumentScreen instrumentScreen, int pitch) {

        super(0, 0, 42, 42, Component.empty());

        width = height = instrumentScreen.getNoteSize();

        this.sound = sound;
        this.labelSupplier = labelSupplier;
        this.instrumentScreen = instrumentScreen;
        this.pitch = pitch;
    }
    public NoteButton(NoteSound sound,
            NoteLabelSupplier labelSupplier, AbstractInstrumentScreen instrumentScreen) {
                
        this(sound, labelSupplier, instrumentScreen, instrumentScreen.getPitch());
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
    }


    private int initX, initY;
    /**
     * Initializes the button's initial position.
     * This is done for the animations to work properly - for them to stick to the same position.
     */
    public void initPos() {
        initX = x;
        initY = y;

        noteRenderer.setLabelX(x + width/2);
        noteRenderer.setLabelY(y + height/2 + 7);
    }

    public int getInitX() {
        return initX;
    }
    public int getInitY() {
        return initY;
    }

    public Point getCenter() {
        return ClientUtil.getInitCenter(initX, initY, instrumentScreen.getNoteSize(), width);
    }
    public void moveToCenter() {
        final Point center = getCenter();
        x = center.x;
        y = center.y;
    }

    private int pitch;
    public int getPitch() {
        return pitch;
    }
    public void setPitch(final int pitch) {
        this.pitch = NoteSound.clampPitch(pitch);
        updateNoteLabel();
    }
    
    public NoteNotation getNotation() {
        return NoteNotation.NONE;
    }


    public void init() {
        noteRenderer = initNoteRenderer();
        initPos();
        setLabelSupplier(labelSupplier);
    }

    public boolean isPlaying() {
        return noteRenderer.noteAnimation.isPlaying();
    }


    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTick) {
        noteRenderer.render(stack, mouseX, mouseY, partialTick, instrumentScreen.getThemeLoader());
    }


    public boolean locked = false;
    public void play() {
        if (locked)
            return;
        
        sound.playLocally(getPitch());


        final Player player = minecraft.player;

        final BlockPos pos = InstrumentOpenProvider.isItem(player)
            ? player.blockPosition()
            : InstrumentOpenProvider.getBlockPos(player);

        // Send sound packet to server
        ModPacketHandler.sendToServer(
            new InstrumentPacket(pos,
                sound, getPitch(),
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


    public void playNoteAnimation(final boolean isForeign) {
        noteRenderer.playNoteAnimation(isForeign);
    }


    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        setFocused(false);
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }


    @Override
    public void playDownSound(SoundManager pHandler) {}
    

    @Override
    public void updateNarration(final NarrationElementOutput neo) {
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