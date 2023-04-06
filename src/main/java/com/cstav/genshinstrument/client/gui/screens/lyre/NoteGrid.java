package com.cstav.genshinstrument.client.gui.screens.lyre;

import java.util.Iterator;
import java.util.function.Supplier;

import com.cstav.genshinstrument.client.gui.screens.lyre.label.NoteLabel;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.FrameWidget;
import net.minecraft.client.gui.components.GridWidget;
import net.minecraft.client.gui.components.GridWidget.RowHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoteGrid implements Iterable<NoteButton> {
    public static final int PADDING_HORZ = 9, PADDING_VERT = 7;

    
    private final NoteButton[][] notes;
    public final int rows, columns;

    public NoteGrid(final int rows, final int columns,
      final Supplier<Integer> colorThemeSupplier, final Supplier<Integer> pressedThemeSupplier) {
        this.rows = rows;
        this.columns = columns;

        notes = new NoteButton[columns][rows];
        for (int i = 0; i < columns; i++) {
            final NoteButton[] buttonRow = new NoteButton[rows];

            for (int j = 0; j < rows; j++)
                buttonRow[j] = createNote(j, i, colorThemeSupplier, pressedThemeSupplier);

            notes[i] = buttonRow;
        }
    }
    
    NoteButton createNote(final int row, final int column,
      final Supplier<Integer> colorThemeSupplier, final Supplier<Integer> pressedThemeSupplier) {
        return new NoteButton(row, column,
            //TODO: Add option in lyre settings screen
            Enum.valueOf(NoteLabel.class, "KEYBOARD_LAYOUT").getLabelSupplier(),
            colorThemeSupplier, pressedThemeSupplier
        );
    }

    AbstractWidget genNoteGridWidget(final float vertAlignment, final int screenWidth, final int screenHeight) {
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
