package com.cstav.genshinstrument.client.gui.screens.instrument.vintagelyre;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteNotation;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.AbstractGridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.NoteGridButton;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VintageNoteButton extends NoteGridButton {

    public VintageNoteButton(int row, int column,
            NoteSound sound, NoteLabelSupplier labelSupplier, AbstractGridInstrumentScreen instrumentScreen) {
        super(row, column, sound, labelSupplier, instrumentScreen);
    }

    
    private boolean isDefaultFlat() {
        return (row == 6) || (row == 2) ||
            ((row == 1) && (column == 0)) || ((row == 5) && (column == 0));
    }

    @Override
    public NoteNotation getNotation() {
        return ModClientConfigs.ACCURATE_ACCIDENTALS.get()
            ? super.getNotation()
            : isDefaultFlat() ? NoteNotation.FLAT : NoteNotation.NONE;
    }



    @Override
    public void renderWidget(GuiGraphics gui, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(gui, pMouseX, pMouseY, pPartialTick);
    }
    
}
