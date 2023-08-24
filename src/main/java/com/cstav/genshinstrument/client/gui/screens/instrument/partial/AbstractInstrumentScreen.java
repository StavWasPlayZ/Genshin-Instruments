package com.cstav.genshinstrument.client.gui.screens.instrument.partial;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.GenshinConsentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.options.ModOptionsScreen;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.BaseInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings;
import com.cstav.genshinstrument.client.midi.MidiController;
import com.cstav.genshinstrument.event.MidiEvent;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.instrument.CloseInstrumentPacket;
import com.cstav.genshinstrument.sound.NoteSound;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;

import net.minecraft.client.Minecraft;
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
    @SuppressWarnings("resource")
    public int getNoteSize() {
        return switch (Minecraft.getInstance().options.guiScale().get()) {
            case 1 -> 36;
            case 2 -> 46;
            case 3 -> 48;
            case 4 -> 40;
            case 5 -> 35;
            case 6 -> 30;

            default -> 35;
        };
    }
    
    /**
     * The set pitch of all note buttons in this screen
     */
    private int pitch;
    public int getPitch() {
        return pitch;
    }
    public void setPitch(int pitch) {
        this.pitch = NoteSound.clampPitch(pitch);
        notesIterable().forEach((note) -> note.setPitch(this.pitch));
    }

    protected void initPitch(final Consumer<Integer> pitchConsumer) {
        pitchConsumer.accept(ModClientConfigs.PITCH.get().intValue());
    }
    public void resetPitch() {
        initPitch(this::setPitch);
    }


    public abstract InstrumentThemeLoader getThemeLoader();
    public abstract ResourceLocation getInstrumentId();
    
    protected abstract BaseInstrumentOptionsScreen initInstrumentOptionsScreen();

    /**
     * @return The location of all labels present in this instrument
     */
    public abstract ResourceLocation getNoteSymbolsLocation();


    /**
     * @return The layout of the note names accross the instrument's rows.
     * Null for when this instrument does not support note names.
     * @implNote All built-in instruments' layouts are derived from
     * <a href=https://github.com/Specy/genshin-music/blob/19dfe0e2fb8081508bd61dd47289dcb2d89ad5e3/src/Config.ts#L114>
     * Specy's Genshin Music app
     * </a>
     */
    public String[] noteLayout() {
        return null;
    }

    /**
     * @return Whether this instrument is derived from Genshin Impact
     * @apiNote This value will help the mod determine whether a disclaimer pop-up should appear upon opening this
     * instrument.
     */
    public boolean isGenshinInstrument() {
        return true;
    }

    /**
     * Handles this instrument being closed by either recieving a false signal from {@link InstrumentOpenProvider#isOpen}
     * or, if it is an item, if the item has been ripped out of the player's hands.
     * @return Whether the instrument has closed as a result of this method
     */
    public void handleAbruptClosing() {
        if (!InstrumentOpenProvider.isOpen(minecraft.player))
            onClose(false);
    }


    /**
     * @return The first {@link NoteButton} that matches the description of the given identifier
     */
    public NoteButton getNoteButton(final NoteButtonIdentifier noteIdentifier) throws NoSuchElementException {
        for (NoteButton note : notesIterable())
            if (noteIdentifier.matches(note))
                return note;

        throw new NoSuchElementException("Could not find a note in "+getClass().getSimpleName()+" based on the given identifier");
    }

    /**
     * @return A map holding an integer key as its keycode and a {@link NoteButton} as its value.
     */
    public abstract Map<Key, NoteButton> noteMap();
    public Iterable<NoteButton> notesIterable() {
        return noteMap().values();
    }

    /**
     * @return The path of the root directory of all instruments
     */
    public static String getGlobalRootPath() {
        return "textures/gui/instrument/";
    }
    public ResourceLocation getResourceFromGlob(final String path) {
        return getSourcePath().withPath(getGlobalRootPath() + path);
    }
    public static ResourceLocation getInternalResourceFromGlob(final String path) {
        return new ResourceLocation(GInstrumentMod.MODID, getGlobalRootPath() + path);
    }
    /**
     * Gets the resource path under this instrument.
     * It will usually be {@code textures/gui/instrument/<instrument>/}.
     * {@code instrument} is as specified by {@link AbstractInstrumentScreen#getSourcePath getSourcePath}.
     */
    protected String getPath() {
        return getGlobalRootPath() + getSourcePath().getPath() + "/";
    }

    /**
     * Override this method if you want to reference another directory for resources
     */
    public ResourceLocation getSourcePath() {
        return getInstrumentId();
    }

    public String getModId() {
        return getInstrumentId().getNamespace();
    }

    
    /**
     * @param path The desired path to obtain from the root directory
     * @param considerGlobal If {@link InstrumentThemeLoader#isGlobalThemed() a global resource pack is enabled}, take the resource from there
     * @return The resource contained in this instrument's root directory
     * @see {@link AbstractInstrumentScreen#getInstrumentResourcesLocation()}
     * @see {@link AbstractInstrumentScreen#getResourceFrom(ResourceLocation, String)}
     */
    public ResourceLocation getResourceFromRoot(final String path, final boolean considerGlobal) {
        return (considerGlobal && InstrumentThemeLoader.isGlobalThemed())
            ? InstrumentThemeLoader.GLOBAL_LOC.withSuffix("/"+path)
            : getSourcePath().withPath(getPath() + path);
    }
    /**
     * Gets The desired path to obtain from either the root or global directory.
     * The global directory will be used if {@link InstrumentThemeLoader#isGlobalThemed()} is true.
     * @return The resource contained in this instrument's root directory
     * @see {@link AbstractInstrumentScreen#getInstrumentResourcesLocation()}
     * @see {@link AbstractInstrumentScreen#getResourceFrom(ResourceLocation, String)}
     */
    public ResourceLocation getResourceFromRoot(final String path) {
        return getResourceFromRoot(path, true);
    }


    public final BaseInstrumentOptionsScreen optionsScreen = initInstrumentOptionsScreen();
    
    public final Optional<InteractionHand> interactionHand;
    public AbstractInstrumentScreen(final InteractionHand hand) {
        super(CommonComponents.EMPTY);

        interactionHand = Optional.ofNullable(hand);
    }


    @Override
    protected void init() {
        loadMidiDevices();

        resetPitch();
        optionsScreen.init(minecraft, width, height);

        if (isGenshinInstrument() && !ModClientConfigs.ACCEPTED_GENSHIN_CONSENT.get())
            minecraft.setScreen(new GenshinConsentScreen(this));
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


    protected void loadMidiDevices() {
        final int infoIndex = ModClientConfigs.MIDI_DEVICE_INDEX.get();

        if (infoIndex >= MidiController.DEVICES.size()) {
            ModClientConfigs.MIDI_DEVICE_INDEX.set(-1);
            return;
        }

        if ((infoIndex != -1) && !MidiController.isLoaded(infoIndex)) {
            MidiController.loadDevice(infoIndex);
            MidiController.openForListen();
        }
    }

    public void onMidi(final MidiEvent event) {}


    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (checkPitchTransposeUp(pKeyCode, pScanCode))
            return true;

        final NoteButton note = getNoteByKey(pKeyCode);
        
        if (note != null) {
            note.play();
            return true;
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (checkTransposeDown(pKeyCode, pScanCode))
            return true;

        unlockFocused();

        final NoteButton note = getNoteByKey(pKeyCode);
        if (note != null)
            note.locked = false;

        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    private boolean pitchChanged;
    protected boolean checkPitchTransposeUp(int pKeyCode, int pScanCode) {
        if (pitchChanged)
            return false;

        if (checkTranposeUpKey(pKeyCode, pScanCode)) {
            transposeUp();
            return true;
        }
        else if (checkTranposeDownKey(pKeyCode, pScanCode)) {
            transposeDown();
            return true;
        }

        return false;
    }
    protected boolean checkTransposeDown(int pKeyCode, int pScanCode) {
        if (!pitchChanged)
            return false;

        if (checkTranposeUpKey(pKeyCode, pScanCode) || checkTranposeDownKey(pKeyCode, pScanCode)) {
            resetTransposition();
            return true;
        }

        return false;
    }


    public void transposeUp() {
        setPitch(getPitch() + 1);
        pitchChanged = true;
    }
    public void transposeDown() {
        setPitch(getPitch() - 1);
        pitchChanged = true;
    }

    public void resetTransposition() {
        resetPitch();
        pitchChanged = false;
    }

    /**
     * @return Whether this instrument's pitch is being tranposed up/down as requested by the keybinds
     */
    public boolean isTranposed() {
        return pitchChanged;
    }


    /**
     * @return {@code true} if the given key is being used by this instrument.
     * Otherwise, {@code false}.
     */
    public boolean isKeyConsumed(final int keyCode, final int scanCode) {
        return (getNoteByKey(keyCode) != null)
            || checkTranposeDownKey(keyCode, scanCode) || checkTranposeUpKey(keyCode, scanCode);
    }

    protected boolean checkTranposeDownKey(final int keyCode, final int scanCode) {
        return InstrumentKeyMappings.TRANSPOSE_DOWN_MODIFIER.get().matches(keyCode, scanCode);
    }
    protected boolean checkTranposeUpKey(final int keyCode, final int scanCode) {
        return InstrumentKeyMappings.TRANSPOSE_UP_MODIFIER.get().matches(keyCode, scanCode);
    }


    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
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


    private boolean isOptionsScreenActive;
    public boolean isOptionsScreenActive() {
        return isOptionsScreenActive;
    }

    public void onOptionsOpen() {
        setFocused(null);
        minecraft.pushGuiLayer(optionsScreen);

        resetPitch();

        isOptionsScreenActive = true;
    }
    public void onOptionsClose() {
        isOptionsScreenActive = false;
    }


    @Override
    public void onClose() {
        onClose(true);
    }
    public void onClose(final boolean notify) {
        // This should always be false after the above move to server todo is implemented
        if (notify) {
            InstrumentOpenProvider.setClosed(minecraft.player);
            ModPacketHandler.sendToServer(new CloseInstrumentPacket());
        }

        if (isOptionsScreenActive)
            optionsScreen.onClose();
        super.onClose();
    }


    /**
     * @return The current instrument screen, if present
     */
    public static Optional<AbstractInstrumentScreen> getCurrentScreen(final Minecraft minecraft) {
        if (minecraft.screen instanceof AbstractInstrumentScreen)
            return Optional.of((AbstractInstrumentScreen)minecraft.screen);

        if (minecraft.screen instanceof ModOptionsScreen instrumentOptionsScreen) {
            if (instrumentOptionsScreen.isOverlay)
                return Optional.of(instrumentOptionsScreen.instrumentScreen);
        }

        return Optional.empty();
    }
    

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}