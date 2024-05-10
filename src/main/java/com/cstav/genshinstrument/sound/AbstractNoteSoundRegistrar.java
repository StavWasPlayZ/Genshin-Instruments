package com.cstav.genshinstrument.sound;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.GridInstrumentScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.IntFunction;

public abstract class AbstractNoteSoundRegistrar<T, R extends AbstractNoteSoundRegistrar<T, R>> {
    public static final String STEREO_SUFFIX = "_stereo";


    /* ----------- Registration Builder ----------- */

    protected final DeferredRegister<SoundEvent> soundRegistrar;
    protected final ResourceLocation baseSoundLocation;

    protected boolean hasStereo = false;
    protected boolean alreadyRegistered = false;

    public AbstractNoteSoundRegistrar(DeferredRegister<SoundEvent> soundRegistrar, ResourceLocation baseSoundLocation) {
        this.soundRegistrar = soundRegistrar;
        this.baseSoundLocation = baseSoundLocation;
    }

    public abstract R getThis();


    /**
     * Defines that this note sound will support stereo.
     * Stereo sounds are suffixed with {@code "_stereo"}.
     */
    public R stereo() {
        hasStereo = true;
        return getThis();
    }
    /**
     * Skips the process of registering this note's SoundEvents with Minecraft.
     * For use with already registered sounds.
     */
    public R alreadyRegistered() {
        alreadyRegistered = true;
        return getThis();
    }


    /* ----------- Registration Methods ----------- */

    public abstract T[] register(final T[] noteSounds);

    // Grid registrar
    /**
     * Registers a matrix of sounds for a grid instrument.
     */
    public abstract T[] registerGrid(final int rows, final int columns);

    /**
     * Registers a matrix of sounds for a grid instrument, with the
     * default amount of {@link GridInstrumentScreen#DEF_ROWS rows} and {@link GridInstrumentScreen#DEF_COLUMNS columns}.
     */
    public T[] registerGrid() {
        return registerGrid(GridInstrumentScreen.DEF_ROWS, GridInstrumentScreen.DEF_COLUMNS);
    }


    //#region Singles registrar

    protected final ArrayList<T> stackedSounds = new ArrayList<>();

    /**
     * <p>Chains a note sound to this registrar.</p>
     * <p>Call back {@link ChainedNoteSoundRegistrar#add()}
     * to perform the chain and return here.</p>
     *
     * <p>Call {@link NoteSoundRegistrar#registerAll()} after all registrations
     * are complete.</p>
     */
    public ChainedNoteSoundRegistrar<T, R> chain(ResourceLocation soundLocation) {
        validateNotChained();
        return new ChainedNoteSoundRegistrar<>(getThis(), soundLocation);
    }

    /**
     * @return The head of the stacked sounds
     */
    public T peek() {
        validateNotChained();
        return stackedSounds.get(stackedSounds.size() - 1);
    }

    /**
     * Registers all NoteSounds added via chained {@link ChainedNoteSoundRegistrar#add}
     */
    public T[] registerAll() {
        validateNotChained();
        return register(stackedSounds.toArray(noteArrayGenerator()));
    }
    protected abstract IntFunction<T[]> noteArrayGenerator();

    private void validateNotChained() {
        if (this instanceof ChainedNoteSoundRegistrar)
            throw new IllegalStateException("Called non-chainable method on a chained registrar!");
    }

    //#endregion


    // Single register
    /**
     * Creates a singular {@link T} with null sounds, that will get filled
     * upon registration.
     */
    public T registerNote() {
        return createNote(baseSoundLocation, 0);
    }


    /**
     * Creates a singular {@link NoteSound} with null sounds, that will get filled
     * upon registration.
     */
    protected abstract T createNote(ResourceLocation soundLocation, int index);

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
     * Creates and registers a {@link NoteSound} with null sounds, that will get filled
     * upon registration.
     * The name of the registered sound entry will be suffixed by "_note{@code noteIndex}".
     * @param noteIndex The index of the note
     */
    public T createNote(int noteIndex) {
        return createNote(baseSoundLocation.withSuffix("_note_"+noteIndex), noteIndex);
    }

}
