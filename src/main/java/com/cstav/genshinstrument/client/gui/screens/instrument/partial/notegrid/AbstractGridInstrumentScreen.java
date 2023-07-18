package com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid;

import java.util.Map;
import java.util.NoSuchElementException;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.AbstractInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.GridInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.keyMaps.KeyMappings;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteGridButtonIdentifier;
import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.AbstractLayout;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractGridInstrumentScreen extends AbstractInstrumentScreen {
    public static final int DEF_ROWS = 7, DEF_COLUMNS = 3,
        CLEF_WIDTH = 26, CLEF_HEIGHT = 52;

    protected AbstractLayout grid;


    public AbstractGridInstrumentScreen(InteractionHand hand) {
        super(hand);
    }

    public int columns() {
        return DEF_COLUMNS;
    }
    public int rows() {
        return DEF_ROWS;
    }


    /**
     * <p>
     * If the given identifier is of type {@link NoteGridButtonIdentifier},
     * uses the optimal method to obtain the described {@link NoteButton}.
     * </p>
     * Otherwise, uses {@link AbstractInstrumentScreen#getNoteButton the regular linear method}.
     * @return The {@link NoteButton} as described by the given identifier
     */
    @Override
    public NoteButton getNoteButton(final NoteButtonIdentifier noteIdentifier) throws IndexOutOfBoundsException, NoSuchElementException {
        if (!(noteIdentifier instanceof NoteGridButtonIdentifier))
            return super.getNoteButton(noteIdentifier);

        return getNoteButton((NoteGridButtonIdentifier)noteIdentifier);
    }
    /**
     * Gets a {@link NoteButton} based on the location of the note as described by the given identifier.
     */
    public NoteButton getNoteButton(final NoteGridButtonIdentifier noteIdentifier) throws IndexOutOfBoundsException {
        return getNoteButton(noteIdentifier.row, noteIdentifier.column);
    }

    public NoteButton getNoteButton(final int row, final int column) throws IndexOutOfBoundsException {
        return noteGrid.getNoteButton(row, column);
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
        grid = noteGrid.initNoteGridLayout(.9f, width, height);
        grid.visitWidgets(this::addRenderableWidget);
        
        initOptionsButton(grid.getY() - 15);
        super.init();
    }


    @Override
    public void render(GuiGraphics gui, int pMouseX, int pMouseY, float pPartialTick) {
        if (ModClientConfigs.RENDER_BACKGROUND.get())
            renderInstrumentBackground(gui);
            
        super.render(gui, pMouseX, pMouseY, pPartialTick);
    }


    // Background rendering

    /**
     * Renders the background of this grid instrument.
     * This render method will only work for a 3-column instrument. Overwrite it
     * to customize the background.
     */
    protected void renderInstrumentBackground(final GuiGraphics gui) {
        if (columns() != 3)
            return;

        final int clefX = grid.getX() - NoteButton.getSize() + 8;

        for (int i = 0; i < columns(); i++) {
            renderClef(gui, i, clefX);
            renderStaff(gui, i);
        }
    }

    protected void renderClef(final GuiGraphics gui, final int index, final int x) {
        gui.blit(getResourceFromGlob("background/clefs.png"),
            x, grid.getY() + (NoteButton.getSize() + 16) * index,
            index * CLEF_WIDTH, 0,
            CLEF_WIDTH, CLEF_HEIGHT,
            CLEF_WIDTH*3, CLEF_HEIGHT
        );
    }

    protected void renderStaff(final GuiGraphics gui, final int index) {
        gui.blit(getResourceFromGlob("background/staff.png"),
            grid.getX() + 2, grid.getY() + 8 + ((NoteButton.getSize() + NoteGrid.PADDING_VERT + 6) * index),
            0, 0,
            grid.getWidth() - 5, NoteButton.getSize(),
            grid.getWidth() - 5, NoteButton.getSize()
        );
    }

}
