package com.cstav.genshinstrument.client.gui.screens.instrument.partial;

import java.util.Iterator;
import java.util.function.Supplier;

import com.cstav.genshinstrument.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.label.NoteLabelSupplier;
import com.cstav.genshinstrument.sounds.NoteSound;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.FrameWidget;
import net.minecraft.client.gui.components.GridWidget;
import net.minecraft.client.gui.components.GridWidget.RowHelper;
import net.minecraft.resources.ResourceLocation;
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

    public NoteGrid(int rows, int columns, NoteSound[] sounds,
      ResourceLocation noteResourcesLocation, Supplier<Integer> colorThemeSupplier, Supplier<Integer> pressedThemeSupplier) {
        this.rows = rows;
        this.columns = columns;

        notes = new NoteButton[columns][rows];
        for (int i = 0; i < columns; i++) {
            final NoteButton[] buttonRow = new NoteButton[rows];

            for (int j = 0; j < rows; j++)
                buttonRow[j] = createNote(j, i, sounds, noteResourcesLocation, colorThemeSupplier, pressedThemeSupplier);

            notes[i] = buttonRow;
        }
    }
    
    protected NoteButton createNote(int row, int column, NoteSound[] sounds,
      ResourceLocation noteResourceLocation, Supplier<Integer> colorThemeSupplier, Supplier<Integer> pressedThemeSupplier) {
        return new NoteButton(row, column,
            getSoundAt(sounds, row, column), getLabelSupplier(),
            noteResourceLocation, colorThemeSupplier, pressedThemeSupplier
        );
    }
    /**
     * Evaulates the sound at the given indexes, and returns it
     * @param sounds The sound array of this instrument
     * @param row The row of the note
     * @param column The column of the note
     */
    protected static NoteSound getSoundAt(final NoteSound[] sounds, final int row, final int column) {
        return sounds[row + column * AbstractInstrumentScreen.ROWS];
    }
    /**
     * @return The perferred label supplier specified in this mod's configs
     */
    protected static NoteLabelSupplier getLabelSupplier() {
        return ModClientConfigs.LABEL_TYPE.get().getLabelSupplier();
    }

    public void setSoundArr(final NoteSound[] soundArr) {
        for (int i = 0; i < columns; i++)
            for (int j = 0; j < rows; j++)
                notes[i][j].setSound(getSoundAt(soundArr, j, i));
    }

    /**
     * Constructs a new grid of notes as described in this object.
     * @param vertAlignment A percentage determining the vertical offset of the grid
     * @param screenWidth The width of the screen
     * @param screenHeight The height of the screen
     * @return A new {@link NoteButton} grid
     */
    protected AbstractWidget genNoteGridWidget(final float vertAlignment, final int screenWidth, final int screenHeight) {
        final GridWidget grid = new GridWidget();
        grid.defaultCellSetting().padding(PADDING_HORZ, PADDING_VERT);

        final RowHelper rowHelper = grid.createRowHelper(rows);
        forEach((note) -> rowHelper.addChild(note));

        grid.pack();

        FrameWidget.alignInRectangle(grid, 0, 0, screenWidth, screenHeight, 0.5f, vertAlignment);
        
        // Update the initial position of the buttons
        // For their animation
        forEach((note) -> note.initPos());

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
