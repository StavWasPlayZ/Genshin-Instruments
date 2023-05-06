package com.cstav.genshinstrument.client.gui.screens.instrument.vintagelyre;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteGrid;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VintageNoteGrid extends NoteGrid {

    public VintageNoteGrid(final ItemStack vintageLyre, int rows, int columns, NoteSound[] sounds, VintageLyreScreen instrumentScreen) {
        super(vintageLyre, rows, columns, sounds, instrumentScreen);
    }

    @Override
    protected NoteButton createNote(int row, int column, AbstractInstrumentScreen screen) {
        return new VintageNoteButton(instrument, row, column, 
            getSoundAt(getNoteSounds(), row, column), getLabelSupplier(), screen
        );
    }
    
}
