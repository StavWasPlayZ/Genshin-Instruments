package com.cstav.genshinstrument.client.gui.screens.instrument.partial;

import java.util.function.Supplier;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.label.NoteLabelSupplier;
import com.cstav.genshinstrument.sounds.NoteSound;

import net.minecraft.resources.ResourceLocation;

public class NoteGridButton extends NoteButton {

    public final int row, column,
        maxRows;

    public NoteGridButton(int row, int column, NoteSound sound, NoteLabelSupplier labelSupplier,
            ResourceLocation noteResourcesLocation, int maxRows,
            Supplier<Integer> colorTheme, Supplier<Integer> pressedThemeColor) {
        super(sound, labelSupplier, noteResourcesLocation, row, maxRows, colorTheme, pressedThemeColor);
        
        
        this.row = row;
        this.column = column;

        this.maxRows = maxRows;
    }
    
}
