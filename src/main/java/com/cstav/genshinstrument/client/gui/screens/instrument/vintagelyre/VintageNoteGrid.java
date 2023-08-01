package com.cstav.genshinstrument.client.gui.screens.instrument.vintagelyre;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.NoteGrid;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VintageNoteGrid extends NoteGrid {

    public VintageNoteGrid(NoteSound[] sounds, VintageLyreScreen instrumentScreen) {
        super(sounds, instrumentScreen);
    }

    @Override
    protected NoteButton createNote(int row, int column) {
        return new VintageNoteButton(row, column, 
            getSoundAt(getNoteSounds(), row, column), getLabelSupplier()
        , instrumentScreen);
    }
    
}
