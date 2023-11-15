package com.cstav.genshinstrument.client.gui.screen.instrument.partial.note;

import java.awt.Point;

import org.slf4j.Logger;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.client.util.ClientUtil;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.buttonidentifier.DefaultNoteButtonIdentifier;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.instrument.InstrumentPacket;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.util.LabelUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The abstract implementation of an instrument's note button.
 * @param <T> The type of this button's identifier
 */
@OnlyIn(Dist.CLIENT)
public abstract class NoteButton extends AbstractButton {
    private static Logger LOGGER = LogUtils.getLogger();

    protected final Minecraft minecraft = Minecraft.getInstance();

    /**
     * Returns the UI identifier of this button.
     */
    public NoteButtonIdentifier getIdentifier() {
        return new DefaultNoteButtonIdentifier(getSound(), getPitch(), false);
    }


    public final InstrumentScreen instrumentScreen;
    
    private NoteSound sound;
    private NoteLabelSupplier labelSupplier;

    protected NoteButtonRenderer noteRenderer;
    protected abstract NoteButtonRenderer initNoteRenderer();


    public NoteButton(NoteSound sound, NoteLabelSupplier labelSupplier, InstrumentScreen instrumentScreen,
            int pitch) {

        super(0, 0, 42, 42, Component.empty());

        width = height = instrumentScreen.getNoteSize();

        this.sound = sound;
        this.labelSupplier = labelSupplier;
        this.instrumentScreen = instrumentScreen;
        this.pitch = pitch;
    }
    public NoteButton(NoteSound sound, NoteLabelSupplier labelSupplier, InstrumentScreen instrumentScreen) {
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
        initX = getX();
        initY = getY();

        noteRenderer.setLabelX(getX() + width/2);
        noteRenderer.setLabelY(getY() + height/2 + 7);
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
        setPosition(center.x, center.y);
    }

    private int pitch;
    public int getPitch() {
        return pitch;
    }
    public void setPitch(final int pitch) {
        this.pitch = NoteSound.clampPitch(pitch);
        updateNoteLabel();
    }
    
    // Note labeling
    public NoteNotation getNotation() {
        return ModClientConfigs.ACCURATE_NOTES.get()
            ? NoteNotation.getNotation(getNoteName())
            : NoteNotation.NONE;
    }

    public String getFormattedNoteName() {
        return LabelUtil.formatNoteName(getNoteName(), true);
    }
    public String getNoteName() {
        if (instrumentScreen.noteLayout() == null)
            return "";

        return LabelUtil.getNoteName(instrumentScreen.getPitch(), instrumentScreen.noteLayout(), getNoteOffset());
    }
    /**
     * Defines the offset of this note relative to the this screen's {@link InstrumentScreen#noteLayout() note layout}
     */
    public abstract int getNoteOffset();


    public void init() {
        noteRenderer = initNoteRenderer();
        initPos();
        setLabelSupplier(labelSupplier);
    }

    public boolean isPlaying() {
        return noteRenderer.noteAnimation.isPlaying();
    }


    @Override
    public void renderWidget(PoseStack stack, int mouseX, int mouseY, float partialTick) {
        noteRenderer.render(stack, mouseX, mouseY, partialTick, instrumentScreen.getThemeLoader());
    }


    public boolean locked = false;
    public void play() {
        if (locked)
            return;
        
        sound.playLocally(getPitch(), instrumentScreen.volume());
        sendNotePlayPacket();
        playNoteAnimation(false);

        locked = true;
    }
    @Override
    public void onPress() {
        play();
    }

    protected void sendNotePlayPacket() {
        ModPacketHandler.sendToServer(new InstrumentPacket(this));
    }


    public void playNoteAnimation(final boolean isForeign) {
        noteRenderer.playNoteAnimation(isForeign);
    }


    /**
     * @return The index position of this note relative to the note {@code C}
     */
    public int getABCOffset() {
        final ResourceLocation instrumentId = instrumentScreen.getInstrumentId();
        final String noteName = getNoteName();

        if (noteName.isEmpty()) {
            LOGGER.warn("Cannot get ABC offset for an instrument without a note layout! ("+instrumentId+")");
            return 0;
        }

        final char note = noteName.charAt(0);

        for (int i = 0; i < LabelUtil.ABC.length; i++)
            if (note == LabelUtil.ABC[i])
                return i;

        LOGGER.warn("Could not get note "+note+" for instrument "+instrumentScreen.getInstrumentId()+"!");
        return 0;
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
        return (obj instanceof NoteButton btn) && getIdentifier().matches(btn);
    }

}