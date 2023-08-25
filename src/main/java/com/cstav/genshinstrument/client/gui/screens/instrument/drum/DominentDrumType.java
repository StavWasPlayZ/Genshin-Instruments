package com.cstav.genshinstrument.client.gui.screens.instrument.drum;

import com.cstav.genshinstrument.client.midi.DrumMidiOptionsScreen;

public enum DominentDrumType {
    DON, KA, BOTH;

    public String getKey() {
        return (this == BOTH)
            ? (DrumMidiOptionsScreen.DDT_KEY+".both")
            : ((this == KA) ? DrumButtonType.KA : DrumButtonType.DON).getTransKey();
    }
}
