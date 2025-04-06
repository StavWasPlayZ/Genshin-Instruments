package com.cstav.genshinstrument.client.gui.screen.instrument.djemdjemdrum;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButtonRenderer;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings;
import com.cstav.genshinstrument.sound.GISounds;
import com.mojang.blaze3d.platform.InputConstants.Key;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DjemDjemDrumNoteButton extends NoteButton {
    public final int row, column;
    /**
     * The index of the Djem Djem drum.
     * <p>
     * Starts form the top left, ends at the bottom right.
     */
    public final int index;

    public DjemDjemDrumNoteButton(InstrumentScreen instrumentScreen, int row, int column) {
        super(
            GISounds.DJEM_DJEM_DRUM[getIndex(row, column)],
            ModClientConfigs.DJEM_DJEM_DRUM_LABEL_TYPE.get().getLabelSupplier(),
            instrumentScreen
        );

        this.row = row;
        this.column = column;
        index = getIndex(row, column);
    }

    private static int getIndex(int row, int column) {
        return column + row * 4;
    }


    @Override
    protected NoteButtonRenderer initNoteRenderer() {
//        return new NoteButtonRenderer(this, () ->
//            instrumentScreen.getResourceFromRoot("note/label/" + switch (btnType) {
//                case DON -> "don";
//                case KA -> "ka_" + (isRight ? "right" : "left");
//            }+".png", false)
//        );
        //TODO: djem djem renderer
        return new NoteButtonRenderer(this, () -> new ResourceLocation("askduhgfd"));
    }


    public Key getKey() {
        return InstrumentKeyMappings.GRID_INSTRUMENT_MAPPINGS[1 - row][column];
    }


    @Override
    public int getNoteOffset() {
        return index;
    }
}
