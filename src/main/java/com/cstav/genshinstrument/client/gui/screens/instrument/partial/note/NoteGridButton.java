package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoteGridButton extends NoteButton {

    public final int row, column,
        maxRows;

    public NoteGridButton(ItemStack instrument, int row, int column, NoteSound sound, NoteLabelSupplier labelSupplier, int maxRows,
      AbstractInstrumentScreen instrumentScreen) {
        super(instrument, sound, labelSupplier, row, maxRows, instrumentScreen);
        
        
        this.row = row;
        this.column = column;

        this.maxRows = maxRows;
    }
    
}
