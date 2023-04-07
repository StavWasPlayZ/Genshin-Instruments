package com.cstav.genshinstrument.client.gui.screens.options.instrument;

import java.util.function.Supplier;

import com.cstav.genshinstrument.sounds.ModSounds;

import net.minecraft.sounds.SoundEvent;

public enum ZitherSoundType {
    OLD(() -> ModSounds.getSoundsFromArr(ModSounds.ZITHER_OLD_NOTE_SOUNDS)),
    NEW(() -> ModSounds.getSoundsFromArr(ModSounds.ZITHER_NEW_NOTE_SOUNDS));

    private Supplier<SoundEvent[]> soundArr;
    private ZitherSoundType(final Supplier<SoundEvent[]> soundType) {
        this.soundArr = soundType;
    }

    public Supplier<SoundEvent[]> soundArr() {
        return soundArr;
    }
}