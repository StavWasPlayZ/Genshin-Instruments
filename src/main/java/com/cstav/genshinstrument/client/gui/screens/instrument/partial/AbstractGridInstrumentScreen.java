package com.cstav.genshinstrument.client.gui.screens.instrument.partial;

import java.util.Map;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteGrid;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.AbstractInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.GridInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.keyMaps.KeyMappings;
import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.client.gui.layouts.AbstractLayout;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractGridInstrumentScreen extends AbstractInstrumentScreen {
    public static final int DEF_ROWS = 7, DEF_COLUMNS = 3;
    public AbstractGridInstrumentScreen(InteractionHand hand) {
        super(hand);
    }

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
    
    private final Map<Key, NoteButton> noteMap = noteGrid.genKeyboardMap(KeyMappings.GRID_INSTRUMENT_MAPPINGS);
    @Override
    public Map<Key, NoteButton> noteMap() {
        return noteMap;
    }

    @Override
    protected AbstractInstrumentOptionsScreen initInstrumentOptionsScreen() {
        return new GridInstrumentOptionsScreen(this);
    }

    @Override
    public ResourceLocation getNotesLocation() {
        return new ResourceLocation(Main.MODID, getGlobalRootPath() + "grid_notes.png");
    }
    

    @Override
    protected void init() {
        final AbstractLayout grid = noteGrid.initNoteGridLayout(.9f, width, height);
        grid.visitWidgets(this::addRenderableWidget);
        
        initOptionsButton(grid.getY() - 15);
        super.init();
    }
    
}
