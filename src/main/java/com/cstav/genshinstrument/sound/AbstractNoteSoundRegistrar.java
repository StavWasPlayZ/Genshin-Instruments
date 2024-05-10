package com.cstav.genshinstrument.sound;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.GridInstrumentScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.function.IntFunction;

public abstract class AbstractNoteSoundRegistrar<T, R extends AbstractNoteSoundRegistrar<T, R>> {
    public static final String STEREO_SUFFIX = "_stereo";


    /* ----------- Registration Builder ----------- */

    protected final DeferredRegister<SoundEvent> soundRegistrar;
    protected final ResourceLocation baseSoundLocation;

    protected boolean hasStereo = false;

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

    protected void validateNotChained() {
        if (this instanceof ChainedNoteSoundRegistrar)
            throw new IllegalStateException("Called non-chainable method on a chained registrar!");
    }

    //#endregion

}
