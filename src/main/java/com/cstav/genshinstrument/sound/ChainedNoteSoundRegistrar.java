package com.cstav.genshinstrument.sound;

import java.util.ArrayList;

public abstract class ChainedNoteSoundRegistrar<T, R extends ChainableNoteSoundRegistrar<T, R>> extends AbstractNoteSoundRegistrar<T, ChainedNoteSoundRegistrar<T, R>> {
    private final R original;

    public ChainedNoteSoundRegistrar(R original) {
        super(original.soundRegistrar, original.baseSoundLocation);
        this.original = original;
    }

    public R add() {
        final ArrayList<T> stackedSounds = original.stackedSounds;

        stackedSounds.add(createNote());
        return original;
    }

    protected abstract T createNote();

    @Override
    public ChainedNoteSoundRegistrar<T, R> getThis() {
        return this;
    }
}