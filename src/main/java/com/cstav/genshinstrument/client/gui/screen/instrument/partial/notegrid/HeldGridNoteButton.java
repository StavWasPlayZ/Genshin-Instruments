package com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held.HeldNoteButtonRenderer;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held.IHoldableNoteButton;

public class HeldGridNoteButton extends NoteGridButton implements IHoldableNoteButton {
    private boolean isHeld = false;

    public HeldGridNoteButton(int row, int column, GridInstrumentScreen instrumentScreen) {
        super(row, column, instrumentScreen);
    }
    public HeldGridNoteButton(int row, int column, GridInstrumentScreen instrumentScreen, int pitch) {
        super(row, column, instrumentScreen, pitch);
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
    public boolean play() {
        if (!super.play())
            return false;

        isHeld = true;
        return true;
    }

    @Override
    protected HeldNoteButtonRenderer<HeldGridNoteButton> initNoteRenderer() {
        return new HeldNoteButtonRenderer<>(this, this::getTextureAtRow);
    }
}
