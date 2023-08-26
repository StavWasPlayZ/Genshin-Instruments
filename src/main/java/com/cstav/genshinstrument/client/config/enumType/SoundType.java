package com.cstav.genshinstrument.client.config.enumType;

import java.util.function.Supplier;

import com.cstav.genshinstrument.sound.NoteSound;

public interface SoundType {
    Supplier<NoteSound[]> getSoundArr();
}
