package com.cstav.genshinstrument.sound.registrar;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.GridInstrumentScreen;
import com.cstav.genshinstrument.util.CommonUtil;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid.GridInstrumentScreen;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.registrar.impl.ChainableNoteSoundRegistrar;
import com.cstav.genshinstrument.sound.registrar.impl.ChainedNoteSoundRegistrar;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;

public class NoteSoundRegistrar extends ChainableNoteSoundRegistrar<NoteSound, NoteSoundRegistrar> {
    public static final String STEREO_SUFFIX = "_stereo";

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final HashMap<ResourceLocation, NoteSound[]> SOUNDS_REGISTRY = new HashMap<>();
    public static NoteSound[] getSounds(final ResourceLocation baseSoundName) {
        return SOUNDS_REGISTRY.get(baseSoundName);
    }


    public NoteSoundRegistrar(DeferredRegister<SoundEvent> soundRegistrar, ResourceLocation baseSoundLocation) {
        super(soundRegistrar, baseSoundLocation);
    }

    @Override
    public NoteSoundRegistrar getThis() {
        return this;
    }


    /**
     * Skips the process of registering this note's SoundEvents with Minecraft.
     * For use with already registered sounds.
     */
    public NoteSoundRegistrar alreadyRegistered() {
        paramsMap.put("ALREADY_REGISTERED", true);
        return getThis();
    }
    /**
     * Defines that this note sound will support stereo.
     * Stereo sounds are suffixed with {@code "_stereo"}.
     */
    public NoteSoundRegistrar stereo() {
        paramsMap.put("HAS_STEREO", true);
        return getThis();
    }


    @Override
    public NoteSound[] register(final NoteSound[] noteSounds) {
        SOUNDS_REGISTRY.put(baseSoundLocation, noteSounds);

        LOGGER.info("Successfully registered "+noteSounds.length+" note sounds of "+baseSoundLocation);
        return noteSounds;
    }

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

    protected NoteSound createNote(ResourceLocation soundLocation, int index, Map<String, Object> paramMap) {
        final NoteSound sound = new NoteSound(index, baseSoundLocation);

        setSoundField((soundEvent) -> sound.mono = soundEvent, soundLocation);
        if (getBool(paramMap, "HAS_STEREO")) {
            setSoundField((soundEvent) -> sound.stereo = soundEvent, soundLocation.withSuffix(STEREO_SUFFIX));
        }

        return sound;
    }
    protected NoteSound createNote(ResourceLocation soundLocation, int index) {
        return createNote(soundLocation, index, paramsMap);
    }
    /**
     * Registers a sound event to the {@link NoteSoundRegistrar#soundRegistrar} if necessary,
     * and passes it to the consumer upon its registration.
     */
    protected void setSoundField(Function<SoundEvent, SoundEvent> fieldConsumer, ResourceLocation soundLocation) {
        if (getBool(paramsMap, "ALREADY_REGISTERED")) {
            fieldConsumer.apply(ForgeRegistries.SOUND_EVENTS.getValue(soundLocation));
        } else {
            soundRegistrar.register(soundLocation.getPath(), () ->
                fieldConsumer.apply(SoundEvent.createVariableRangeEvent(soundLocation))
            );
        }
    }


    /**
     * <p>Chains a note sound to this registrar.</p>
     * <p>Call back {@link ChainedNoteSoundRegistrar#add()}
     * to perform the chain and return here.</p>
     *
     * <p>Call {@link NoteSoundRegistrar#registerAll()} after all registrations
     * are complete.</p>
     */
    public Chained chain(ResourceLocation soundLocation) {
        return new Chained(soundLocation);
    }
    public final class Chained extends ChainedNoteSoundRegistrar<NoteSound, NoteSoundRegistrar> {
        private final ResourceLocation soundLocation;
        public Chained(ResourceLocation soundLocation) {
            super(NoteSoundRegistrar.this.getThis());
            this.soundLocation = soundLocation;
        }


        @Override
        protected NoteSound createNote() {
            return NoteSoundRegistrar.this.createNote(soundLocation, stackedSounds.size(), paramsMap);
        }

        /**
         * Skips the process of registering this note's SoundEvents with Minecraft.
         * For use with already registered sounds.
         */
        public ChainedNoteSoundRegistrar<NoteSound, NoteSoundRegistrar> alreadyRegistered() {
            paramsMap.put("ALREADY_REGISTERED", true);
            return getThis();
        }
        /**
         * Defines that this note sound will support stereo.
         * Stereo sounds are suffixed with {@code "_stereo"}.
         */
        public ChainedNoteSoundRegistrar<NoteSound, NoteSoundRegistrar> stereo() {
            paramsMap.put("HAS_STEREO", true);
            return getThis();
        }
    }


    @Override
    protected IntFunction<NoteSound[]> noteArrayGenerator() {
        return NoteSound[]::new;
    }


    // Single register
    /**
     * Creates a singular {@link NoteSound} with null sounds, that will get filled
     * upon registration.
     */
    public NoteSound registerNote() {
        return createNote(baseSoundLocation, 0);
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
