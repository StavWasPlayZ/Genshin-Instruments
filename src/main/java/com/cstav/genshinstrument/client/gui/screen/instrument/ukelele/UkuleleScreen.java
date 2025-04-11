package com.cstav.genshinstrument.client.gui.screen.instrument.ukelele;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid.GridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid.NoteGrid;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.grid.NoteGridButton;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.InstrumentOptionsScreen;
import com.cstav.genshinstrument.client.midi.InstrumentMidiReceiver;
import com.cstav.genshinstrument.client.util.ClientUtil;
import com.cstav.genshinstrument.sound.GISounds;
import com.cstav.genshinstrument.sound.NoteSound;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
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


    public Ukulele3rdOctaveType octaveType = ModClientConfigs.UKULELE_3RD_OCTAVE_TYPE.get();

    /**
     * @return Whether the 3rd octave is not used as the chord octave,
     * but a regular 3rd octave.
     */
    public boolean isTopRegular() {
        return octaveType == Ukulele3rdOctaveType.TREBLE;
    }


    @Override
    public NoteGridButton createNote(int row, int column) {
        return new UkuleleNoteButton(row, column, this);
    }

    @Override
    protected InstrumentOptionsScreen initInstrumentOptionsScreen() {
        return new UkuleleOptionsScreen(this);
    }

    public static final UkuleleThemeLoader THEME_LOADER = new UkuleleThemeLoader(INSTRUMENT_ID);
    @Override
    public UkuleleThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }


    @Override
    public InstrumentMidiReceiver initMidiReceiver() {
        return new UkuleleMidiReceiver(this);
    }

    // Render the chord "clef" atop and the rest to go down an octave.
    @Override
    protected void renderClef(PoseStack stack, int index, int x, String clefName) {
        if (isTopRegular()) {
            super.renderClef(stack, index, x, clefName);
            return;
        }

        switch (index) {
            case 0: renderClefChord(stack, index, x); break;
            case 1: super.renderClef(stack, index, x, "treble"); break;
            case 2: super.renderClef(stack, index, x, "alto"); break;
        }
    }

    private void renderClefChord(PoseStack stack, int index, int x) {
        RenderSystem.enableBlend();

        ClientUtil.displaySprite(getResourceFromRoot("background/clef/chord.png"));

        GuiComponent.blit(stack,
            x, grid.y + NoteGrid.getPaddingVert() + getLayerAddition(index) - 5,
            0, 0,

            CLEF_WIDTH, CLEF_HEIGHT,
            CLEF_WIDTH, CLEF_HEIGHT
        );

        RenderSystem.disableBlend();
    }
}
