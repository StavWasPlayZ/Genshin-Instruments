package com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held;

import com.cstav.genshinstrument.sound.held.HeldNoteSound;

public interface IHoldableNoteButton {
    public boolean isHeld();
    public HeldNoteSound getHeldNoteSound();
    public void setHeldNoteSound(final HeldNoteSound sound);
}
