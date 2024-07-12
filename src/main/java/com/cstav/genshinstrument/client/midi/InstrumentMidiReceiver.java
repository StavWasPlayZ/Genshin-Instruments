package com.cstav.genshinstrument.client.midi;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held.IHoldableNoteButton;
import com.cstav.genshinstrument.event.MidiEvent;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.util.BiValue;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class InstrumentMidiReceiver {
    public static final int MIN_MIDI_VELOCITY = 6;

    public final InstrumentScreen instrument;
    public InstrumentMidiReceiver(InstrumentScreen instrument) {
        this.instrument = instrument;
        MidiController.loadByConfigs();
    }

    /**
     * Maps a note message to an instrument pitch to pressed note button.
     */
    private final Map<Byte, BiValue<Integer, NoteButton>> pressedMidiNotes = new HashMap<>();

    /**
     * Fires when a MIDI note is being pressed successfully, only if this is {@link InstrumentScreen#isMidiInstrument a midi instrument}.
     * @param note The raw note being pressed by the MIDI device, {@link InstrumentMidiReceiver#getLowC relative to low C} {@code note % 12}
     * @param key The scale played by the MIDI device; the absolute value of current pitch saved in the client configs (Always set to 0 here)
     * @return The pressed note button. Null if none.
     */
    protected abstract @Nullable NoteButton handleMidiPress(int note, int key);
    

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
        final float sensitivity = ModClientConfigs.MIDI_IN_SENSITIVITY.get().floatValue();
        // 0 sensitivity = fixed touch:
        if (!ModClientConfigs.FIXED_TOUCH.get() && (sensitivity != 0)) {
            double volMultiplier = (message[2] / 127D) / sensitivity;
            instrument.volume = (int)Mth.clamp(instrument.volume * volMultiplier, MIN_MIDI_VELOCITY, 100);
        }


        final NoteButton pressedMidiNote = handleMidiPress(note, pitch);
        if (pressedMidiNote != null) {
            pressedMidiNote.unlockInput();
            pressedMidiNote.play();
            // Remember the note to later release it
            pressedMidiNotes.put(message[1], new BiValue<>(instrument.getPitch(), pressedMidiNote));
        }

        instrument.setVolume(prevVolume);
    }

    protected boolean canPerformMidi(final MidiEvent event) {
        final byte[] message = event.message.getMessage();

        final BiValue<Integer, NoteButton> prevBtnTuple = pressedMidiNotes.get(message[1]);
        NoteButton prevButton = null;
        boolean isHoldableBtn = false;
        if (prevBtnTuple != null) {
            prevButton = prevBtnTuple.obj2();
            isHoldableBtn = prevButton instanceof IHoldableNoteButton;

            // Release the previously pressed note
            if (!isHoldableBtn) {
                prevButton.release();
            }

            pressedMidiNotes.remove(message[1]);
        }


        // Ignore last 4 bits (channel bits)
        final int eventType = (message[0] >> 4) << 4;
        final int midiChannel = message[0] - eventType;

        switch (eventType) {
            case -112: {
                // press
                if (!ModClientConfigs.ACCEPT_ALL_CHANNELS.get())
                    return midiChannel == ModClientConfigs.MIDI_CHANNEL.get();

                return true;
            }

            case -128: {
                // release
                if (isHoldableBtn) {
                    ((IHoldableNoteButton)prevButton).releaseHeld(prevBtnTuple.obj1(), true);
                }

                break;
            }
        }

        return false;
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
    protected abstract int maxMidiNote();


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
