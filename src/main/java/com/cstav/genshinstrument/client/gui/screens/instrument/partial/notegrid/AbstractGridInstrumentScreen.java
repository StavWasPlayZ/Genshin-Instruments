package com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.BaseInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.GridInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings;
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
    public static final String[] NOTE_LAYOUT = {"C", "D", "E", "F", "G", "A", "B"};

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
    public abstract NoteSound[] getInitSounds();

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
        return getNoteButton(noteIdentifier.row, noteGrid.getFlippedColumn(noteIdentifier.column));
    }

    public NoteButton getNoteButton(final int row, final int column) throws IndexOutOfBoundsException {
        return noteGrid.getNoteButton(row, column);
    }


    /**
     * Creates a note for a singular sound type (SSTI) instrument
     */
    public NoteGridButton createNote(int row, int column, int pitch) {
        return new NoteGridButton(row, column, this, pitch);
    }
    public NoteGridButton createNote(int row, int column) {
        return new NoteGridButton(row, column, this);
    }

    /**
     * @return The perferred label supplier specified in this mod's configs
     */
    protected NoteLabelSupplier getInitLabelSupplier() {
        return ModClientConfigs.GRID_LABEL_TYPE.get().getLabelSupplier();
    }

    
    // Abstract implementations
    /**
     * Initializes a new Note Grid to be paired with this instrument
     * @return The new Note Grid
     */
    public NoteGrid initNoteGrid() {
        return isSSTI()
            ? new NoteGrid(this, NoteSound.MIN_PITCH)
            : new NoteGrid(this);
    }


    public final NoteGrid noteGrid = initNoteGrid();
    
    private final Map<Key, NoteButton> noteMap = noteGrid.genKeyboardMap(InstrumentKeyMappings.GRID_INSTRUMENT_MAPPINGS);
    @Override
    public Map<Key, NoteButton> noteMap() {
        return noteMap;
    }

    @Override
    protected BaseInstrumentOptionsScreen initInstrumentOptionsScreen() {
        return new GridInstrumentOptionsScreen(this);
    }

    /**
     * Defines the location of the note symbols.
     * The number of symbols should match with the number of {@link AbstractGridInstrumentScreen#rows rows} in this instrument.
     */
    @Override
    public ResourceLocation getNoteSymbolsLocation() {
        return getInternalResourceFromGlob("grid_notes.png");
    }

    @Override
    public String[] noteLayout() {
        return NOTE_LAYOUT;
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



    @Override
    public boolean isMidiInstrument() {
        // idk how to handle these, nor do i really care tbh
        return (rows() == 7) && !isSSTI();
    }
    
    @Override
    protected NoteButton handleMidiPress(int note, int pitch) {

        final int layoutNote = note % 12;
        final boolean higherThan3 = layoutNote > pitch + 4;

        // Handle transposition
        final boolean shouldSharpen = shouldSharpen(layoutNote, higherThan3, pitch);
        final boolean shouldFlatten = shouldFlatten(shouldSharpen);

        transposeMidi(shouldSharpen, shouldFlatten);

        
        final int playedNote = note + (shouldFlatten ? 1 : shouldSharpen ? -1 : 0);

        final int currNote = ((playedNote + (higherThan3 ? 1 : 0)) / 2)
            // 12th note should go to the next column
            + playedNote / (12 + pitch);

        return getNoteButton(currNote % rows(), currNote / rows());
    }

}
