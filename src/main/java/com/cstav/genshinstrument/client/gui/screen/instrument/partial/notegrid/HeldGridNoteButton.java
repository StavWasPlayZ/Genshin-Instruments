package com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held.HeldNoteButtonRenderer;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held.IHoldableNoteButton;
import com.cstav.genshinstrument.sound.HeldNoteSound;

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

    @SuppressWarnings("unchecked")
    @Override
    public void release() {
        super.release();
        isHeld = false;
        ((HeldNoteButtonRenderer<HeldGridNoteButton>) noteRenderer).playRelease();
    }

    @Override
    protected void playSound() {
        isHeld = true;
        getHeldNoteSound().startPlaying();
    }

    @Override
    protected HeldNoteButtonRenderer<HeldGridNoteButton> initNoteRenderer() {
        return new HeldNoteButtonRenderer<>(this, this::getTextureAtRow);
    }
}
