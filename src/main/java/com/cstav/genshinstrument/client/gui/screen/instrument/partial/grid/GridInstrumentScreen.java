package com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.grid.NoteGridButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.GridInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.InstrumentOptionsScreen;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings;
import com.cstav.genshinstrument.client.midi.InstrumentMidiReceiver;
import com.cstav.genshinstrument.client.util.ClientUtil;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteGridButtonIdentifier;
import com.cstav.genshinstrument.sound.NoteSound;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public abstract class GridInstrumentScreen extends InstrumentScreen {
    public static final String[] NOTE_LAYOUT = {"C", "D", "E", "F", "G", "A", "B"};

    public static final int DEF_ROWS = 7, DEF_COLUMNS = 3,
        CLEF_WIDTH = 26, CLEF_HEIGHT = 52;

    protected AbstractWidget grid;

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

    @Override
    public void setNoteSounds(NoteSound[] sounds) {
        noteGrid.setNoteSounds(sounds);
    }

    /**
     * <p>
     * An SSTI instrument is a Singular Sound-Type Instrument, such that
     * only the <b>first</b> note in {@link GridInstrumentScreen#getInitSounds()} will get used.
     * </p><p>
     * Notes will start with the {@link NoteSound#MIN_PITCH set minimum pitch},
     * and increment their pitch up by 1 for every new instance.
     * </p>
     * This behaviour can be changed by overriding {@link GridInstrumentScreen#initNoteGrid}.
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


    @Override
    protected boolean identifyByPitch() {
        return isSSTI();
    }

    /**
     * <p>
     * If the given identifier is of type {@link NoteGridButtonIdentifier},
     * uses the optimal method to obtain the described {@link NoteButton}.
     * </p>
     * Otherwise, uses {@link InstrumentScreen#getNoteButton the regular linear method}.
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
     * Retrieves the MIDI note that corresponds with
     * note button at position {@code note}.
     * Starts from bottom-left corner (0, 0).
     * @param note The MIDI note to fetch
     * @return The corresponding note button
     */
    public NoteButton getNoteButtonByMIDINote(final int note) {
        return getNoteButton(note % rows(), note / rows());
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
     * @return The preferred label supplier specified in this mod's configs
     */
    public static NoteLabelSupplier getInitLabelSupplier() {
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
    protected InstrumentOptionsScreen initInstrumentOptionsScreen() {
        return new GridInstrumentOptionsScreen(this);
    }

    @Override
    public String[] noteLayout() {
        return NOTE_LAYOUT;
    }
    

    @Override
    protected void init() {
        grid = noteGrid.initNoteGridWidget(.9f, width, height);
        addRenderableWidget(grid);
        
        initOptionsButton(grid.y - 15);
        super.init();
    }


    @Override
    public void renderInstrument(PoseStack stack, int pMouseX, int pMouseY, float pPartialTick) {
        if (ModClientConfigs.RENDER_BACKGROUND.get())
            renderInstrumentBackground(stack);
            
        super.renderInstrument(stack, pMouseX, pMouseY, pPartialTick);
    }


    // Background rendering

    /**
     * Renders the background of this grid instrument.
     */
    protected void renderInstrumentBackground(final PoseStack stack) {
        final int clefX = grid.x - getNoteSize() + 8;

        // Implement your own otherwise, idk
        if (columns() == 3) {
            renderClef(stack, 0, clefX, "treble");
            renderClef(stack, 1, clefX, "alto");
            renderClef(stack, 2, clefX, "bass");
        }

        for (int i = 0; i < columns(); i++)
            renderStaff(stack, i);
    }

    protected void renderClef(PoseStack stack, int index, int x, String clefName) {
        ClientUtil.displaySprite(getInternalResourceFromGlob("background/clef/"+clefName+".png"));

        blit(stack,
            x, grid.y + NoteGrid.getPaddingVert() + getLayerAddition(index) - 5,
            0, 0,

            CLEF_WIDTH, CLEF_HEIGHT,
            CLEF_WIDTH, CLEF_HEIGHT
        );

        RenderSystem.disableBlend();
    }
    protected void renderStaff(final PoseStack stack, final int index) {
        ClientUtil.displaySprite(getInternalResourceFromGlob("background/staff.png"));

        blit(stack,
            grid.x + 2, grid.y + NoteGrid.getPaddingVert() + getLayerAddition(index),
            0, 0,

            grid.getWidth() - 5, getNoteSize(),
            grid.getWidth() - 5, getNoteSize()
        );

        RenderSystem.disableBlend();
    }

    /**
     * Used for background rendering while determining how deep to go down
     */
    protected int getLayerAddition(final int index) {
        return index * (getNoteSize() + NoteGrid.getPaddingVert()*2);
    }


    @Override
    public InstrumentMidiReceiver initMidiReceiver() {
        return ((rows() != 7) || isSSTI()) ? null : new GridInstrumentMidiReceiver(this);
    }

}
