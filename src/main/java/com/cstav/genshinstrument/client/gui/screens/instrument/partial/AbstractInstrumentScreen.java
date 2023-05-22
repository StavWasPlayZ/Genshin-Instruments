package com.cstav.genshinstrument.client.gui.screens.instrument.partial;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.AbstractInstrumentOptionsScreen;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.packets.instrument.CloseInstrumentPacket;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.util.ModId;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractInstrumentScreen extends Screen {
    
    /**
     * The set pitch of all note buttons in this screen
     */
    private float pitch = ModClientConfigs.PITCH.get().floatValue();
    public float getPitch() {
        return pitch;
    }
    public void setPitch(float pitch) {
        this.pitch = NoteSound.clampPitch(pitch);
    }


    /**
     * A method to initialize the theme loader of this instrument.
     * All subclasses must call this method on game loading.
     */
    protected static final InstrumentThemeLoader initThemeLoader(String modId, String instrumentId) {
        return new InstrumentThemeLoader(
            new ResourceLocation(modId,
                getGlobalRootPath() + instrumentId + "/" + "instrument_style.json"
            )
        );
    }
    public abstract InstrumentThemeLoader getThemeLoader();


    public abstract String getInstrumentId();
    protected abstract AbstractInstrumentOptionsScreen initInstrumentOptionsScreen();

    
    /**
     * <p>Gets the sound array used by this instrument.
     * Its length must be equal to this Note Grid's {@code row*column}.</p>
     * Each sound is used on press by the their index on the grid.
     * @return The array of sounds used by this instruments.
     */
    public abstract NoteSound[] getSounds();

    /**
     * @return A map holding an integer key as its keycode and a {@link NoteButton} as its value.
     * All notes are to be present in this map.
     */
    public abstract Map<Key, NoteButton> noteMap();

    /**
     * @return The path of the root directory of all instruments
     */
    public static String getGlobalRootPath() {
        return "textures/gui/instrument/";
    }
    public ResourceLocation getResourceFromGlob(final String path) {
        return new ResourceLocation(getModId(), path);
    }
    /**
     * Shorthand for {@code getRootPath() + getInstrumentId()}
     */
    protected String getPath() {
        return getGlobalRootPath() + getInstrumentId() + "/";
    }

    public String getModId() {
        return (this instanceof ModId) ? ((ModId)this).modid() : Main.MODID;
    }

    
    /**
     * @param path The desired path to obtain from the root directory
     * @return The resource contained in this instrument's root directory
     * @see {@link AbstractInstrumentScreen#getInstrumentResourcesLocation()}
     * @see {@link AbstractInstrumentScreen#getResourceFrom(ResourceLocation, String)}
     */
    public ResourceLocation getResourceFromRoot(final String path) {
        return new ResourceLocation(getModId(), getPath() + path);
    }


    protected final AbstractInstrumentOptionsScreen optionsScreen = initInstrumentOptionsScreen();
    
    public final InteractionHand interactionHand;
    public AbstractInstrumentScreen(final InteractionHand hand) {
        super(CommonComponents.EMPTY);

        interactionHand = hand;
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
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        final NoteButton note = getNoteByKey(pKeyCode);
        
        if (note != null) {
            note.play();
            return true;
        }

        return ioa() ? optionsScreen.keyPressed(pKeyCode, pScanCode, pModifiers)
            : super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        unlockFocused();

        final NoteButton note = getNoteByKey(pKeyCode);
        if (note != null)
            note.locked = false;

        return ioa() ? optionsScreen.keyReleased(pKeyCode, pScanCode, pModifiers)
            : super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (ioa())
            return optionsScreen.mouseReleased(pMouseX, pMouseY, pButton);

        unlockFocused();

        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    public NoteButton getNoteByKey(final int keyCode) {
        final Key key = Type.KEYSYM.getOrCreate(keyCode);

        return noteMap().containsKey(key) ? noteMap().get(key) : null;
    }
    /**
     * Unlocks any focused {@link NoteButton}s
     */
    private void unlockFocused() {
        if ((getFocused() != null) && (getFocused() instanceof NoteButton))
            ((NoteButton)getFocused()).locked = false;
    }


    //#region Making the options screen function
    private boolean isOptionsActive = false;

    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        if (ioa())
            optionsScreen.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    protected void onOptionsOpen() {
        isOptionsActive = true;
        setFocused(null);
    }
    protected void onOptionsClose() {
        isOptionsActive = false;
    }


    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return ioa() ? optionsScreen.mouseClicked(pMouseX, pMouseY, pButton)
            : super.mouseClicked(pMouseX, pMouseY, pButton);
    }
    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return ioa() ? optionsScreen.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)
            : super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }
    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        if (ioa())
            optionsScreen.mouseMoved(pMouseX, pMouseY);
        else
            super.mouseMoved(pMouseX, pMouseY);
    }
    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        return ioa() ? optionsScreen.mouseScrolled(pMouseX, pMouseY, pDelta)
            : super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }


    /**
     * Shorthand for {@link AbstractInstrumentScreen#isOptionsActive isOptionsActive}.
     */
    private boolean ioa() {
        return isOptionsActive;
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