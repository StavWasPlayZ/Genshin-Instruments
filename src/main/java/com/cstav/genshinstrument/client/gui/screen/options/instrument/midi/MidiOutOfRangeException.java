package com.cstav.genshinstrument.client.gui.screen.options.instrument.midi;

public class MidiOutOfRangeException extends Exception {
    
    public MidiOutOfRangeException() {
        super("MIDI note is out of allowed range");
    }

}
