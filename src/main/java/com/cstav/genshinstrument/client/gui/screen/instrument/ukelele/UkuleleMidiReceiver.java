package com.cstav.genshinstrument.client.gui.screen.instrument.ukelele;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid.GridInstrumentMidiReceiver;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid.GridInstrumentScreen;

public class UkuleleMidiReceiver extends GridInstrumentMidiReceiver {
    public UkuleleMidiReceiver(GridInstrumentScreen instrument) {
        super(instrument);
    }

    // Do not allow overflowing past the 3rd octave (chords) OR extended 2nd octave.
    //TODO: Perhaps allow chords overflow to but also update note labels.
    @Override
    protected int maxMidiOverflow() {
        return maxMidiNote();
    }
}
