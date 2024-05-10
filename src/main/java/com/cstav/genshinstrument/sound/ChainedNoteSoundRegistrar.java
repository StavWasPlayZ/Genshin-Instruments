package com.cstav.genshinstrument.sound;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.function.IntFunction;

public class ChainedNoteSoundRegistrar<T, R extends AbstractNoteSoundRegistrar<T, R>> extends AbstractNoteSoundRegistrar<T, ChainedNoteSoundRegistrar<T, R>> {

    private final ResourceLocation soundLocation;
    private final R original;

    public ChainedNoteSoundRegistrar(R original, ResourceLocation soundLocation) {
        super(original.soundRegistrar, original.baseSoundLocation);
        this.soundLocation = soundLocation;
        this.original = original;
    }

    public R add() {
        final ArrayList<T> stackedSounds = original.stackedSounds;

        stackedSounds.add(original.createNote(soundLocation, stackedSounds.size()));
        return original;
    }

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
    protected T createNote(ResourceLocation soundLocation, int index) {
        throw new IllegalStateException("Called createNote in ChainedNoteRegistrar!");
    }
    @Override
    protected IntFunction<T[]> noteArrayGenerator() {
        throw new IllegalStateException("Called noteArrayGenerator in ChainedNoteRegistrar!");
    }
}