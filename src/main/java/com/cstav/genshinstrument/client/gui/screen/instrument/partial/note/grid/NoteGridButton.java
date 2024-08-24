package com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.grid;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid.GridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid.NoteGrid;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButtonRenderer;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteGridButtonIdentifier;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.util.LabelUtil;
import com.mojang.blaze3d.platform.InputConstants.Key;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoteGridButton extends NoteButton {
    private static final ResourceLocation[] GRID_LABELS = new ResourceLocation[LabelUtil.ABC.length];
    static {
        for (int i = 0; i < LabelUtil.ABC.length; i++) {
            GRID_LABELS[i] = InstrumentScreen.getInternalResourceFromGlob(
                "note/label/grid/" + Character.toLowerCase(LabelUtil.ABC[i]) + ".png"
            );
        }
    }


    public final int row, column;

    public NoteGridButton(int row, int column, GridInstrumentScreen instrumentScreen) {
        super(
            getSoundFromArr(instrumentScreen, instrumentScreen.getInitSounds(), row, column),
            GridInstrumentScreen.getInitLabelSupplier(), instrumentScreen
        );
        
        this.row = row;
        this.column = column;
    }
    /**
     * Creates a button for an SSTI-type instrument
     */
    public NoteGridButton(int row, int column, GridInstrumentScreen instrumentScreen,
            int pitch) {
        super(instrumentScreen.getInitSounds()[0], instrumentScreen.getInitLabelSupplier(), instrumentScreen, pitch);

        this.row = row;
        this.column = column;
    }

    public GridInstrumentScreen gridInstrument() {
        return (GridInstrumentScreen) instrumentScreen;
    }


    public void updateSoundArr() {
        final NoteGrid grid = gridInstrument().noteGrid;
        final NoteSound[] sounds = grid.getNoteSounds();

        setSound(gridInstrument().isSSTI() ? sounds[0]
            : sounds[posToIndex()]
        );
    }

    /**
     * @return The position of this button ({@link NoteGridButton#row}, {@link NoteGridButton#column})
     * as an array index
     */
    public int posToIndex() {
        return row + NoteGrid.getFlippedColumn(column, gridInstrument().columns()) * gridInstrument().rows();
    }
    /**
     * Evaluates the sound at the current position.
     * Meant for static initialization of sounds.
     * @param sounds The sound array of the instrument
     * @see NoteGridButton#posToIndex
     */
    protected static NoteSound getSoundFromArr(GridInstrumentScreen gridInstrument, NoteSound[] sounds, int row, int column) {
        return sounds[row + NoteGrid.getFlippedColumn(column, gridInstrument.columns()) * gridInstrument.rows()];
    }


    public Key getKey() {
        return InstrumentKeyMappings.GRID_INSTRUMENT_MAPPINGS[column][row];
    }


    @Override
    public NoteGridButtonIdentifier getIdentifier() {
        return new NoteGridButtonIdentifier(this);
    }


    @Override
    protected NoteButtonRenderer initNoteRenderer() {
        return new NoteButtonRenderer(this, this::getTextureAtRow);
    }
    protected int textureRow() {
        return ModClientConfigs.ACCURATE_NOTES.get() ? getABCOffset() : (row % GRID_LABELS.length);
    }

    protected ResourceLocation getTextureAtRow(final int row) {
        return GRID_LABELS[row];
    }
    protected ResourceLocation getTextureAtRow() {
        return getTextureAtRow(textureRow());
    }


    @Override
    public int getNoteOffset() {
        return row + column * gridInstrument().rows();
    }
}
