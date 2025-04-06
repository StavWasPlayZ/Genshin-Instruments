package com.cstav.genshinstrument.client.gui.screen.instrument.ukelele;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid.GridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid.NoteGrid;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.grid.NoteGridButton;
import com.cstav.genshinstrument.sound.GISounds;
import com.cstav.genshinstrument.sound.NoteSound;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UkuleleScreen extends GridInstrumentScreen {
    public static final ResourceLocation INSTRUMENT_ID = new ResourceLocation(GInstrumentMod.MODID, "ukulele");

    private static final String[] NOTE_LAYOUT = {
        "C", "Dm", "Em", "F", "G", "Am", "G7",
        "C", "D", "E", "F", "G", "A", "B",
        "C", "D", "E", "F", "G", "A", "B"
    };

    @Override
    public ResourceLocation getInstrumentId() {
        return INSTRUMENT_ID;
    }


    @Override
    public String[] noteLayout() {
        return NOTE_LAYOUT;
    }

    @Override
    public NoteSound[] getInitSounds() {
        return GISounds.UKULELE;
    }


    @Override
    public NoteGridButton createNote(int row, int column) {
        return new UkuleleNoteButton(row, column, this);
    }

    public static final UkuleleThemeLoader THEME_LOADER = new UkuleleThemeLoader(INSTRUMENT_ID);
    @Override
    public UkuleleThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }


    // Render the chord "clef" atop and the rest to go down an octave.
    @Override
    protected void renderClef(GuiGraphics gui, int index, int x, String clefName) {
        switch (index) {
            case 0: renderClefChord(gui, index, x); break;
            case 1: super.renderClef(gui, index, x, "treble"); break;
            case 2: super.renderClef(gui, index, x, "alto"); break;
        }
    }

    private void renderClefChord(GuiGraphics gui, int index, int x) {
        RenderSystem.enableBlend();

        gui.blit(getResourceFromRoot("background/clef/chord.png"),
            x, grid.getY() + NoteGrid.getPaddingVert() + getLayerAddition(index) - 5,
            0, 0,

            CLEF_WIDTH, CLEF_HEIGHT,
            CLEF_WIDTH, CLEF_HEIGHT
        );

        RenderSystem.disableBlend();
    }
}
