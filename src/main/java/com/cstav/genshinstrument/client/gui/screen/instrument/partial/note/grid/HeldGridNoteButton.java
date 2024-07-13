package com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.grid;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid.GridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held.HeldNoteButtonRenderer;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held.IHoldableNoteButton;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSound;

public class HeldGridNoteButton extends NoteGridButton implements IHoldableNoteButton {
    private boolean isHeld = false;
    private HeldNoteSound heldNoteSound;

    public HeldGridNoteButton(int row, int column, GridInstrumentScreen instrumentScreen, HeldNoteSound[] heldNoteSounds) {
        super(row, column, instrumentScreen);
        this.heldNoteSound = heldNoteSounds[posToIndex()];
    }
    public HeldGridNoteButton(int row, int column, GridInstrumentScreen instrumentScreen, int pitch, HeldNoteSound[] heldNoteSounds) {
        super(row, column, instrumentScreen, pitch);
        this.heldNoteSound = heldNoteSounds[posToIndex()];
    }

    @Override
    public void setSound(NoteSound sound) {
        GInstrumentMod.LOGGER.warn("Attempted to set the sound of a held note button; ignoring");
    }


    @Override
    public HeldNoteSound getHeldNoteSound() {
        return heldNoteSound;
    }
    @Override
    public void setHeldNoteSound(HeldNoteSound heldNoteSound) {
        this.heldNoteSound = heldNoteSound;
    }

    @Override
    public boolean isPlaying() {
        return isHeld();
    }

    @Override
    public boolean isHeld() {
        return isHeld;
    }
    @Override
    public void setHeld(boolean held) {
        isHeld = held;
    }

    @Override
    public void release() {
        releaseHeld(false);
    }
    @Override
    public void releaseHeld(int notePitch, boolean targetPitch, HeldNoteSound heldSound, boolean playAnimation) {
        super.release();
        IHoldableNoteButton.super.releaseHeld(notePitch, targetPitch, heldSound, playAnimation);
    }

    @Override
    protected void playLocalSound(final NoteSound sound, final int pitch) {
        isHeld = true;
        playLocalHeldSound(sound, pitch);
    }

    @Override
    protected HeldNoteButtonRenderer initNoteRenderer() {
        return new HeldNoteButtonRenderer(this, this::getTextureAtRow);
    }
}
