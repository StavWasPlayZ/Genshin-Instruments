package com.cstav.genshinstrument.client.gui.screens.instrument.partial;

import org.jetbrains.annotations.NotNull;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.AbstractInstrumentOptionsScreen;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.packets.instrument.CloseInstrumentPacket;
import com.cstav.genshinstrument.sound.NoteSound;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractInstrumentScreen extends Screen {

    // Abstract implementations
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
    protected abstract AbstractInstrumentOptionsScreen initInstrumentOptionsScreen();

    // Any subclass must make their own InstrumentThemeLoader
    public abstract InstrumentThemeLoader getThemeLoader();

    // Public
    /**
     * <p>Gets the sound array used by this instrument.
     * Its length must be equal to this Note Grid's {@code row*column}.</p>
     * Each sound is used on press by the their index on the grid.
     * @return The array of sounds used by this instruments.
     */
    public abstract NoteSound[] getSounds();
    /**
     * @return All possible label values this instrument's notes can have
     */

    public abstract Iterable<NoteButton> noteIterable();

    /**
     * Shorthand for {@code "textures/gui/instrument/" + instrumentId}
     */
    protected static String genPath(final String instrumentId) {
        return "textures/gui/instrument/" + instrumentId;
    }
    /**
     * Shorthand for {@code "textures/gui/instrument/" + instrumentId}
     */
    protected static String genStylerPath(final String instrumentId) {
        return genPath(instrumentId) + "/instrument_style.json";
    }

    
    /**
     * @param path The desired path to obtain from the root directory
     * @return The resource contained in this instrument's root directory
     * @see {@link AbstractInstrumentScreen#getInstrumentResourcesLocation()}
     * @see {@link AbstractInstrumentScreen#getResourceFrom(ResourceLocation, String)}
     */
    public ResourceLocation getResourceFromRoot(final String path) {
        return new ResourceLocation(
            getInstrumentResourcesLocation().getNamespace(),
            getInstrumentResourcesLocation().getPath() + "/" + path
        );
    }


    protected final AbstractInstrumentOptionsScreen optionsScreen = initInstrumentOptionsScreen();
    
    public final ItemStack instrument;
    public AbstractInstrumentScreen(final ItemStack instrument) {
        super(Component.empty());

        this.instrument = instrument;
        optionsScreen.setOnCloseRunnable(this::onOptionsClose);
    }


    @Override
    protected void init() {
        optionsScreen.init(minecraft, width, height);
    }
    /**
     * Initializes a new button responsible for popping up the options menu for this instrument.
     * Called during {@link Screen#init}.
     * @param vertOffset The vertical offset at which this button will be rendered.
     * @return A new Instrument Options button
     */
    protected AbstractWidget initOptionsButton(final int vertOffset) {
        final Button button = Button.builder(
            Component.translatable("button.genshinstrument.instrumentOptions").append("..."), (btn) -> onOptionsOpen()
        )
        .width(150)
        .build();

        button.setPosition((width - button.getWidth())/2, vertOffset - button.getHeight()/2);

        addRenderableWidget(button);
        return button;
    }


    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        optionsScreen.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }


    //#region Making the options screen function

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
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return optionsScreen.keyPressed(pKeyCode, pScanCode, pModifiers) || super.keyPressed(pKeyCode, pScanCode, pModifiers);
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

    //#endregion


    @Override
    public void onClose() {
        minecraft.player.getCapability(InstrumentOpenProvider.INSTRUMENT_OPEN).ifPresent((lyreOpen) ->
            lyreOpen.setOpen(false)
        );
        ModPacketHandler.sendToServer(new CloseInstrumentPacket());
        super.onClose();
    }
    

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}