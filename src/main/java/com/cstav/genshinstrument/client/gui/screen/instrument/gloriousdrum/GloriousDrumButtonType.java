package com.cstav.genshinstrument.client.gui.screen.instrument.gloriousdrum;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.label.INoteLabel;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings.GloriousDrumKeys;
import com.cstav.genshinstrument.sound.GISounds;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

// This class is also used by the server as an identifier for the drum
public enum GloriousDrumButtonType {
    DON(GISounds.GLORIOUS_DRUM[0], "glorious_drum.don"),
    KA(GISounds.GLORIOUS_DRUM[1], "glorious_drum.ka");

    private final String transKey;
    private final NoteSound sound;

    GloriousDrumButtonType(NoteSound sound, String transKey) {
        this.sound = sound;
        this.transKey = INoteLabel.TRANSLATABLE_PATH + transKey;
    }

    public NoteSound getSound() {
        return sound;
    }
    public String getTransKey() {
        return transKey;
    }


    // Seperated for server compatibility
    @OnlyIn(Dist.CLIENT)
    public GloriousDrumKeys getKeys() {
        return (this == DON) ? InstrumentKeyMappings.DON : InstrumentKeyMappings.KA;
    }

}