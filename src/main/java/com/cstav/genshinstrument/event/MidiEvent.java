package com.cstav.genshinstrument.event;

import javax.sound.midi.MidiMessage;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

@OnlyIn(Dist.CLIENT)
public class MidiEvent extends Event {

    public final MidiMessage message;
    public MidiEvent(final MidiMessage message) {
        this.message = message;
    }
    
}
