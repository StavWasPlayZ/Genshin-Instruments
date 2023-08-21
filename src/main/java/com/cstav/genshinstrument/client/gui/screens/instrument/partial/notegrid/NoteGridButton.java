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
            getSoundFromArr(instrumentScreen.getInitSounds(), row, column, instrumentScreen.rows()),
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

    /**
     * Evaulates the sound at the current position, and sets it as this note's sound
     * @param sounds The sound array to set for this instrument
     */
    public void setSoundFromArr(final NoteSound[] sounds) {
        if (!(instrumentScreen instanceof AbstractGridInstrumentScreen gridInstrument))
            return;

        setSound(gridInstrument.isSSTI() ? sounds[0]
            : getSoundFromArr(sounds, row, column, gridInstrument.rows())
        );
    }
    /**
     * Evaulates the sound at the current position, and sets it as this note's sound
     * @param sounds The sound array of the instrument
     */
    public static NoteSound getSoundFromArr(NoteSound[] sounds, int row, int column, int rows) {
        return sounds[row + column * rows];
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
