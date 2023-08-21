package com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButtonRenderer;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteGridButtonIdentifier;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.util.LabelUtil;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoteGridButton extends NoteButton {

    public final int row, column;

    public NoteGridButton(int row, int column, AbstractGridInstrumentScreen instrumentScreen) {
        super(
            getSoundFromArr(instrumentScreen, instrumentScreen.getInitSounds(), row, column),
            instrumentScreen.getInitLabelSupplier(), instrumentScreen
        );
        
        this.row = row;
        this.column = column;
    }
    /**
     * Creates a button for an SSTI-type instrument
     */
    public NoteGridButton(int row, int column, AbstractGridInstrumentScreen instrumentScreen,
            int pitch) {
        super(instrumentScreen.getInitSounds()[0], instrumentScreen.getInitLabelSupplier(), instrumentScreen, pitch);

        this.row = row;
        this.column = column;
    }

    public void updateSoundArr() {
        if (!(instrumentScreen instanceof AbstractGridInstrumentScreen gridInstrument))
            return;

        final NoteGrid grid = gridInstrument.noteGrid;
        final NoteSound[] sounds = grid.getNoteSounds();

        setSound(gridInstrument.isSSTI() ? sounds[0]
            : getSoundFromArr(gridInstrument, sounds, row, column)
        );
    }
    /**
     * Evaulates the sound at the current position, and sets it as this note's sound
     * @param sounds The sound array of the instrument
     */
    public static NoteSound getSoundFromArr(AbstractGridInstrumentScreen gridInstrument, NoteSound[] sounds, int row, int column) {
        return sounds[row + NoteGrid.getFlippedColumn(column, gridInstrument.columns()) * gridInstrument.rows()];
    }


    @Override
    public NoteGridButtonIdentifier getIdentifier() {
        return new NoteGridButtonIdentifier(this);
    }


    @Override
    protected NoteButtonRenderer initNoteRenderer() {
        return new NoteButtonRenderer(this, row, LabelUtil.ABC.length);
    }

    @Override
    public void updateNoteLabel() {
        super.updateNoteLabel();
        noteRenderer.noteTextureRow = ModClientConfigs.ACCURATE_NOTES.get()
            ? getABCOffset() : row;
    }
    public int getABCOffset() {
        return LabelUtil.getABCOffset(this);
    }


    @Override
    public int getNoteOffset() {
        final AbstractGridInstrumentScreen gridInstrument = (AbstractGridInstrumentScreen)instrumentScreen;
        return row + gridInstrument.noteGrid.getFlippedColumn(column) * gridInstrument.rows();
    }
}
