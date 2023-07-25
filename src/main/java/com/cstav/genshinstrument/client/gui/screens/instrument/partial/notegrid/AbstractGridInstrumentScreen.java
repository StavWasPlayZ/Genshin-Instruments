package com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.AbstractInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.GridInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.keyMaps.KeyMappings;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteGridButtonIdentifier;
import com.cstav.genshinstrument.sound.NoteSound;
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
     * <p>Gets the sound array used by this instrument.
     * Its length must be equal to this Note Grid's {@code row*column}.</p>
     * Each sound is used on press by the their index on the grid.
     * @return The array of sounds used by this instruments.
     */
    public abstract NoteSound[] getSounds();

    /**
     * <p>
     * An SSTI instrument is a Singular Sound-Type Instrument, such that
     * only the <b>first</b> note in {@link AbstractGridInstrumentScreen#getSounds} will get used.
     * </p><p>
     * Notes will start with the {@link NoteSound#MIN_PITCH set minimum pitch},
     * and increment their pitch up by 1 for every new instance.
     * </p>
     * This behaviour can be changed by overriding {@link AbstractGridInstrumentScreen#initNoteGrid}.
     */
    public boolean isSSTI() {
        return false;
    }
    
    @Override
    public void setPitch(int pitch) {
        if (!isSSTI())
            super.setPitch(pitch);
    }

    @Override
    protected void initPitch(Consumer<Integer> pitchConsumer) {
        if (!isSSTI())
            super.initPitch(pitchConsumer);
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
        return isSSTI()
            ? new NoteGrid(getSounds(), this, NoteSound.MIN_PITCH)
            : new NoteGrid(getSounds(), this);
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

    /**
     * Defines the location of the note symbols.
     * The number of symbols should match with the number of {@link AbstractGridInstrumentScreen#rows rows} in this instrument.
     */
    @Override
    public ResourceLocation getNoteSymbolsLocation() {
        return new ResourceLocation(GInstrumentMod.MODID, getGlobalRootPath() + "grid_notes.png");
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
     */
    protected void renderInstrumentBackground(final GuiGraphics gui) {
        final int clefX = grid.getX() - getNoteSize() + 8;

        for (int i = 0; i < columns(); i++) {
            renderClef(gui, i, clefX);
            renderStaff(gui, i);
        }
    }

    protected void renderClef(final GuiGraphics gui, final int index, final int x) {
        gui.blit(getInternalResourceFromGlob("background/clefs.png"),
            x, grid.getY() + NoteGrid.getPaddingVert() + getLayerAddition(index) - 5,
            index * CLEF_WIDTH, 0,
            CLEF_WIDTH, CLEF_HEIGHT,
            CLEF_WIDTH*3, CLEF_HEIGHT
        );
    }
    protected void renderStaff(final GuiGraphics gui, final int index) {
        gui.blit(getInternalResourceFromGlob("background/staff.png"),
            grid.getX() + 2, grid.getY() + NoteGrid.getPaddingVert() + getLayerAddition(index),
            0, 0,
            grid.getWidth() - 5, getNoteSize(),
            grid.getWidth() - 5, getNoteSize()
        );
    }

    /**
     * Used for background rendering while determining how deep to go down
     */
    protected int getLayerAddition(final int index) {
        return index * (getNoteSize() + NoteGrid.getPaddingVert()*2);
    }

}
