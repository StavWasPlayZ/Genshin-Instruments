package com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.midi.InstrumentMidiReceiver;
import com.cstav.genshinstrument.sound.NoteSound;
import org.jetbrains.annotations.Nullable;

public class GridInstrumentMidiReceiver extends InstrumentMidiReceiver {

    public GridInstrumentMidiReceiver(GridInstrumentScreen instrument) {
        super(instrument);
    }
    protected GridInstrumentScreen gridInstrument() {
        return (GridInstrumentScreen) instrument;
    }

    @Override
    public boolean allowMidiOverflow() {
        return true;
    }

    protected int maxMidiNote() {
        return NoteSound.MAX_PITCH * gridInstrument().columns();
    }

    @Override
    protected NoteButton getHighestNote() {
        return gridInstrument().getNoteButton(gridInstrument().rows() - 1, gridInstrument().columns() - 1);
    }
    @Override
    protected NoteButton getLowestNote() {
        return gridInstrument().getNoteButton(0, 0);
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

        // A sharpened/flattened note is still the same note - just pitched up/down.
        // Thus, go backwards/forwards to stay on the same note.

        int playedNote = note + (shouldFlatten ? 1 : shouldSharpen ? -1 : 0);

        playedNote = ((playedNote + (higherThan3 ? 1 : 0)) / 2)
            // 12th note should go to the next column
            + playedNote / (12 + key);

        return instrumentScreen.getNoteButtonByMIDINote(playedNote);
    }

}
