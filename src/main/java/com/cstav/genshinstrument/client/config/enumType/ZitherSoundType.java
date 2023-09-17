package com.cstav.genshinstrument.client.config.enumType;

import java.util.function.Supplier;

import com.cstav.genshinstrument.sound.ModSounds;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum ZitherSoundType implements SoundType {
    NEW(() -> ModSounds.ZITHER_NEW_NOTE_SOUNDS),
    OLD(() -> ModSounds.ZITHER_OLD_NOTE_SOUNDS);

    private Supplier<NoteSound[]> soundArr;
    private ZitherSoundType(final Supplier<NoteSound[]> soundType) {
        this.soundArr = soundType;
    }

    @Override
    public Supplier<NoteSound[]> getSoundArr() {
        return soundArr;
    }
}