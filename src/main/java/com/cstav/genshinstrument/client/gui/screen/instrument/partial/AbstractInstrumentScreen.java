package com.cstav.genshinstrument.client.gui.screen.instrument.partial;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.GenshinConsentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.BaseInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.AbstractInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings;
import com.cstav.genshinstrument.client.midi.MidiController;
import com.cstav.genshinstrument.event.MidiEvent;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.instrument.CloseInstrumentPacket;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.util.CommonUtil;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.logging.LogUtils;

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
        return new ResourceLocation(getSourcePath().getNamespace(), getGlobalRootPath() + path);
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
            ? CommonUtil.withSuffix(InstrumentThemeLoader.GLOBAL_LOC, "/"+path)
            : CommonUtil.withPath(getSourcePath(), getPath() + path);
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
        final Button button = new Button(0, 0,
            150, 20,
            Component.translatable("button.genshinstrument.instrumentOptions").append("..."),
            (btn) -> onOptionsOpen()
        );

        button.x = (width - button.getWidth())/2;
        button.y = vertOffset - button.getHeight()/2;

        addRenderableWidget(button);
        return button;
    }


    protected void loadMidiDevices() {
        final int infoIndex = ModClientConfigs.MIDI_DEVICE_INDEX.get();
        if (infoIndex == -1)
            return;


        MidiController.reloadIfEmpty();
        if (infoIndex > (MidiController.DEVICES.size() - 1)) {
            LogUtils.getLogger().warn("MIDI device out of range; setting device to none");
            ModClientConfigs.MIDI_DEVICE_INDEX.set(-1);
            return;
        }

        if (!MidiController.isLoaded(infoIndex)) {
            MidiController.loadDevice(infoIndex);
            MidiController.openForListen();
        }
    }


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

        unlockFocused(pKeyCode);

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
        for (final NoteButton note : notesIterable())
            if (note.locked) {
                note.locked = false;
                return;
            }
    }
    /**
     * Unlocks the specified {@link NoteButton} that matches the given key.
     * If it is not present, will perform {@link AbstractInstrumentScreen#unlockFocused} instead.
     */
    private void unlockFocused(final int keyCode) {
        final NoteButton note = getNoteByKey(keyCode);

        if (note == null)
            unlockFocused();
        else
            note.locked = false;
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

        if (minecraft.screen instanceof AbstractInstrumentOptionsScreen instrumentOptionsScreen) {
            if (instrumentOptionsScreen.isOverlay)
                return Optional.of(instrumentOptionsScreen.instrumentScreen);
        }

        return Optional.empty();
    }
    

    @Override
    public boolean isPauseScreen() {
        return false;
    }


    /* ----------- MIDI implementations ----------- */

    /**
     * Defines wether this instrument can handle MIDI messages.
     * Must override {@link AbstractInstrumentScreen#handleMidiPress} to function.
     */
    public boolean isMidiInstrument() {
        return false;
    }


    private NoteButton pressedMidiNote = null;
    public void onMidi(final MidiEvent event) {
        if (!isMidiInstrument())
            return;


        // Release previously pressed notes    
        if (pressedMidiNote != null)
            pressedMidiNote.locked = false;
            
        final byte[] message = event.message.getMessage();
        // We only care for presses
        if (message[0] != -112)
            return;


        // So we don't do tranpositions on a sharpened scale
        resetTransposition();

        final int note = handleMidiOverflow(getLowC(message[1]));
        if (note == -99)
            return;


        //NOTE: Math.abs(getPitch()) was here instead, but transposition seems fair enough
        final int pitch = 0;

        pressedMidiNote = handleMidiPress(note, pitch);
        if (pressedMidiNote != null)
            pressedMidiNote.play();
    }

    /**
     * Fires when a MIDI note is being pressed sucessfully, only if this is {@link AbstractInstrumentScreen#isMidiInstrument a midi instrument}.
     * @param note The raw note being pressed by the MIDI device, {@link AbstractInstrumentScreen#getLowC relative to low C} {@code note % 12}
     * @param pitch The scale played by the MIDI device; the absolute value of current pitch saved in the client configs (Always set to 0 here)
     * @return The pressed note button. Null if none.
     */
    protected NoteButton handleMidiPress(int note, int pitch) {
        return null;
    }


    protected boolean shouldSharpen(final int layoutNote, final boolean higherThan3, final int pitch) {
        // Much testing and maths later
        // The logic here is that accidentals only occur when the note number is
        // the same divisable as the pitch itself
        boolean shouldSharpen = (layoutNote % 2) != (pitch % 2);
        
        // Negate logic for notes higher than 3 on the scale
        if (higherThan3)
            shouldSharpen = !shouldSharpen;

        // Negate logic for notes beyond the 12th note
        if (layoutNote < pitch)
            shouldSharpen = !shouldSharpen;

        return shouldSharpen;
    }
    /**
     * Minecraft pitch limitations will want us to go down a pitch instead of up.
     */
    protected boolean shouldFlatten(final boolean shouldSharpen) {
        return shouldSharpen && (getPitch() == NoteSound.MAX_PITCH);
    }
    
    protected void transposeMidi(final boolean shouldSharpen, final boolean shouldFlatten) {
        if (shouldFlatten)
            transposeDown();
        else if (shouldSharpen)
            transposeUp();
    }


    public boolean allowMidiOverflow() {
        return false;
    }

    /**
     * Extends the usual limitation of octaves by 2 by adjusting the pitch higher/lower
     * when necessary
     * @param note The current note
     * @return The new shited (or not) note to handle, or -99 if overflows
     */
    protected int handleMidiOverflow(int note) {
        if (!allowMidiOverflow() || !ModClientConfigs.EXTEND_OCTAVES.get()) {
            if ((note < minMidiNote()) || (note >= maxMidiNote()))
                return -99;
            return note;
        }


        final int minPitch = NoteSound.MIN_PITCH, maxPitch = NoteSound.MAX_PITCH;

        // Set the pitch
        if (note < minMidiNote()) {
            // Minecraft pitch limitations
            if (note < minMidiOverflow())
                return -99;

            if (getPitch() != minPitch) {
                setPitch(minPitch);
                ModClientConfigs.PITCH.set(minPitch);
            }
        } else if (note >= maxMidiNote()) {
            if (note >= maxMidiOverflow())
                return -99;

            if (getPitch() != maxPitch) {
                setPitch(maxPitch);
                ModClientConfigs.PITCH.set(maxPitch);
            }
        }

        if (getPitch() == minPitch) {
            // Check if we are an octave above
            if (note >= minMidiNote()) {
                // Reset if so
                setPitch(0);
                ModClientConfigs.PITCH.set(0);
            }
            // Shift the note to the lower octave
            else
                note -= minPitch;
        }
        else if (getPitch() == maxPitch) {
            if (note < maxMidiNote()) {
                setPitch(0);
                ModClientConfigs.PITCH.set(0);
            }
            else
                note -= maxPitch;
        }

        return note;
    }

    protected int minMidiNote() {
        return 0;
    }
    protected int maxMidiNote() {
        return NoteSound.MAX_PITCH * 3;
    }

    protected int maxMidiOverflow() {
        return maxMidiNote() + 12;
    }
    protected int minMidiOverflow() {
        return minMidiNote() - 12;
    }


    /**
     * @return The MIDI note adjusted by -48, as well as the perferred shift accounted.
     * Assumes middle C is 60 as per MIDI specifications.
     */
    protected int getLowC(final int note) {
        return note - ModClientConfigs.OCTAVE_SHIFT.get() * 12 - 48;
    }

}