package com.cstav.genshinstrument.client.config.enumType;

import com.cstav.genshinstrument.sound.GISounds;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public enum ZitherSoundType implements SoundType {
    OLD(() -> GISounds.ZITHER_OLD_NOTE_SOUNDS),
    NEW(() -> GISounds.ZITHER_NEW_NOTE_SOUNDS);

    private Supplier<NoteSound[]> soundArr;
    private ZitherSoundType(final Supplier<NoteSound[]> soundType) {
        this.soundArr = soundType;
    }

    @Override
    public Supplier<NoteSound[]> getSoundArr() {
        return soundArr;
    }
}