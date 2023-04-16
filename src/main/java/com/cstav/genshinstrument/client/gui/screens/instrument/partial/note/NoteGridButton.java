package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.sounds.NoteSound;

public class NoteGridButton extends NoteButton {

    public final int row, column,
        maxRows;

    public NoteGridButton(int row, int column, NoteSound sound, NoteLabelSupplier labelSupplier, int maxRows,
      AbstractInstrumentScreen instrumentScreen) {
        super(sound, labelSupplier, row, maxRows, instrumentScreen);
        
        
        this.row = row;
        this.column = column;

        this.maxRows = maxRows;
    }
    
}
