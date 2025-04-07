package com.cstav.genshinstrument.client.gui.screen.instrument.ukelele;

import com.cstav.genshinstrument.client.gui.screen.options.instrument.GridInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.MidiOptionsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UkuleleOptionsScreen extends GridInstrumentOptionsScreen {

    public UkuleleOptionsScreen(final UkuleleScreen screen) {
        super(screen);
    }

    @Override
    protected MidiOptionsScreen midiOptionsScreen() {
        return new UkuleleMidiOptionsScreen(MIDI_OPTIONS, this, instrumentScreen);
    }

}