package com.cstav.genshinstrument.client.gui.screen.instrument.partial;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.GenshinConsentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.AbstractInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.BaseInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings;
import com.cstav.genshinstrument.client.midi.MidiController;
import com.cstav.genshinstrument.client.midi.MidiOutOfRangeException;
import com.cstav.genshinstrument.event.MidiEvent;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.instrument.CloseInstrumentPacket;
import com.cstav.genshinstrument.sound.NoteSound;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
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


    public double volume = ModClientConfigs.VOLUME.get();
    /**
     * Convinience method to get the {@link AbstractInstrumentScreen#volume volume}
     * of this instrument as a {@code float}
     */
    public float volume() {
        return (float)volume;
    }


    /**
     * Sets the sounds of this instruments.
     * @apiNote This method should generally be overitten by subclasses to keep their respected order of notes
     */
    public void setNoteSounds(final NoteSound[] sounds) {
        final Iterator<NoteButton> noteIterator = notesIterable().iterator();

        int i = 0;
        while (noteIterator.hasNext() && (i < sounds.length))
            noteIterator.next().setSound(sounds[i++]);


        if (noteIterator.hasNext() || (i < sounds.length))
            LogUtils.getLogger().warn("Not all sounds could be set for this instrument!");
    }


    public abstract InstrumentThemeLoader getThemeLoader();
    public abstract ResourceLocation getInstrumentId();
    
    protected abstract BaseInstrumentOptionsScreen initInstrumentOptionsScreen();


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
     * @return The path of the root directory of the mod
     */
    public static String getGlobalRootPath() {
        return "textures/gui/genshinstrument/";
    }
    /**
     * @return The resource laied inside of this instrument's directory
     */
    public ResourceLocation getResourceFromGlob(final String path) {
        return getSourcePath().withPath(getGlobalRootPath() + "instrument/" + path);
    }
    public static ResourceLocation getInternalResourceFromGlob(final String path) {
        return new ResourceLocation(GInstrumentMod.MODID, getGlobalRootPath() + path);
    }

    public static ResourceLocation getInstrumentRootPath(final ResourceLocation instrumentId) {
        return instrumentId.withPath(AbstractInstrumentScreen.getGlobalRootPath() + "instrument/" + instrumentId.getPath());
    }

    /**
     * Gets the resource path under this instrument.
     * It will usually be {@code textures/gui/genshinstrument/instrument/<instrument>/}.
     * {@code instrument} is as specified by {@link AbstractInstrumentScreen#getSourcePath getSourcePath}.
     */
    protected String getPath() {
        return getGlobalRootPath() + "instrument/" + getSourcePath().getPath() + "/";
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
     * @param path The desired path to obtain from the instrument's root directory
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
     * Gets The desired path to obtain from either the instrument's root or global directory.
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

    // To omit background
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        for (Renderable renderable : this.renderables)
            renderable.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
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
    public static final int MIN_MIDI_VELOCITY = 10;

    /**
     * Defines wether this instrument can handle MIDI messages.
     * @apiNote Override {@link AbstractInstrumentScreen#handleMidiPress} to handle MIDI input
     */
    public boolean isMidiInstrument() {
        return false;
    }

    
    private NoteButton pressedMidiNote = null;

    public void onMidi(final MidiEvent event) {
        if (!canPerformMidi(event))
            return;

        final byte[] message = event.message.getMessage();


        // So we don't do tranpositions on a sharpened scale
        resetTransposition();

        final int note;
        try {
            note = handleMidiOverflow(getLowC(message[1]));
        } catch (MidiOutOfRangeException e) {
            return;
        }


        //NOTE: Math.abs(getPitch()) was here instead, but transposition seems fair enough
        final int pitch = 0;

        // Handle dynamic touch
        final double prevVolume = volume;
        if (!ModClientConfigs.FIXED_TOUCH.get())
            volume *= Math.max(MIN_MIDI_VELOCITY, message[2]) / 127D;


        pressedMidiNote = handleMidiPress(note, pitch);
        if (pressedMidiNote != null)
            pressedMidiNote.play();


        volume = prevVolume;
    }

    protected boolean canPerformMidi(final MidiEvent event) {
        if (!isMidiInstrument())
            return false;
    
        final byte[] message = event.message.getMessage();

        // Release the previously pressed note
        if (pressedMidiNote != null)
            pressedMidiNote.locked = false;

        // We only care for press events:
        
        // Ignore last 4 bits (don't care about the channel atm)
        final int eventType = (message[0] >> 4) << 4;
        if (eventType != -112)
            return false;

        if (!ModClientConfigs.ACCEPT_ALL_CHANNELS.get())
            if ((message[0] - eventType) != ModClientConfigs.MIDI_CHANNEL.get())
                return false;


        return true;
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
     * @return The new shifted (or not) note to handle
     * @throws MidiOutOfRangeException If the pressed note exceeds the allowed MIDI range (overflows)
     */
    protected int handleMidiOverflow(int note) throws MidiOutOfRangeException {
        if (!allowMidiOverflow() || !ModClientConfigs.EXTEND_OCTAVES.get()) {
            if ((note < minMidiNote()) || (note >= maxMidiNote()))
                throw new MidiOutOfRangeException();

            return note;
        }


        final int minPitch = NoteSound.MIN_PITCH, maxPitch = NoteSound.MAX_PITCH;

        // Set the pitch
        if (note < minMidiNote()) {
            if (note < minMidiOverflow())
                throw new MidiOutOfRangeException();

            if (getPitch() != minPitch)
                overflowMidi(minPitch);
                
        } else if (note >= maxMidiNote()) {
            if (note >= maxMidiOverflow())
                throw new MidiOutOfRangeException();

            if (getPitch() != maxPitch)
                overflowMidi(maxPitch);
        }

        // Check if we are an octave above/below
        // and reset back to pitch C
        if (getPitch() == minPitch) {
            if (note >= minMidiNote())
                setPitch(0);
            // Shift the note to the higher octave
            else
                note += 12;
        }
        else if (getPitch() == maxPitch) {
            if (note < maxMidiNote())
                setPitch(0);
            else
                note -= 12;
        }

        return note;
    }

    private void overflowMidi(final int desiredPitch) {
        setPitch(desiredPitch);
        // Reset pitch to C to avoid coming back down for a mess
        if (!ModClientConfigs.PITCH.get().equals(0))
            ModClientConfigs.PITCH.set(0);
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