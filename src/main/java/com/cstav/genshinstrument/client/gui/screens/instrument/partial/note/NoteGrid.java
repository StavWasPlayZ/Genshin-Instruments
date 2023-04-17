package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note;

import java.util.Iterator;

import com.cstav.genshinstrument.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractGridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.sounds.NoteSound;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.FrameWidget;
import net.minecraft.client.gui.components.GridWidget;
import net.minecraft.client.gui.components.GridWidget.RowHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


/**
 * A class holding an abstract {@link NoteButton note} grid for {@link AbstractInstrumentScreen}.
 * All fields are described in there.
 */
@OnlyIn(Dist.CLIENT)
public class NoteGrid implements Iterable<NoteButton> {
    public static final int PADDING_HORZ = 9, PADDING_VERT = 7;

    
    private final NoteButton[][] notes;
    public final int rows, columns;
    private NoteSound[] noteSounds;

    public NoteGrid(final int rows, final int columns, NoteSound[] noteSounds, AbstractInstrumentScreen instrumentScreen) {
        this.rows = rows;
        this.columns = columns;
        this.noteSounds = noteSounds;

        // Construct the note grid
        notes = new NoteButton[columns][rows];
        for (int i = 0; i < columns; i++) {
            final NoteButton[] buttonRow = new NoteButton[rows];

            for (int j = 0; j < rows; j++)
                buttonRow[j] = createNote(j, i, instrumentScreen);

            notes[i] = buttonRow;
        }

        updatePitch();
    }
    /**
     * Updates the grid to use the pitch stored in {@link ModClientConfigs#PITCH the configs}
     */
    public void updatePitch() {
        final float pitch = ModClientConfigs.PITCH.get().floatValue();
        forEach((note) ->
            note.getSound().setPitch(pitch)
        );
    }
    
    protected NoteButton createNote(final int row, final int column, final AbstractInstrumentScreen instrumentScreen) {
        return new NoteGridButton(row, column,
            getSoundAt(noteSounds, row, column), getLabelSupplier(), rows
        , instrumentScreen);
    }
    /**
     * Evaulates the sound at the given indexes, and returns it
     * @param sounds The sound array of this instrument
     * @param row The row of the note
     * @param column The column of the note
     */
    protected static NoteSound getSoundAt(final NoteSound[] sounds, final int row, final int column) {
        return sounds[row + column * AbstractGridInstrumentScreen.DEF_ROWS];
    }
    /**
     * @return The perferred label supplier specified in this mod's configs
     */
    protected static NoteLabelSupplier getLabelSupplier() {
        return ModClientConfigs.GRID_LABEL_TYPE.get().getLabelSupplier();
    }

    public NoteSound[] getNoteSounds() {
        return noteSounds;
    }
    public void setNoteSounds(final NoteSound[] noteSounds) {
        this.noteSounds = noteSounds;

        for (int i = 0; i < columns; i++)
            for (int j = 0; j < rows; j++)
                notes[i][j].setSound(getSoundAt(noteSounds, j, i));

        updatePitch();
    }

    /**
     * Constructs a new grid of notes as described in this object.
     * @param vertAlignment A percentage determining the vertical offset of the grid
     * @param screenWidth The width of the screen
     * @param screenHeight The height of the screen
     * @return A new {@link NoteButton} grid
     */
    public AbstractWidget initNoteGridWidget(final float vertAlignment, final int screenWidth, final int screenHeight) {
        final GridWidget grid = new GridWidget();
        grid.defaultCellSetting().padding(PADDING_HORZ, PADDING_VERT);

        final RowHelper rowHelper = grid.createRowHelper(rows);
        forEach((note) -> rowHelper.addChild(note));

        grid.pack();

        FrameWidget.alignInRectangle(grid, 0, 0, screenWidth, screenHeight, 0.5f, vertAlignment);
        
        // Initialize all the notes
        forEach((note) -> note.init());

        return grid;
    }


    public NoteButton getNote(final int row, final int column) {
        return notes[column][row];
    }



    @Override
    public Iterator<NoteButton> iterator() {
        return new Iterator<NoteButton>() {

            private int i, j;

            @Override
            public boolean hasNext() {
                return i < columns;
            }

            @Override
            public NoteButton next() {
                final NoteButton btn = notes[i][j];

                if (j >= (rows - 1)) {
                    j = 0;
                    i++;
                } else
                    j++;

                return btn;
            }

        };
    }
}
