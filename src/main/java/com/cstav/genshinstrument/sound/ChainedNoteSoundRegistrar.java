package com.cstav.genshinstrument.sound;

import java.util.ArrayList;
import java.util.function.IntFunction;

public abstract class ChainedNoteSoundRegistrar<T, R extends AbstractNoteSoundRegistrar<T, R>> extends AbstractNoteSoundRegistrar<T, ChainedNoteSoundRegistrar<T, R>> {
    private final R original;

    public ChainedNoteSoundRegistrar(R original) {
        super(original.soundRegistrar, original.baseSoundLocation);
        this.original = original;
    }

    public R add() {
        final ArrayList<T> stackedSounds = original.stackedSounds;

        stackedSounds.add(createNote(stackedSounds.size()));
        return original;
    }

    protected abstract T createNote(final int noteIndex);

    @Override
    public ChainedNoteSoundRegistrar<T, R> getThis() {
        return this;
    }


    @Override
    public T[] register(T[] noteSounds) {
        throw new IllegalStateException("Called register in ChainedNoteRegistrar!");
    }
    @Override
    public T[] registerGrid(int rows, int columns) {
        throw new IllegalStateException("Called registerGrid in ChainedNoteRegistrar!");
    }
    @Override
    protected IntFunction<T[]> noteArrayGenerator() {
        throw new IllegalStateException("Called noteArrayGenerator in ChainedNoteRegistrar!");
    }
}