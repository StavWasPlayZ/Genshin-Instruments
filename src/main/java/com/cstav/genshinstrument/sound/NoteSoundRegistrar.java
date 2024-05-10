package com.cstav.genshinstrument.sound;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.IntFunction;

public class NoteSoundRegistrar extends AbstractNoteSoundRegistrar<NoteSound, NoteSoundRegistrar> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final HashMap<ResourceLocation, NoteSound[]> SOUNDS_REGISTRY = new HashMap<>();
    public static NoteSound[] getSounds(final ResourceLocation baseSoundName) {
        return SOUNDS_REGISTRY.get(baseSoundName);
    }

    protected boolean alreadyRegistered = false;

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
        alreadyRegistered = true;
        return getThis();
    }


    @Override
    public NoteSound[] register(final NoteSound[] noteSounds) {
        SOUNDS_REGISTRY.put(baseSoundLocation, noteSounds);

        LOGGER.info("Successfully registered "+noteSounds.length+" note sounds of "+baseSoundLocation);
        return noteSounds;
    }

    @Override
    public NoteSound[] registerGrid(final int rows, final int columns) {
        final NoteSound[] sounds = new NoteSound[rows * columns];

        for (int i = 0; i < sounds.length; i++)
            sounds[i] = createNote(i);

        return register(sounds);
    }

    protected NoteSound createNote(ResourceLocation soundLocation, int index) {
        final NoteSound sound = new NoteSound(index, baseSoundLocation);

        setSoundField((soundEvent) -> sound.mono = soundEvent, soundLocation);
        if (hasStereo) {
            setSoundField((soundEvent) -> sound.stereo = soundEvent, soundLocation.withSuffix(STEREO_SUFFIX));
        }

        return sound;
    }
    /**
     * Registers a sound event to the {@link AbstractNoteSoundRegistrar#soundRegistrar} if necessary,
     * and passes it to the consumer upon its registration.
     */
    protected void setSoundField(Function<SoundEvent, SoundEvent> fieldConsumer, ResourceLocation soundLocation) {
        if (alreadyRegistered) {
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
    public ChainedNoteSoundRegistrar<NoteSound, NoteSoundRegistrar> chain(ResourceLocation soundLocation) {
        return new ChainedNoteSoundRegistrar<>(getThis()) {
            @Override
            protected NoteSound createNote(int noteIndex) {
                return NoteSoundRegistrar.this.createNote(soundLocation, noteIndex);
            }
        };
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
        return createNote(baseSoundLocation.withSuffix("_note_"+noteIndex), noteIndex);
    }
}
