package com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.midi.InstrumentMidiReceiver;
import org.jetbrains.annotations.Nullable;

public class GridInstrumentMidiReceiver extends InstrumentMidiReceiver {

    public GridInstrumentMidiReceiver(GridInstrumentScreen instrument) {
        super(instrument);
    }

    @Override
    public boolean allowMidiOverflow() {
        return true;
    }


    @Override
    protected @Nullable NoteButton handleMidiPress(int note, int key) {
        final GridInstrumentScreen instrumentScreen = (GridInstrumentScreen)instrument;

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

        //TODO fix 2 columned screens overflowing weirdly
        try {
            return instrumentScreen.getNoteButton(
                currNote % instrumentScreen.rows(),
                currNote / instrumentScreen.rows()
            );
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

}
