package com.cstav.genshinstrument.client.gui.screens.instrument.drum;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.AbstractNoteLabels;
import com.cstav.genshinstrument.client.keyMaps.KeyMappings;
import com.cstav.genshinstrument.client.keyMaps.KeyMappings.DrumKeys;
import com.cstav.genshinstrument.sounds.ModSounds;
import com.cstav.genshinstrument.sounds.NoteSound;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum DrumButtonType {
    DON(0, ModSounds.GLORIOUS_DRUM[0], "glorious_drum.don", KeyMappings.DON),
    KA(1, ModSounds.GLORIOUS_DRUM[1], "glorious_drum.ka", KeyMappings.KA);

    private final DrumKeys keys;
    private final String transKey;
    private final NoteSound sound;
    private final int index;

    private DrumButtonType(int index, NoteSound sound, String transKey, DrumKeys keys) {
        this.sound = sound;
        this.index = index;
        this.keys = keys;

        this.transKey = AbstractNoteLabels.TRANSLATABLE_PATH + transKey;
    }

    public DrumKeys getKeys() {
        return keys;
    }
    public NoteSound getSound() {
        return sound;
    }
    public int getIndex() {
        return index;
    }
    public String getTransKey() {
        return transKey;
    }
}