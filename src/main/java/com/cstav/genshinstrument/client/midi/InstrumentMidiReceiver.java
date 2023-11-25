package com.cstav.genshinstrument.client.midi;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.event.MidiEvent;
import com.cstav.genshinstrument.sound.NoteSound;
import com.mojang.logging.LogUtils;

public abstract class InstrumentMidiReceiver {
    public static final int MIN_MIDI_VELOCITY = 6;

    public final InstrumentScreen instrument;
    public InstrumentMidiReceiver(InstrumentScreen instrument) {
        this.instrument = instrument;
        loadMidiDevices();
    }

    protected void loadMidiDevices() {
        final int infoIndex = ModClientConfigs.MIDI_DEVICE_INDEX.get();
        if (infoIndex == -1)
            return;


        MidiController.reloadIfEmpty();
        if (infoIndex > (MidiController.DEVICES.size() - 1)) {
            LogUtils.getLogger().warn("MIDI device out of range; setting device to none");
            ModClientConfigs.MIDI_DEVICE_INDEX.set(-1);
            return;
        }

        if (!MidiController.isLoaded(infoIndex)) {
            MidiController.loadDevice(infoIndex);
            MidiController.openForListen();
        }
    }

    
    private NoteButton pressedMidiNote = null;

    /**
     * Fires when a MIDI note is being pressed successfully, only if this is {@link InstrumentScreen#isMidiInstrument a midi instrument}.
     * @param note The raw note being pressed by the MIDI device, {@link InstrumentMidiReceiver#getLowC relative to low C} {@code note % 12}
     * @param key The scale played by the MIDI device; the absolute value of current pitch saved in the client configs (Always set to 0 here)
     * @return The pressed note button. Null if none.
     */
    protected abstract NoteButton handleMidiPress(int note, int key);
    

    public void onMidi(final MidiEvent event) {
        if (!canPerformMidi(event))
            return;

        final byte[] message = event.message.getMessage();


        // So we don't do transpositions on a sharpened scale
        instrument.resetTransposition();

        final int note;
        try {
            note = handleMidiOverflow(getLowC(message[1]));
        } catch (MidiOutOfRangeException e) {
            return;
        }


        //NOTE: Math.abs(getPitch()) was here instead, but transposition seems fair enough
        final int pitch = 0;

        // Handle dynamic touch
        final float prevVolume = instrument.volume();
        if (!ModClientConfigs.FIXED_TOUCH.get())
            instrument.volume *= Math.max(MIN_MIDI_VELOCITY, message[2]) / 127D;


        pressedMidiNote = handleMidiPress(note, pitch);
        if (pressedMidiNote != null)
            pressedMidiNote.play();


        instrument.setVolume(prevVolume);
    }

    protected boolean canPerformMidi(final MidiEvent event) {
        final byte[] message = event.message.getMessage();

        // Release the previously pressed note
        if (pressedMidiNote != null)
            pressedMidiNote.locked = false;

        // We only care for press events:
        
        // Ignore last 4 bits (don't care about the channel atm)
        final int eventType = (message[0] >> 4) << 4;
        if (eventType != -112)
            return false;

        if (!ModClientConfigs.ACCEPT_ALL_CHANNELS.get())
            return (message[0] - eventType) == ModClientConfigs.MIDI_CHANNEL.get();


        return true;
    }


    protected boolean shouldSharpen(final int layoutNote, final int key) {
        final boolean higherThan3 = layoutNote > key + 4;

        // Much testing and maths later
        // The logic here is that accidentals only occur when the note number is
        // the same divisible as the pitch itself
        boolean shouldSharpen = (layoutNote % 2) != (key % 2);
        
        // Negate logic for notes higher than 3 on the scale
        if (higherThan3)
            shouldSharpen = !shouldSharpen;

        // Negate logic for notes beyond the 12th note
        if (layoutNote < key)
            shouldSharpen = !shouldSharpen;

        return shouldSharpen;
    }
    /**
     * Minecraft pitch limitations will want us to go down a pitch instead of up.
     */
    protected boolean shouldFlatten(final boolean shouldSharpen) {
        return shouldSharpen && (instrument.getPitch() == NoteSound.MAX_PITCH);
    }
    
    protected void transposeMidi(final boolean shouldSharpen, final boolean shouldFlatten) {
        if (shouldFlatten)
            instrument.transposeDown();
        else if (shouldSharpen)
            instrument.transposeUp();
    }




    /**
     * Extends the usual limitation of octaves by 2 by adjusting the pitch higher/lower
     * when necessary
     * @param note The current note
     * @return The new shifted (or not) note to handle
     * @throws MidiOutOfRangeException If the pressed note exceeds the allowed MIDI range (overflows)
     */
    protected int handleMidiOverflow(int note) throws MidiOutOfRangeException {
        if (!allowMidiOverflow() || !ModClientConfigs.EXTEND_OCTAVES.get()) {
            if ((note < minMidiNote()) || (note >= maxMidiNote()))
                throw new MidiOutOfRangeException();

            return note;
        }


        final int minPitch = NoteSound.MIN_PITCH, maxPitch = NoteSound.MAX_PITCH;

        // Set the pitch
        if (note < minMidiNote()) {
            if (note < minMidiOverflow())
                throw new MidiOutOfRangeException();

            if (instrument.getPitch() != minPitch)
                overflowMidi(minPitch);
                
        } else if (note >= maxMidiNote()) {
            if (note >= maxMidiOverflow())
                throw new MidiOutOfRangeException();

            if (instrument.getPitch() != maxPitch)
                overflowMidi(maxPitch);
        }

        // Check if we are an octave above/below
        // and reset back to pitch C
        if (instrument.getPitch() == minPitch) {
            if (note >= minMidiNote())
                instrument.setPitch(0);
            // Shift the note to the higher octave
            else
                note += 12;
        }
        else if (instrument.getPitch() == maxPitch) {
            if (note < maxMidiNote())
                instrument.setPitch(0);
            else
                note -= 12;
        }

        return note;
    }

    private void overflowMidi(final int desiredPitch) {
        instrument.setPitch(desiredPitch);
        // Reset pitch to C to avoid coming back down for a mess
        if (!ModClientConfigs.PITCH.get().equals(0))
            ModClientConfigs.PITCH.set(0);
    }


    protected int minMidiNote() {
        return 0;
    }
    protected int maxMidiNote() {
        return NoteSound.MAX_PITCH * 3;
    }


    /**
     * Extends the usual limitation of octaves by 2 by adjusting the pitch higher/lower
     * when necessary
     * @see InstrumentMidiReceiver#handleMidiOverflow
     */
    public boolean allowMidiOverflow() {
        return false;
    }

    protected int maxMidiOverflow() {
        return maxMidiNote() + 12;
    }
    protected int minMidiOverflow() {
        return minMidiNote() - 12;
    }


    /**
     * @return The MIDI note adjusted by -48, as well as the preferred shift accounted.
     * Assumes middle C is 60 as per MIDI specifications.
     */
    protected int getLowC(final int note) {
        return note - ModClientConfigs.OCTAVE_SHIFT.get() * 12 - 48;
    }

}
