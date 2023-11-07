package com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButtonRenderer;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings;
import com.cstav.genshinstrument.event.MidiEvent;
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
            GRID_LABELS[i] = AbstractInstrumentScreen.getInternalResourceFromGlob(
                "note/label/grid/" + Character.toLowerCase(LabelUtil.ABC[i]) + ".png"
            );
        }
    }


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

    public AbstractGridInstrumentScreen gridInstrument() {
        return (AbstractGridInstrumentScreen) instrumentScreen;
    }


    @Override
    public void play() {
        if (locked)
            return;

        final boolean actAsTranspose = true; //TODO make config
        if (!actAsTranspose) {
            super.play();
            return;
        }


        try {
            instrumentScreen.midiReciever.onMidi(new MidiEvent(
                new ShortMessage(-112, toMidiNote() + getPitch() * 2, 127), 0,
                (note) -> {
                    play(note.getSound(), 0);
                    return false;
                }
            ));
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    public int toMidiNote() {
        final int columns = gridInstrument().columns();

            // Base note
        return 60 - (columns - 1) * 12
            // Climbing from left to right
            + (row * 2) + (((columns - 1) - column) * 12)
            // Gaps
            - (row / 3) + (row / 6);
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


    public Key getKey() {
        return InstrumentKeyMappings.GRID_INSTRUMENT_MAPPINGS[column][row];
    }


    @Override
    public NoteGridButtonIdentifier getIdentifier() {
        return new NoteGridButtonIdentifier(this);
    }


    @Override
    protected NoteButtonRenderer initNoteRenderer() {
        return new NoteButtonRenderer(this, () -> GRID_LABELS[textureRow()]);
    }
    protected int textureRow() {
        return ModClientConfigs.ACCURATE_NOTES.get() ? getABCOffset() : row;
    }


    @Override
    public int getNoteOffset() {
        return row + column * gridInstrument().rows();
    }
}
