package com.cstav.genshinstrument.client.gui.screen.instrument.partial;

import com.cstav.genshinstrument.sound.held.HeldNoteSound;

public interface IHeldInstrumentScreen {
    HeldNoteSound[] getHeldNoteSounds();
    void setHeldNoteSounds(final HeldNoteSound[] heldNoteSounds);
}
