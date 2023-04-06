package com.cstav.genshinstrument.client.gui.screens.instrument;

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
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractInstrumentScreen extends Screen {
    public static final int ROWS = 7, COLUMNS = 3;


    // Abstract implementations
    public NoteGrid initNoteGrid() {
        return new NoteGrid(
            ROWS, COLUMNS, getSounds(),
            () -> getThemeLoader().getNoteTheme().getNumeric(),
            () -> getThemeLoader().getPressedNoteTheme().getNumeric()
        );
    }
    public InstrumentOptionsScreen initInstrumentOptionsScreen() {
        return new InstrumentOptionsScreen(title, true, this);
    }
    public abstract SoundEvent[] getSounds();
    // Any subclass must make their own LyreThemeLoader
    public abstract InstrumentThemeLoader getThemeLoader();


    public AbstractInstrumentScreen() {
        super(Component.empty());
    }


    public final NoteGrid noteGrid = initNoteGrid();
    final InstrumentOptionsScreen optionsScreen = initInstrumentOptionsScreen();

    @Override
    protected void init() {
        final AbstractWidget grid = noteGrid.genNoteGridWidget(.9f, width, height);

        addRenderableWidget(grid);
        addRenderableWidget(initCustomizeButton(grid.getY() - 15));

        optionsScreen.init(minecraft, width, height);
    }
    AbstractWidget initCustomizeButton(final int vertOffset) {
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


    void onOptionsOpen() {
        setSettingsOpen(true);
    }
    void onOptionsClose() {
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


    static void setChildrenActive(final ContainerEventHandler container, final boolean active) {
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


    // public static void open(final Class<? extends AbstractInstrumentScreen> instrumentScreen) {
    //     try {
    //         Minecraft.getInstance().setScreen(instrumentScreen.getDeclaredConstructor().newInstance());
    //     } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
    //             | NoSuchMethodException | SecurityException e) {
    //         LOGGER.error(
    //             "Tried to open instrument screen " + instrumentScreen.getSimpleName() + ", but met with failure"
    //         , e);
    //     }
    // }

}