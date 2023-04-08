package com.cstav.genshinstrument.client.gui.screens.instrument.vintageLyre;

import java.util.function.Supplier;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.NoteGrid;
import com.cstav.genshinstrument.sounds.NoteSound;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VintageNoteGrid extends NoteGrid {

    public VintageNoteGrid(int rows, int columns, NoteSound[] sounds, ResourceLocation noteResourcesLocation,
            Supplier<Integer> colorThemeSupplier, Supplier<Integer> pressedThemeSupplier) {
        super(rows, columns, sounds, noteResourcesLocation, colorThemeSupplier, pressedThemeSupplier);
    }

    @Override
    protected NoteButton createNote(int row, int column, NoteSound[] sounds, ResourceLocation noteResourceLocation,
            Supplier<Integer> colorThemeSupplier, Supplier<Integer> pressedThemeSupplier) {
        return new VintageNoteButton(row, column, 
            getSoundAt(sounds, row, column), getLabelSupplier(),
            noteResourceLocation, colorThemeSupplier, pressedThemeSupplier
        );
    }
    
}
