package com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid;

import java.util.HashMap;
import java.util.Iterator;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.sound.NoteSound;
import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.client.gui.layouts.AbstractLayout;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.GridLayout.RowHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


/**
 * A class holding an abstract {@link NoteButton note} grid for {@link AbstractGridInstrumentScreen}.
 * All fields are described in there.
 */
@OnlyIn(Dist.CLIENT)
public class NoteGrid implements Iterable<NoteButton> {

    public static int getPaddingHorz() {
        return 9;
    }
    public static int getPaddingVert() {
        return 7;
    }

    
    public final AbstractGridInstrumentScreen instrumentScreen;
    protected final NoteGridButton[][] notes;
    private NoteSound[] noteSounds;

    public final int rows, columns;

    /**
     * @param begginingNote The note to start the linear pitch increment. Only gets used if this is an SSTI instrument.
     * @param noteSkip The amount of pitch to skip for each note button. Only gets used if this is an SSTI instrument.
     */
    public NoteGrid(NoteSound[] noteSounds, AbstractGridInstrumentScreen instrumentScreen,
            int begginingNote, int noteSkip) {

        this.instrumentScreen = instrumentScreen;
        rows = instrumentScreen.rows();
        columns = instrumentScreen.columns();

        this.noteSounds = noteSounds;


        // Construct the note grid
        notes = new NoteGridButton[columns][rows];
        for (int i = columns - 1; i >= 0; i--) {
            final NoteGridButton[] buttonRow = new NoteGridButton[rows];

            for (int j = 0; j < rows; j++)
                if (instrumentScreen.isSSTI()) {
                    buttonRow[j] = instrumentScreen.createNote(j, i, begginingNote);
                    begginingNote += noteSkip;
                } else
                    buttonRow[j] = instrumentScreen.createNote(j, i);

            notes[i] = buttonRow;
        }
    }
    public NoteGrid(NoteSound[] noteSounds, AbstractGridInstrumentScreen instrumentScreen) {
        this(noteSounds, instrumentScreen, 0, 0);
    }
    /**
     * @param begginingNote The note to start the linear pitch increment. Only gets used if this is an SSTI instrument.
     */
    public NoteGrid(NoteSound[] noteSounds, AbstractGridInstrumentScreen instrumentScreen, int begginingNote) {
        this(noteSounds, instrumentScreen, begginingNote, 1);
    }

    public NoteSound[] getNoteSounds() {
        return noteSounds;
    }
    public void setNoteSounds(final NoteSound[] noteSounds) {
        this.noteSounds = noteSounds;

        for (int i = 0; i < columns; i++)
            for (int j = 0; j < rows; j++)
                notes[i][j].setSoundFromArr(noteSounds);
    }


    public HashMap<Key, NoteButton> genKeyboardMap(final Key[][] keyMap) {
        final HashMap<Key, NoteButton> result = new HashMap<>(rows * columns);

        for (int i = 0; i < columns; i++)
            for (int j = 0; j < rows; j++)
                result.put(keyMap[i][j], notes[i][j]);
                
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



    @Override
    public Iterator<NoteButton> iterator() {
        // This is a basic 2x2 matrix iterator
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
