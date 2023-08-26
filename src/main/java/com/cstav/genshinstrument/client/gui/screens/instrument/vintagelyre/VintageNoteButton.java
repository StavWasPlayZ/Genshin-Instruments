package com.cstav.genshinstrument.client.gui.screens.instrument.vintagelyre;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteNotation;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.AbstractGridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.NoteGridButton;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VintageNoteButton extends NoteGridButton {

    public VintageNoteButton(int row, int column, AbstractGridInstrumentScreen instrumentScreen) {
        super(row, column, instrumentScreen);
    }

    
    private boolean isDefaultFlat() {
        return (row == 6) || (row == 2) ||
            ((column == 0) && ((row == 1) || (row == 5)));
    }

    @Override
    public NoteNotation getNotation() {
        return ModClientConfigs.ACCURATE_NOTES.get()
            ? super.getNotation()
            : isDefaultFlat() ? NoteNotation.FLAT : NoteNotation.NONE;
    }
    
}
