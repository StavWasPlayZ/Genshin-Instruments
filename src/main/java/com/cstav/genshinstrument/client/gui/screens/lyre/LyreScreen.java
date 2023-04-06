package com.cstav.genshinstrument.client.gui.screens.lyre;

import org.jetbrains.annotations.NotNull;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.gui.screens.options.InstrumentOptionsScreen;
import com.cstav.genshinstrument.client.keyMaps.KeyMappings;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.packets.lyre.CloseLyrePacket;
import com.cstav.genshinstrument.util.RGBColor;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
//NOTE: There just to make it load on mod startup
@EventBusSubscriber(bus = Bus.MOD)
public class LyreScreen extends Screen {
    public static final int ROWS = 7, COLUMNS = 3;

    //TODO abstract
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
    // Any subclass must make their own LyreThemeLoader
    private static final InstrumentThemeLoader THEME_LOADER = new InstrumentThemeLoader(
        new ResourceLocation(Main.MODID, "textures/gui/lyre/lyre_style.json"),
        new RGBColor(255, 249, 239), new RGBColor(154, 228, 212)
    );



    public LyreScreen() {
        super(Component.empty());
    }


    public final NoteGrid noteGrid = new NoteGrid(
        ROWS, COLUMNS,
        () -> getThemeLoader().getNoteTheme().getNumeric(),
        () -> getThemeLoader().getPressedNoteTheme().getNumeric()
    );
    final InstrumentOptionsScreen optionsScreen = new InstrumentOptionsScreen(title, true, this);

    @Override
    protected void init() {
        final AbstractWidget grid = noteGrid.genNoteGridWidget(.9f, width, height);

        addRenderableWidget(grid);
        addRenderableWidget(initCustomizeButton(grid.getY() - 15));

        assert minecraft != null;
        optionsScreen.init(minecraft, width, height);
    }
    // Generalizing in case of override
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

        ModPacketHandler.sendToServer(new CloseLyrePacket());
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


    public static void open() {
        Minecraft.getInstance().setScreen(new LyreScreen());
    }

}