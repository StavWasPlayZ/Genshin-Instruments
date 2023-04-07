package com.cstav.genshinstrument.client.gui.screens.instrument.partial;

import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import com.cstav.genshinstrument.client.gui.screens.options.InstrumentOptionsScreen;
import com.cstav.genshinstrument.client.keyMaps.KeyMappings;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.packets.lyre.CloseInstrumentPacket;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractInstrumentScreen extends Screen {
    public static final String NOTE_DIR = "note";
    public static final int ROWS = 7, COLUMNS = 3;

    
    // Abstract implementations
    /**
     * Initializes a new Note Grid to be paired with this instrument
     * @return The new Note Grid
     */
    public NoteGrid initNoteGrid() {
        return new NoteGrid(
            ROWS, COLUMNS, getSounds(),
            getResourceFromRoot(NOTE_DIR),
            () -> getThemeLoader().getNoteTheme().getNumeric(),
            () -> getThemeLoader().getPressedNoteTheme().getNumeric()
        );
    }
    /**
     * <p>Gets the root directory of this instrument's resources.</p>
     * Such directory is made of:
     * <ul>
        * <li><b>instrument_style.json - as described in {@link InstrumentThemeLoader}</b></li>
        * <li><b>note</b> - contains note_bg.png and note.png</li>
     * </ul>
     * @return The resource location of this instrument
     */
    protected abstract ResourceLocation getInstrumentResourcesLocation();
    protected InstrumentOptionsScreen initInstrumentOptionsScreen() {
        return new InstrumentOptionsScreen(title, true, this);
    }
    // Any subclass must make their own LyreThemeLoader
    protected abstract InstrumentThemeLoader getThemeLoader();

    /**
     * <p>Gets the sound array used by this instrument.
     * Its length must be equal to this Note Grid's {@code row*column}.</p>
     * Each sound is used on press by the their index on the grid.
     * @return The array of sounds used by this instruments.
     */
    public abstract SoundEvent[] getSounds();
    protected SoundEvent[] getSoundsFromObjectArr(final RegistryObject<SoundEvent>[] sounds) {
        return Stream.of(sounds).map(RegistryObject::get).toArray(SoundEvent[]::new);
    }
    
    /**
     * @param path The desired path to obtain from the root directory
     * @return The resource contained in this instrument's root directory
     * @see {@link AbstractInstrumentScreen#getInstrumentResourcesLocation()}
     */
    protected ResourceLocation getResourceFromRoot(final String path) {
        return new ResourceLocation(
            getInstrumentResourcesLocation().getNamespace(),
            getInstrumentResourcesLocation().getPath() + "/" + path
        );
    }


    public AbstractInstrumentScreen() {
        super(Component.empty());
    }


    public final NoteGrid noteGrid = initNoteGrid();
    protected final InstrumentOptionsScreen optionsScreen = initInstrumentOptionsScreen();

    @Override
    protected void init() {
        final AbstractWidget grid = noteGrid.genNoteGridWidget(.9f, width, height);

        addRenderableWidget(grid);
        addRenderableWidget(initCustomizeButton(grid.getY() - 15));

        optionsScreen.init(minecraft, width, height);
    }
    /**
     * Initializes a new button responsible for popping up the customize menu for this instrument.
     * Called during {@link Screen#init}.
     * @param vertOffset The vertical offset at which this button will be rendered.
     * @return A new Customize button
     */
    protected AbstractWidget initCustomizeButton(final int vertOffset) {
        final Button button = Button.builder(
            Component.translatable("button.genshinstrument.instrumentOptions").append("..."), (btn) -> onOptionsOpen()
        )
        .width(130)
        .build();

        button.setPosition((width - button.getWidth())/2, vertOffset - button.getHeight()/2);
        return button;
    }


    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        optionsScreen.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }


    protected void onOptionsOpen() {
        setSettingsOpen(true);
    }
    protected void onOptionsClose() {
        setSettingsOpen(false);
    }
    private void setSettingsOpen(final boolean open) {
        setChildrenActive(this, !open);
        optionsScreen.active = open;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        optionsScreen.mouseClicked(pMouseX, pMouseY, pButton);
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        optionsScreen.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }
    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        optionsScreen.mouseReleased(pMouseX, pMouseY, pButton);
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }


    /**
     * Recursively sets all buttons' {@link Button#active active} field in {@code container} to be the given state.
     * @param container The container to apply this method to
     * @param active The state of the button's active field
     */
    protected static void setChildrenActive(final ContainerEventHandler container, final boolean active) {
        for (final GuiEventListener child : container.children())
            if (child instanceof Button)
                ((Button)child).active = active;
            else if (child instanceof ContainerEventHandler)
                setChildrenActive((ContainerEventHandler)child, active);
    }


    @Override
    public void onClose() {
        if (optionsScreen.active)
            return;

        ModPacketHandler.sendToServer(new CloseInstrumentPacket());
        super.onClose();
    }
    

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        
        for (int i = 0; i < COLUMNS; i++)
            for (int j = 0; j < ROWS; j++)
                if (lyreKeyPressed(j, i, pKeyCode)) {
                    noteGrid.getNote(j, i).play(true);
                    return true;
                }
            

        final boolean result = super.keyPressed(pKeyCode, pScanCode, pModifiers);

        if (optionsScreen.active)
            optionsScreen.keyPressed(pKeyCode, pScanCode, pModifiers);
        if (!optionsScreen.active)
            onOptionsClose();

        return result;
        
    }
    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        for (int i = 0; i < COLUMNS; i++)
            for (int j = 0; j < ROWS; j++)
                if (lyreKeyPressed(j, i, pKeyCode)) {
                    noteGrid.getNote(j, i).locked = false;
                    return true;
                }

        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }
    public static boolean lyreKeyPressed(final int row, final int column, final int keyCode) {
        return KeyMappings.LYRE_MAPPINGS[column][row].getValue() == keyCode;
    }
    

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}