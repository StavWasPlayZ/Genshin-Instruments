package com.cstav.genshinstrument.client.gui.screen.instrument.partial;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.GenshinConsentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.AbstractInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.InstrumentOptionsScreen;
import com.cstav.genshinstrument.client.gui.widget.IconToggleButton;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings;
import com.cstav.genshinstrument.client.midi.InstrumentMidiReceiver;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.event.NoteSoundPlayedEvent;
import com.cstav.genshinstrument.networking.GIPacketHandler;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.instrument.c2s.CloseInstrumentPacket;
import com.cstav.genshinstrument.sound.NoteSound;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * The abstract implementation of a Genshin Instrument screen.
 */
@OnlyIn(Dist.CLIENT)
public abstract class InstrumentScreen extends Screen {
    private static final int VISIBILITY_BUTTON_MARGIN = 6;
    private static final String VISIBILITY_SPRITE_LOC = "textures/gui/sprites/icon/visibility/";

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

    /**
     * Sets the pitch of all notes in this instrument
     * @param pitch The new pitch to apply
     */
    public void setPitch(int pitch) {
        this.pitch = NoteSound.clampPitch(pitch);
        notesIterable().forEach((note) -> note.setPitch(this.pitch));
    }

    /**
     * Supplies the given consumer with the configured default pitch
     * set in {@link ModClientConfigs#PITCH}.
     */
    protected void initPitch(final Consumer<Integer> pitchConsumer) {
        pitchConsumer.accept(ModClientConfigs.PITCH.get());
    }
    public void resetPitch() {
        initPitch(this::setPitch);
    }


    /**
     * Represents the volume of this instrument in percentages (0% - 100%)
     */
    public int volume = (int)(ModClientConfigs.VOLUME.get() * 100);
    /**
     * Convenience method to get the {@link InstrumentScreen#volume volume}
     * of this instrument as a {@code float} percentage
     */
    public float volume() {
        return volume / 100f;
    }
    /**
     * Convenience method to set the {@link InstrumentScreen#volume volume}
     * of this instrument via a float percentage
     */
    public void setVolume(float volume) {
        this.volume = (int)(volume * 100);
    }


    /**
     * Sets the sounds of this instrument.
     * @apiNote This method should generally be overwritten by subclasses to keep their respected order of notes
     */
    public void setNoteSounds(final NoteSound[] sounds) {
        final Iterator<NoteButton> noteIterator = notesIterable().iterator();

        int i = 0;
        while (noteIterator.hasNext() && (i < sounds.length))
            noteIterator.next().setSound(sounds[i++]);


        if (noteIterator.hasNext() || (i < sounds.length))
            LogUtils.getLogger().warn("Not all sounds were set for instrument "+getInstrumentId()+"!");
    }

    private NoteLabelSupplier noteLabelSupplier;
    /**
     * Updates all buttons in this instrument to use
     * the specified label supplier
     * @param supplier The new supplier to use
     */
    public void setLabelSupplier(final NoteLabelSupplier supplier) {
        noteLabelSupplier = supplier;
        notesIterable().forEach((note) -> note.setLabelSupplier(supplier));
    }
    public NoteLabelSupplier getNoteLabelSupplier() {
        return noteLabelSupplier;
    }


    public abstract InstrumentThemeLoader getThemeLoader();
    public abstract ResourceLocation getInstrumentId();

    protected abstract InstrumentOptionsScreen initInstrumentOptionsScreen();


    /**
     * @return The layout of the note names across the instrument's rows.
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


    public final InstrumentMidiReceiver midiReceiver;
    /**
     * Initiates the MIDI handler of this instrument.
     * Override to implement MIDI support.
     */
    public InstrumentMidiReceiver initMidiReceiver() {
        return null;
    }

    /**
     * @return Whether this instrument can support MIDI input
     */
    public boolean isMidiInstrument() {
        return midiReceiver != null;
    }


    public void handleAbruptClosing() {
        if (!InstrumentOpenProvider.isOpen(minecraft.player))
            onClose(false);
    }


    /**
     * Uses either {@link InstrumentScreen#getNoteButton(NoteButtonIdentifier)}
     * or {@link InstrumentScreen#getNoteButton(NoteSound, int)}
     * based on whether the provided {@code noteIdentifier} is empty
     *
     * @see InstrumentScreen#identifyByPitch()
     */
    public NoteButton getNoteButton(Optional<NoteButtonIdentifier> noteIdentifier,
            NoteSound noteSound, int pitch) throws NoSuchElementException {

        if (noteIdentifier.isEmpty())
            return getNoteButton(noteSound, pitch);
        else
            return getNoteButton(noteIdentifier.get());
    }

    /**
     * @return The first {@link NoteButton} that matches the description of the given {@code noteIdentifier}.
     */
    public NoteButton getNoteButton(final NoteButtonIdentifier noteIdentifier) {
        for (NoteButton note : notesIterable())
            if (noteIdentifier.matches(note))
                return note;

        throw new NoSuchElementException("Could not find a note in "+getInstrumentId()+" based on the given identifier");
    }
    /**
     * @param noteSound The sound of the note button to find.
     * @param pitch The sound of the pitch to find.
     *
     * @return The first note button in this instrument
     * that matches the sound of the given {@code sound}.
     */
    public NoteButton getNoteButton(final NoteSound noteSound, final int pitch) {
        for (final NoteButton note : notesIterable()) {
            final NoteSound sound = note.getSound();

            if (!noteSound.equals(sound))
                continue;

            if (!identifyByPitch() || (note.getPitch() == pitch))
                return note;
        }

        throw new NoSuchElementException("Could not find a note in "+getInstrumentId()+" based on the given identifier");
    }

    /**
     * Upon {@link InstrumentScreen#getNoteButton(NoteSound, int) retrieving a note button},
     * defines whether the notes' pitch will be taken account by the comparator.
     *
     * @see InstrumentScreen#getNoteButton(NoteSound, int)
     */
    protected boolean identifyByPitch() {
        return false;
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
     * @return The resource laid inside of this instrument's directory
     */
    public ResourceLocation getResourceFromGlob(final String path) {
        return getSourcePath().withPath(getGlobalRootPath() + "instrument/" + path);
    }
    public static ResourceLocation getInternalResourceFromGlob(final String path) {
        return new ResourceLocation(GInstrumentMod.MODID, getGlobalRootPath() + path);
    }

    public static ResourceLocation getInstrumentRootPath(final ResourceLocation instrumentId) {
        return instrumentId.withPath(getGlobalRootPath() + "instrument/" + instrumentId.getPath());
    }

    /**
     * Gets the resource path under this instrument.
     * It will usually be {@code textures/gui/genshinstrument/instrument/<instrument>/}.
     * {@code instrument} is as specified by {@link InstrumentScreen#getSourcePath getSourcePath}.
     */
    protected String getPath() {
        return getGlobalRootPath() + "instrument/" + getSourcePath().getPath() + "/";
    }

    /**
     * Override this method if you want to reference another directory for resources
     */
    public ResourceLocation getSourcePath() {
        return getThemeLoader().subjectInstrumentId;
    }

    public String getModId() {
        return getInstrumentId().getNamespace();
    }


    /**
     * @param path The desired path to obtain from the instrument's root directory
     * @param considerGlobal If {@link InstrumentThemeLoader#isGlobalThemed() a global resource pack is enabled}, take the resource from there
     * @return The resource contained in this instrument's root directory
     */
    public ResourceLocation getResourceFromRoot(final String path, final boolean considerGlobal) {
        return (considerGlobal && InstrumentThemeLoader.isGlobalThemed())
            ? InstrumentThemeLoader.GLOBAL_LOC.withSuffix("/" + path)
            : getSourcePath().withPath(getPath() + path);
    }
    /**
     * Gets The desired path to obtain from either the instrument's root or global directory.
     * The global directory will be used if {@link InstrumentThemeLoader#isGlobalThemed()} is true.
     * @return The resource contained in this instrument's root directory
     */
    public ResourceLocation getResourceFromRoot(final String path) {
        return getResourceFromRoot(path, true);
    }


    public final InstrumentOptionsScreen optionsScreen = initInstrumentOptionsScreen();

    public InstrumentScreen() {
        super(CommonComponents.EMPTY);
        midiReceiver = initMidiReceiver();
    }

    protected IconToggleButton visibilityButton;

    @Override
    protected void init() {
        resetPitch();
        optionsScreen.init(minecraft, width, height);

        boolean wasEnabled = false;
        // Could be not null on screen refresh event
        if (visibilityButton != null)
            wasEnabled = visibilityButton.enabled();

        visibilityButton = initVisibilityButton();
        addRenderableWidget(visibilityButton);
        visibilityButton.setEnabled(wasEnabled);

        if (isGenshinInstrument() && !ModClientConfigs.ACCEPTED_GENSHIN_CONSENT.get())
            minecraft.setScreen(new GenshinConsentScreen(this));
    }

    /**
     * Initializes a new button responsible for popping up the options menu for this instrument.
     * Called during {@link Screen#init}.
     * @param vertOffset The vertical offset at which this button will be rendered.
     * @return A new Instrument Options button
     */
    protected Button initOptionsButton(final int vertOffset) {
        final Button button = Button.builder(
            Component.translatable("button.genshinstrument.instrumentOptions").append("..."), (btn) -> onOptionsOpen()
        )
            .width(150)
            .build();

        button.setPosition((width - button.getWidth())/2, vertOffset - button.getHeight()/2);

        addRenderableWidget(button);
        return button;
    }
    /**
     * Initialized a new button responsible for hiding the screen's GUI.
     * If enabled, the screen is hidden.
     * @return A new visibility toggle button
     */
    protected IconToggleButton initVisibilityButton() {
        return new IconToggleButton(
            VISIBILITY_BUTTON_MARGIN, VISIBILITY_BUTTON_MARGIN,
            new ResourceLocation(GInstrumentMod.MODID, VISIBILITY_SPRITE_LOC + "enabled.png"),
            new ResourceLocation(GInstrumentMod.MODID, VISIBILITY_SPRITE_LOC + "disabled.png"),
            (btn) -> onInstrumentRenderStateChanged(instrumentRenders())
        );
    }


    /**
     * Play a note button as a foreign
     * player. "Shared instrument screen".
     * @param event The event referring to this function
     */
    public void foreignPlay(final InstrumentPlayedEvent<?> event) {
        if (!(event instanceof NoteSoundPlayedEvent e))
            return;

        try {

            getNoteButton(
                event.soundMeta().noteIdentifier(),
                e.sound(), event.soundMeta().pitch()
            ).playNoteAnimation(true);

        } catch (Exception ignore) {
            // Button was prolly just not found
        }
    }


    /**
     * @return True whether this instrument's GUI
     * is to be rendered
     */
    public boolean instrumentRenders() {
        return !visibilityButton.enabled();
    }
    protected void onInstrumentRenderStateChanged(final boolean isVisible) {
        if (!isVisible) {
            notesIterable().forEach((note) -> note.getRenderer().resetAnimations());
        }

        renderables.forEach((renderable) -> {
            if (renderable instanceof AbstractWidget widget)
                widget.active = isVisible;
        });
        visibilityButton.active = true;
    }

    /**
     * @apiNote Prefer overwriting {@link InstrumentScreen#renderInstrument} instead.
     */
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (!instrumentRenders()) {
            visibilityButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            return;
        }

        renderInstrument(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }
    public void renderInstrument(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
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

        // Release a focused note (in case pressed Enter)
        if (isFocused() && (getFocused() instanceof NoteButton btn))
            btn.release();

        // Filter non-instrument keys
        if (!isKeyConsumed(pKeyCode, pScanCode))
            return false;

        unlockFocused();

        final NoteButton note = getNoteByKey(pKeyCode);
        if (note != null)
            note.release();

        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    private boolean pitchChanged;
    protected boolean checkPitchTransposeUp(int pKeyCode, int pScanCode) {
        if (pitchChanged)
            return false;

        if (checkTransposeUpKey(pKeyCode, pScanCode)) {
            transposeUp();
            return true;
        }
        else if (checkTransposeDownKey(pKeyCode, pScanCode)) {
            transposeDown();
            return true;
        }

        return false;
    }
    protected boolean checkTransposeDown(int pKeyCode, int pScanCode) {
        if (!pitchChanged)
            return false;

        if (checkTransposeUpKey(pKeyCode, pScanCode) || checkTransposeDownKey(pKeyCode, pScanCode)) {
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
     * @return Whether this instrument's pitch is being transposed up/down as requested by the keybindings
     */
    public boolean isTransposed() {
        return pitchChanged;
    }


    /**
     * @return {@code true} if the given key is being used by this instrument.
     * Otherwise, {@code false}.
     */
    public boolean isKeyConsumed(final int keyCode, final int scanCode) {
        return (getNoteByKey(keyCode) != null)
            || checkTransposeDownKey(keyCode, scanCode) || checkTransposeUpKey(keyCode, scanCode);
    }

    protected boolean checkTransposeDownKey(final int keyCode, final int scanCode) {
        return InstrumentKeyMappings.TRANSPOSE_DOWN_MODIFIER.get().matches(keyCode, scanCode);
    }
    protected boolean checkTransposeUpKey(final int keyCode, final int scanCode) {
        return InstrumentKeyMappings.TRANSPOSE_UP_MODIFIER.get().matches(keyCode, scanCode);
    }


    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        unlockFocused();
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    public NoteButton getNoteByKey(final int keyCode) {
        final Key key = Type.KEYSYM.getOrCreate(keyCode);
        return noteMap().getOrDefault(key, null);
    }
    /**
     * Unlocks any focused {@link NoteButton}s
     */
    private void unlockFocused() {
        if ((getFocused() != null) && (getFocused() instanceof NoteButton)) {
            ((NoteButton)getFocused()).release();
            setFocused(null);
        }
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


    private boolean closed = false;

    /**
     * @apiNote Please override {@link InstrumentScreen#onClose(boolean)} instead.
     */
    @Override
    public final void onClose() {
        onClose(true);
    }
    public void onClose(final boolean notify) {
        if (!closed) {
            if (notify)
                notifyClosed();

            if (isOptionsScreenActive)
                optionsScreen.onClose();

            closed = true;
        }

        super.onClose();
    }

    @Override
    public void removed() {
        // For when the screen was forcibly replaced
        if (!closed) {
            notifyClosed();

            if (isOptionsScreenActive)
                optionsScreen.saveOptions();

            closed = true;
        }

        super.removed();
    }

    private void notifyClosed() {
        InstrumentOpenProvider.setClosed(minecraft.player);
        GIPacketHandler.sendToServer(new CloseInstrumentPacket());
    }


    /**
     * @return The current instrument screen, if present
     */
    public static Optional<InstrumentScreen> getCurrentScreen(final Minecraft minecraft) {
        if (minecraft.screen instanceof InstrumentScreen)
            return Optional.of((InstrumentScreen)minecraft.screen);

        if (minecraft.screen instanceof AbstractInstrumentOptionsScreen instrumentOptionsScreen)
            return instrumentOptionsScreen.instrumentScreen;

        return Optional.empty();
    }
    public static Optional<InstrumentScreen> getCurrentScreen() {
        return getCurrentScreen(Minecraft.getInstance());
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }
}