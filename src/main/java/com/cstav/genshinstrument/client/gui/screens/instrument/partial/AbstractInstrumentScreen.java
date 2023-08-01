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
import com.cstav.genshinstrument.client.gui.screens.options.instrument.AbstractInstrumentOptionsScreen;
import com.cstav.genshinstrument.item.InstrumentItem;
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
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractInstrumentScreen extends Screen {
    public static final String[] DEFAULT_NOTE_LAYOUT = new String[] {"C", "D", "E", "F", "G", "A", "B"};
    
    @SuppressWarnings("resource")
    public int getNoteSize() {
        final int guiScale = Minecraft.getInstance().options.guiScale().get();

        return switch (guiScale) {
            case 0 -> 40;
            case 1 -> 35;
            case 2 -> 46;
            case 3 -> 48;
            case 4 -> 41;
            default -> guiScale * 18;
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


    public abstract ResourceLocation getInstrumentId();
    protected abstract AbstractInstrumentOptionsScreen initInstrumentOptionsScreen();

    /**
     * @return The location of all labels present in this instrument
     */
    public abstract ResourceLocation getNoteSymbolsLocation();


    /**
     * @return The layout of the note names accross the instrument's rows.
     * @implNote All built-in instruments' layouts are derived from
     * <a href=https://github.com/Specy/genshin-music/blob/19dfe0e2fb8081508bd61dd47289dcb2d89ad5e3/src/Config.ts#L114>
     * Specy's Genshin Music app
     * </a>
     */
    public String[] noteWidget() {
        return DEFAULT_NOTE_LAYOUT;
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
    public boolean handleAbruptClosing() {
        final Player player = minecraft.player;

        if (!InstrumentOpenProvider.isOpen(player)) {
            onClose(false);
            return true;
        }

        // Handle item not in hand seperately
        // This is done like so because there is no event (that I know of) for when an item is moved/removed
        if (
            (InstrumentOpenProvider.isItem(player) && interactionHand.isPresent())
            && !(player.getItemInHand(interactionHand.get()).getItem() instanceof InstrumentItem)
        ) {
            onClose(true);
            return true;
        }

        return false;
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
     * Shorthand for {@code getRootPath() + getInstrumentId()}
     */
    protected String getPath() {
        return getGlobalRootPath() + getSourcePath().getPath() + "/";
    }

    /**
     * Override this method if you want to reference another directory for resources
     */
    protected ResourceLocation getSourcePath() {
        return getInstrumentId();
    }

    public String getModId() {
        return getInstrumentId().getNamespace();
    }

    
    /**
     * @param path The desired path to obtain from the root directory
     * @return The resource contained in this instrument's root directory
     * @see {@link AbstractInstrumentScreen#getInstrumentResourcesLocation()}
     * @see {@link AbstractInstrumentScreen#getResourceFrom(ResourceLocation, String)}
     */
    public ResourceLocation getResourceFromRoot(final String path) {
        return getSourcePath().withPath(getPath() + path);
    }


    public final AbstractInstrumentOptionsScreen optionsScreen = initInstrumentOptionsScreen();
    
    public final Optional<InteractionHand> interactionHand;
    public AbstractInstrumentScreen(final InteractionHand hand) {
        super(CommonComponents.EMPTY);

        interactionHand = Optional.ofNullable(hand);
    }


    @Override
    protected void init() {
        initPitch(this::setPitch);
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



    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        final NoteButton note = getNoteByKey(pKeyCode);
        
        if (note != null) {
            note.play();
            return true;
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        unlockFocused(pKeyCode);

        final NoteButton note = getNoteByKey(pKeyCode);
        if (note != null)
            note.locked = false;

        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
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
            getNoteByKey(keyCode).locked = false;
    }


    private boolean isOptionsScreenActive;
    public boolean isOptionsScreenActive() {
        return isOptionsScreenActive;
    }

    public void onOptionsOpen() {
        setFocused(null);
        minecraft.pushGuiLayer(optionsScreen);

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
    public static Optional<AbstractInstrumentScreen> getCurrentScreen(final Minecraft minecraft) {
        if (minecraft.screen instanceof AbstractInstrumentScreen)
            return Optional.of((AbstractInstrumentScreen)minecraft.screen);

        if (minecraft.screen instanceof AbstractInstrumentOptionsScreen) {
            final AbstractInstrumentOptionsScreen instrumentOptionsScreen = (AbstractInstrumentOptionsScreen)minecraft.screen;
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