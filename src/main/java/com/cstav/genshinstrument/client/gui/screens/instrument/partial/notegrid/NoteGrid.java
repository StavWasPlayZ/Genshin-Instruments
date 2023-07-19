package com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid;

import java.util.HashMap;
import java.util.Iterator;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.client.gui.widget.copied.GridWidget;
import com.cstav.genshinstrument.client.gui.widget.copied.GridWidget.RowHelper;
import com.cstav.genshinstrument.sound.NoteSound;
import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


/**
 * A class holding an abstract {@link NoteButton note} grid for {@link AbstractGridInstrumentScreen}.
 * All fields are described in there.
 */
@OnlyIn(Dist.CLIENT)
public class NoteGrid implements Iterable<NoteButton> {
    public static final int PADDING_HORZ = 9, PADDING_VERT = 7;

    
    public final AbstractGridInstrumentScreen instrumentScreen;
    protected final NoteButton[][] notes;
    private NoteSound[] noteSounds;

    public final int rows, columns;

    public NoteGrid(int rows, int columns, NoteSound[] noteSounds, AbstractGridInstrumentScreen instrumentScreen) {
        this.instrumentScreen = instrumentScreen;
        this.rows = rows;
        this.columns = columns;
        this.noteSounds = noteSounds;

        // Construct the note grid
        notes = new NoteButton[columns][rows];
        for (int i = 0; i < columns; i++) {
            final NoteButton[] buttonRow = new NoteButton[rows];

            for (int j = 0; j < rows; j++)
                buttonRow[j] = createNote(j, i);

            notes[i] = buttonRow;
        }
    }
    
    protected NoteButton createNote(int row, int column) {
        return new NoteGridButton(row, column,
            getSoundAt(noteSounds, row, column), getLabelSupplier()
        , instrumentScreen);
    }
    /**
     * Evaulates the sound at the given indexes, and returns it
     * @param sounds The sound array of this instrument
     * @param row The row of the note
     * @param column The column of the note
     */
    public NoteSound getSoundAt(final NoteSound[] sounds, final int row, final int column) {
        return sounds[row + column * instrumentScreen.rows()];
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
    }


    public HashMap<Key, NoteButton> genKeyboardMap(final Key[][] keyMap) {
        final HashMap<Key, NoteButton> result = new HashMap<>(rows * columns);

        for (int i = 0; i < keyMap.length; i++)
            for (int j = 0; j < keyMap[0].length; j++)
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
    public AbstractWidget initNoteGridWidget(final float vertAlignment, final int screenWidth, final int screenHeight) {
        final GridWidget grid = new GridWidget();
        grid.defaultCellSetting().padding(PADDING_HORZ, PADDING_VERT);

        final RowHelper rowHelper = grid.createRowHelper(rows);
        forEach(rowHelper::addChild);

        grid.pack();
        grid.x = (screenWidth - grid.getWidth()) / 2;
        grid.y = screenHeight - grid.getHeight() - 25;
        grid.pack();
        
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
