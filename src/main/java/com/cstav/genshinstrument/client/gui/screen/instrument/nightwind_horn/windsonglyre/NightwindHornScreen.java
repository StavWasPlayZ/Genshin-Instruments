package com.cstav.genshinstrument.client.gui.screen.instrument.nightwind_horn.windsonglyre;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held.HeldGridNoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.GridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.NoteGridButton;
import com.cstav.genshinstrument.sound.GISounds;
import com.cstav.genshinstrument.sound.HeldNoteSound;
import com.cstav.genshinstrument.sound.HeldNoteSound.Phase;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NightwindHornScreen extends GridInstrumentScreen {
    public static final ResourceLocation INSTRUMENT_ID = new ResourceLocation(GInstrumentMod.MODID, "nightwind_horn");

    @Override
    public ResourceLocation getInstrumentId() {
        return INSTRUMENT_ID;
    }

    @Override
    public int columns() {
        return 2;
    }


    @Override
    public NoteSound[] getInitSounds() {
        return HeldNoteSound.getSounds(GISounds.NIGHTWIND_HORN, Phase.ATTACK);
    }

    @Override
    public NoteGridButton createNote(int row, int column) {
        return new HeldGridNoteButton(row, column, this);
    }

    @Override
    protected void renderInstrumentBackground(final GuiGraphics gui) {
        final int clefX = grid.getX() - getNoteSize() + 8;

        renderClef(gui, 0, clefX, "treble");
        renderClef(gui, 1, clefX, "bass");

        for (int i = 0; i < columns(); i++)
            renderStaff(gui, i);
    }


    private static final InstrumentThemeLoader THEME_LOADER = new InstrumentThemeLoader(INSTRUMENT_ID);
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
    
}
