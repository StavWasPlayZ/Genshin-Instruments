package com.cstav.genshinstrument.client.gui.screens.instrument.partial;

import com.cstav.genshinstrument.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteGrid;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteGridLabel;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteLabel;
import com.cstav.genshinstrument.client.keyMaps.KeyMappings;

import net.minecraft.client.gui.components.AbstractWidget;

public abstract class AbstractGridInstrumentScreen extends AbstractInstrumentScreen {
    public static final int DEF_ROWS = 7, DEF_COLUMNS = 3;

    public int columns() {
        return DEF_COLUMNS;
    }
    public int rows() {
        return DEF_ROWS;
    }

    
    // Abstract implementations
    /**
     * Initializes a new Note Grid to be paired with this instrument
     * @return The new Note Grid
     */
    public NoteGrid initNoteGrid() {
        return new NoteGrid(
            rows(), columns(), getSounds(), this
        );
    }

    public final NoteGrid noteGrid = initNoteGrid();
    @Override
    public Iterable<NoteButton> noteIterable() {
        return noteGrid;
    }

    @Override
    public NoteGridLabel[] getLabels() {
        return NoteGridLabel.values();
    }
    @Override
    public NoteGridLabel getCurrentLabel() {
        return ModClientConfigs.GRID_LABEL_TYPE.get();
    }
    

    @Override
    protected void init() {
        final AbstractWidget grid = noteGrid.initNoteGridWidget(.9f, width, height);
        addRenderableWidget(grid);
        
        initOptionsButton(grid.getY() - 15);
        super.init();
    }

    
    // Handle pressing on keyboard
    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        for (int i = 0; i < columns(); i++)
            for (int j = 0; j < rows(); j++)
                if (lyreKeyPressed(j, i, pKeyCode)) {
                    noteGrid.getNote(j, i).play(true);
                    return true;
                }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        for (int i = 0; i < columns(); i++)
            for (int j = 0; j < rows(); j++)
                if (lyreKeyPressed(j, i, pKeyCode)) {
                    noteGrid.getNote(j, i).locked = false;
                    return true;
                }

        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    public static boolean lyreKeyPressed(final int row, final int column, final int keyCode) {
        return KeyMappings.GRID_INSTRUMENT_MAPPINGS[column][row].getValue() == keyCode;
    }
    
}
