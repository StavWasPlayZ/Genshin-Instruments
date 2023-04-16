package com.cstav.genshinstrument.client.gui.screens.instrument.vintageLyre;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteGrid;
import com.cstav.genshinstrument.sounds.NoteSound;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VintageNoteGrid extends NoteGrid {

    public VintageNoteGrid(int rows, int columns, NoteSound[] sounds, VintageLyreScreen instrumentScreen) {
        super(rows, columns, sounds, instrumentScreen);
    }

    @Override
    protected NoteButton createNote(int row, int column, AbstractInstrumentScreen screen) {
        return new VintageNoteButton(row, column, 
            getSoundAt(getNoteSounds(), row, column), getLabelSupplier(), screen
        );
    }
    
}
