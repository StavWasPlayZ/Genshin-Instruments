package com.cstav.genshinstrument.event;

import javax.sound.midi.MidiMessage;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.google.common.base.Function;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

@OnlyIn(Dist.CLIENT)
public class MidiEvent extends Event {

    public final MidiMessage message;
    public final long timeStamp;
    /**
     * Consumes the MIDI note about to be pressed
     * @return Wether the note should be pressed
     */
    public final Function<NoteButton, Boolean> noteConsumer;

    public MidiEvent(MidiMessage message, long timeStamp, Function<NoteButton, Boolean> noteConsumer) {
        this.message = message;
        this.timeStamp = timeStamp;
        this.noteConsumer = noteConsumer;
    }
    public MidiEvent(MidiMessage message, long timeStamp) {
        this(message, timeStamp, null);
    }


    public boolean shouldPLay(final NoteButton note) {
        return (noteConsumer != null) && noteConsumer.apply(note);
    }
    
}
