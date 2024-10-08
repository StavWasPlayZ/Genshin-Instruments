package com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.grid.NoteGridButton;
import com.cstav.genshinstrument.sound.NoteSound;
import com.mojang.blaze3d.platform.InputConstants.Key;
import net.minecraft.client.gui.layouts.AbstractLayout;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.GridLayout.RowHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;


/**
 * A class holding an abstract {@link NoteButton note} grid for {@link GridInstrumentScreen}.
 * All fields are described in there.
 */
@OnlyIn(Dist.CLIENT)
public class NoteGrid implements Iterable<NoteGridButton> {

    public static int getPaddingHorz() {
        return 9;
    }
    public static int getPaddingVert() {
        return 7;
    }

    
    public final GridInstrumentScreen instrumentScreen;
    protected final NoteGridButton[][] notes;
    private NoteSound[] noteSounds;

    public final int rows, columns;

    public NoteGrid(GridInstrumentScreen instrumentScreen, SSTIPitchProvider pitchProvider) {
        this.instrumentScreen = instrumentScreen;
        
        rows = instrumentScreen.rows();
        columns = instrumentScreen.columns();
        
        noteSounds = instrumentScreen.getInitSounds();


        // Construct the note grid
        notes = new NoteGridButton[columns][rows];
        for (int i = 0; i < columns; i++) {
            final NoteGridButton[] buttonRow = new NoteGridButton[rows];
            // Columns should start from the bottom/lowest pitch, unlike how an array axis' structure is sorted.
            // Hence, we flip the Y index:
            final int column = getFlippedColumn(i);

            for (int j = 0; j < rows; j++)
                if (instrumentScreen.isSSTI()) {
                    buttonRow[j] = instrumentScreen.createNote(j, column,
                        // Provide the flipped column to the provider
                        // because the lowest pitch should be at the bottom and not vice-versa
                        pitchProvider.get(j, i)
                    );
                } else
                    buttonRow[j] = instrumentScreen.createNote(j, column);

            
            notes[i] = buttonRow;
        }
    }
    public NoteGrid(GridInstrumentScreen instrumentScreen) {
        this(instrumentScreen, null);
    }

    /**
     * Constructs a linearly increasing pitch note grid for an SSTI-type instrument.
     * @param beginningNote The note to start the linear pitch increment.
     * @param noteSkip The amount of pitch to skip over every note in the linear pitch increment.
     */
    public NoteGrid(GridInstrumentScreen instrumentScreen, int beginningNote, int noteSkip) {
        this(instrumentScreen, (row, column) -> beginningNote +
                (noteSkip * (row + column * instrumentScreen.rows()))
        );
    }
    /**
     * Constructs a linearly increasing pitch note grid for an SSTI-type instrument. The increment is set to 1.
     * @param beginningNote The note to start the linear pitch increment.
     */
    public NoteGrid(GridInstrumentScreen instrumentScreen, int beginningNote) {
        this(instrumentScreen, beginningNote, 1);
    }

    public NoteSound[] getNoteSounds() {
        return noteSounds;
    }
    public void setNoteSounds(final NoteSound[] noteSounds) {
        this.noteSounds = noteSounds;
        forEach(NoteGridButton::updateSoundArr);
    }


    public HashMap<Key, NoteButton> genKeyboardMap(final Key[][] keyMap) {
        final HashMap<Key, NoteButton> result = new HashMap<>(rows * columns);

        for (int i = 0; i < columns; i++)
            for (int j = 0; j < rows; j++)
                result.put(keyMap[i][j], notes[getFlippedColumn(i)][j]);
                
        return result;
    }
    

    /**
     * Constructs a new grid of notes as described in this object.
     * @param vertAlignment A percentage determining the vertical offset of the grid
     * @param screenWidth The width of the screen
     * @param screenHeight The height of the screen
     * @return A new {@link NoteButton} grid
     */
    public AbstractLayout initNoteGridLayout(final float vertAlignment, final int screenWidth, final int screenHeight) {
        final GridLayout grid = new GridLayout();
        grid.defaultCellSetting().padding(getPaddingHorz(), getPaddingVert());

        final RowHelper rowHelper = grid.createRowHelper(rows);
        forEach(rowHelper::addChild);

        
        grid.arrangeElements();
        FrameLayout.alignInRectangle(grid, 0, 0, screenWidth, screenHeight, 0.5f, vertAlignment);
        grid.arrangeElements();
        
        // Initialize all the notes
        forEach(NoteButton::init);

        return grid;
    }


    public NoteButton getNoteButton(final int row, final int column) throws IndexOutOfBoundsException {
        return notes[column][row];
    }

    /**
     * Maps an array column to a note grid column by flipping it
     * @return The flipped column of {@code column}
     */
    public int getFlippedColumn(final int column) {
        return getFlippedColumn(column, columns);
    }
    /**
     * Maps an array column to a note grid column by flipping it
     * @return The flipped column of {@code column}
     */
    public static int getFlippedColumn(final int column, final int columns) {
        return columns - 1 - column;
    }


    @FunctionalInterface
    public static interface SSTIPitchProvider {
        int get(final int row, final int column);
    }

    @Override
    public @NotNull Iterator<NoteGridButton> iterator() {
        // This is a basic 2x2 matrix iterator
        return new Iterator<NoteGridButton>() {

            private int i, j;

            @Override
            public boolean hasNext() {
                return i < columns;
            }

            @Override
            public NoteGridButton next() {
                final NoteGridButton btn = notes[getFlippedColumn(i)][j];

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
