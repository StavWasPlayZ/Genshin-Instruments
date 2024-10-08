package com.cstav.genshinstrument.client.gui.screen.instrument.vintagelyre;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteNotation;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.grid.NoteGridButton;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VintageNoteButton extends NoteGridButton {

    public VintageNoteButton(int row, int column, VintageLyreScreen instrumentScreen) {
        super(row, column, instrumentScreen);
    }

    @Override
    public VintageLyreScreen gridInstrument() {
        return (VintageLyreScreen) instrumentScreen;
    }

    
    public boolean isDefaultFlat() {
        return (row == 6) || (row == 2) ||
            ((column == 0) && ((row == 1) || (row == 5)));
    }

    @Override
    public NoteNotation getNotation() {
        return ModClientConfigs.ACCURATE_NOTES.get()
            ? super.getNotation()
            : isDefaultFlat() ? NoteNotation.FLAT : NoteNotation.NONE;
    }


    /**
     * @return Whether this note should be sharpened
     * for a Vintage normalization
     */
    public boolean shouldSharpen() {
        if (!gridInstrument().shouldSoundNormalize())
            return false;

        // Only normalize the Genshin flattened notes
        return isDefaultFlat();
    }

    @Override
    public int getPitch() {
        return super.getPitch() + (shouldSharpen() ? 1 : 0);
    }

}
