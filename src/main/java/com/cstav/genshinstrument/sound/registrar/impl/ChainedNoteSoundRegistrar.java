package com.cstav.genshinstrument.sound.registrar.impl;

import java.util.ArrayList;

public abstract class ChainedNoteSoundRegistrar<T, R extends ChainableNoteSoundRegistrar<T, R>> extends AbstractNoteSoundRegistrar<T, ChainedNoteSoundRegistrar<T, R>> {
    private final R original;

    public ChainedNoteSoundRegistrar(R original) {
        super(original.soundRegistrar, original.baseSoundLocation);
        this.original = original;
    }

    public R add(int times) {
        final ArrayList<T> stackedSounds = original.stackedSounds;

        final T sound = createNote();
        for (int i = 0; i < times; i++) {
            stackedSounds.add(sound);
        }

        return original;
    }
    public R add() {
        return add(1);
    }

    protected abstract T createNote();

    @Override
    public ChainedNoteSoundRegistrar<T, R> getThis() {
        return this;
    }
}