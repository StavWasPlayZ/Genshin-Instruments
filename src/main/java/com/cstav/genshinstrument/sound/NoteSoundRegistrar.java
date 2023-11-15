package com.cstav.genshinstrument.sound;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.GridInstrumentScreen;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class NoteSoundRegistrar {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final HashMap<ResourceLocation, NoteSound[]> SOUNDS_REGISTRY = new HashMap<>();
    public static final String STEREO_SUFFIX = "_stereo";

    public static NoteSound[] getSounds(final ResourceLocation baseSoundName) {
        return SOUNDS_REGISTRY.get(baseSoundName);
    }


    /* ----------- Registration Builder ----------- */

    private final DeferredRegister<SoundEvent> soundRegistrar;
    private final ResourceLocation baseSoundLocation;

    private boolean hasStereo = false;
    private boolean alreadyRegistered = false;

    public NoteSoundRegistrar(DeferredRegister<SoundEvent> soundRegistrar, ResourceLocation baseSoundLocation) {
        this.soundRegistrar = soundRegistrar;
        this.baseSoundLocation = baseSoundLocation;
    }

    /**
     * Defines that this note sound will support stereo.
     * Stereo sounds are suffixed with {@code "_stereo"}.
     */
    public NoteSoundRegistrar stereo() {
        hasStereo = true;
        return this;
    }
    /**
     * Skips the process of registering this note's SoundEvents with Minecraft.
     * For use with already registered sounds.
     */
    public NoteSoundRegistrar alreadyRegistered() {
        alreadyRegistered = true;
        return this;
    }


    public NoteSound[] register(final NoteSound[] noteSounds) {
        SOUNDS_REGISTRY.put(baseSoundLocation, noteSounds);

        LOGGER.info("Successfully registered "+noteSounds.length+" note sounds of "+baseSoundLocation);
        return noteSounds;
    }


    /* ----------- Registration Methods ----------- */

    // Grid registrar
    /**
     * Registers a matrix of sounds for a grid instrument.
     */
    public NoteSound[] registerGrid(final int rows, final int columns) {
        final NoteSound[] sounds = new NoteSound[rows * columns];

        for (int i = 0; i < sounds.length; i++)
            sounds[i] = createNote(i);

        return register(sounds);
    }

    /**
     * Registers a matrix of sounds for a grid instrument, with the
     * default amount of {@link GridInstrumentScreen#DEF_ROWS rows} and {@link GridInstrumentScreen#DEF_COLUMNS columns}.
     */
    public NoteSound[] registerGrid() {
        return registerGrid(GridInstrumentScreen.DEF_ROWS, GridInstrumentScreen.DEF_COLUMNS);
    }


    //#region Singles registrar

    private final ArrayList<NoteSound> stackedSounds = new ArrayList<>();

    /**
     * <p>Chains a note sound to this registrar.</p>
     * <p>Call back {@link ChainedNoteSoundRegistrar#add()}
     * to perform the chain and return here.</p>
     *
     * <p>Call {@link NoteSoundRegistrar#registerAll()} after all registrations
     * are complete.</p>
     */
    public ChainedNoteSoundRegistrar chain(ResourceLocation soundLocation) {
        validateNotChained();
        return new ChainedNoteSoundRegistrar(soundLocation);
    }

    public NoteSound peek() {
        validateNotChained();
        return stackedSounds.get(stackedSounds.size() - 1);
    }

    /**
     * Registers all NoteSounds added via {@link NoteSoundRegistrar#add}
     */
    public NoteSound[] registerAll() {
        validateNotChained();
        return register(stackedSounds.toArray(NoteSound[]::new));
    }

    public NoteSoundRegistrar add() {
        throw new IllegalStateException("Called add() on a non-chained registrar!");
    }


    public class ChainedNoteSoundRegistrar extends NoteSoundRegistrar {

        private final ResourceLocation soundLocation;
        private ChainedNoteSoundRegistrar(ResourceLocation soundLocation) {
            super(NoteSoundRegistrar.this.soundRegistrar, NoteSoundRegistrar.this.baseSoundLocation);
            this.soundLocation = soundLocation;
        }

        @Override
        public NoteSoundRegistrar add() {
            final NoteSoundRegistrar original = NoteSoundRegistrar.this;
            final ArrayList<NoteSound> stackedSounds = original.stackedSounds;

            stackedSounds.add(createNote(soundLocation, stackedSounds.size()));
            return original;
        }

    }
    private void validateNotChained() {
        if (this instanceof ChainedNoteSoundRegistrar)
            throw new IllegalStateException("Called non-chainable method on a chained registrar!");
    }

    //#endregion


    // Single register
    /**
     * Creates a singular {@link NoteSound} with null sounds, that will get filled
     * upon registration.
     */
    public NoteSound registerNote() {
        return createNote(baseSoundLocation, 0);
    }


    /**
     * Creates a singular {@link NoteSound} with null sounds, that will get filled
     * upon registration.
     */
    protected NoteSound createNote(ResourceLocation soundLocation, int index) {
        final NoteSound sound = new NoteSound(index, baseSoundLocation);

        setSoundField((soundEvent) -> sound.mono = soundEvent, soundLocation);
        if (hasStereo) {
            setSoundField((soundEvent) -> sound.stereo = soundEvent, CommonUtil.withSuffix(soundLocation, STEREO_SUFFIX));
        }

        return sound;
    }
    private void setSoundField(Function<SoundEvent, SoundEvent> fieldConsumer, ResourceLocation soundLocation) {
        if (alreadyRegistered) {
            fieldConsumer.apply(ForgeRegistries.SOUND_EVENTS.getValue(soundLocation));
        } else {
            soundRegistrar.register(soundLocation.getPath(), () ->
                fieldConsumer.apply(new SoundEvent(soundLocation))
            );
        }
    }

    /**
     * Creates and registers a {@link NoteSound} with null sounds, that will get filled
     * upon registration.
     * The name of the registered sound entry will be suffixed by "_note{@code noteIndex}".
     * @param noteIndex The index of the note
     */
    public NoteSound createNote(int noteIndex) {
        return createNote(CommonUtil.withSuffix(baseSoundLocation, "_note_"+noteIndex), noteIndex);
    }

}
