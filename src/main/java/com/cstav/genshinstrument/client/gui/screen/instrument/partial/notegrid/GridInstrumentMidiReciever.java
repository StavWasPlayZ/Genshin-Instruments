package com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.midi.InstrumentMidiReciever;

public class GridInstrumentMidiReciever extends InstrumentMidiReciever {

    public GridInstrumentMidiReciever(AbstractGridInstrumentScreen instrument) {
        super(instrument);
    }

    @Override
    public boolean allowMidiOverflow() {
        return true;
    }


    @Override
    protected NoteButton handleMidiPress(int note, int key) {
        final AbstractGridInstrumentScreen instrumentScreen = (AbstractGridInstrumentScreen)instrument;

        final int layoutNote = note % 12;
        final boolean higherThan3 = layoutNote > key + 4;

        // Handle transposition
        final boolean shouldSharpen = shouldSharpen(layoutNote, key);
        final boolean shouldFlatten = shouldFlatten(shouldSharpen);

        transposeMidi(shouldSharpen, shouldFlatten);


        final int playedNote = note + (shouldFlatten ? 1 : shouldSharpen ? -1 : 0);

        final int currNote = ((playedNote + (higherThan3 ? 1 : 0)) / 2)
            // 12th note should go to the next column
            + playedNote / (12 + key);

        return instrumentScreen.getNoteButton(
            currNote % instrumentScreen.rows(),
            currNote / instrumentScreen.rows()
        );

    }

}
