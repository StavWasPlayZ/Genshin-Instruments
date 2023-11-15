package com.cstav.genshinstrument.client.gui.screen.instrument.partial;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.GenshinConsentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.AbstractInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.BaseInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings;
import com.cstav.genshinstrument.client.midi.InstrumentMidiReceiver;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.instrument.CloseInstrumentPacket;
import com.cstav.genshinstrument.sound.NoteSound;
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

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public abstract class InstrumentScreen extends Screen {
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


    public abstract InstrumentThemeLoader getThemeLoader();
    public abstract ResourceLocation getInstrumentId();
    
    protected abstract BaseInstrumentOptionsScreen initInstrumentOptionsScreen();


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
     * @return The resource laid inside of this instrument's directory
     */
    public ResourceLocation getResourceFromGlob(final String path) {
        return CommonUtil.withPath(getSourcePath(), getGlobalRootPath() + "instrument/" + path);
    }
    public static ResourceLocation getInternalResourceFromGlob(final String path) {
        return new ResourceLocation(GInstrumentMod.MODID, getGlobalRootPath() + path);
    }

    public static ResourceLocation getInstrumentRootPath(final ResourceLocation instrumentId) {
        return CommonUtil.withPath(instrumentId, InstrumentScreen.getGlobalRootPath() + "instrument/" + instrumentId.getPath());
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
        return getInstrumentId();
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
            ? CommonUtil.withSuffix(InstrumentThemeLoader.GLOBAL_LOC, "/"+path)
            : CommonUtil.withPath(getSourcePath(), getPath() + path);
    }
    /**
     * Gets The desired path to obtain from either the instrument's root or global directory.
     * The global directory will be used if {@link InstrumentThemeLoader#isGlobalThemed()} is true.
     * @return The resource contained in this instrument's root directory
     */
    public ResourceLocation getResourceFromRoot(final String path) {
        return getResourceFromRoot(path, true);
    }


    public final BaseInstrumentOptionsScreen optionsScreen = initInstrumentOptionsScreen();
    
    public final Optional<InteractionHand> interactionHand;
    public InstrumentScreen(final InteractionHand hand) {
        super(CommonComponents.EMPTY);

        interactionHand = Optional.ofNullable(hand);
        midiReceiver = initMidiReceiver();
    }


    @Override
    protected void init() {
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
    public static Optional<InstrumentScreen> getCurrentScreen(final Minecraft minecraft) {
        if (minecraft.screen instanceof InstrumentScreen)
            return Optional.of((InstrumentScreen)minecraft.screen);

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
}