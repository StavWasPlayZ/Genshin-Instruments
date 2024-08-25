package com.cstav.genshinstrument.client.config.enumType;

import com.cstav.genshinstrument.sound.NoteSound;

import java.util.Locale;
import java.util.function.Supplier;

public interface SoundType {
    Supplier<NoteSound[]> getSoundArr();

    /**
     * @return The name of this sound type
     * as in the translation files
     */
    default String getName() {
        return toString().toLowerCase(Locale.ENGLISH);
    }
}
