package com.cstav.genshinstrument.client.gui.screens.instrument.drum;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.INoteLabel;
import com.cstav.genshinstrument.client.keyMaps.KeyMappings;
import com.cstav.genshinstrument.client.keyMaps.KeyMappings.DrumKeys;
import com.cstav.genshinstrument.sound.ModSounds;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum DrumButtonType {
    DON(0, ModSounds.GLORIOUS_DRUM[0], "glorious_drum.don"),
    KA(1, ModSounds.GLORIOUS_DRUM[1], "glorious_drum.ka");

    private final String transKey;
    private final NoteSound sound;
    private final int index;

    private DrumButtonType(int index, NoteSound sound, String transKey) {
        this.sound = sound;
        this.index = index;

        this.transKey = INoteLabel.TRANSLATABLE_PATH + transKey;
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


    // Seperated for server compatibility
    @OnlyIn(Dist.CLIENT)
    public DrumKeys getKeys() {
        return (this == DON) ? KeyMappings.DON : KeyMappings.KA;
    }

}