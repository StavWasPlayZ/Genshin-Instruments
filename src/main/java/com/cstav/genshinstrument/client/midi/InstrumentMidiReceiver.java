package com.cstav.genshinstrument.client.midi;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held.IHoldableNoteButton;
import com.cstav.genshinstrument.client.midi.MidiOverflowResult.OverflowType;
import com.cstav.genshinstrument.event.MidiEvent;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.NotImplementedException;
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

    private final Map<Byte, PressedMIDINote> pressedMidiNotes = new HashMap<>();

    /**
     * Fires when a MIDI note is being pressed successfully, only if this is {@link InstrumentScreen#isMidiInstrument a midi instrument}.
     * @param note The raw note being pressed by the MIDI device, {@link InstrumentMidiReceiver#getLowC relative to low C} {@code note % 12}
     * @param key The scale played by the MIDI device; the absolute value of current pitch saved in the client configs (Always set to 0 here)
     * @return The pressed note button. Null if none.
     */
    protected abstract @Nullable NoteButton handleMidiPress(int note, int key);

    /**
     * @return The lowest-pitched note button of this instrument.
     * Must be overwritten for an overflowable instrument.
     */
    protected NoteButton getLowestNote() {
        throw new NotImplementedException("Overflow instrument does not implement getLowestNote");
    }
    /**
     * @return The highest-pitched note button of this instrument.
     * Must be overwritten for an overflow-able instrument.
     */
    protected NoteButton getHighestNote() {
        throw new NotImplementedException("Overflow instrument does not implement getHighestNote");
    }
    

    public void onMidi(final MidiEvent event) {
        if (!canPerformMidi(event))
            return;

        final byte[] message = event.message.getMessage();

        final byte midiNote = message[1];
        int note = getLowC(midiNote);

        if (isMIDIOutOfBounds(note))
            return;


        final float prevVolume = instrument.volume();
        handleDynamicTouch(message[2]);

        // So we don't do transpositions on a sharpened scale
        instrument.resetTransposition();

        //NOTE: Math.abs(getPitch()) was here instead, but transposition seems fair enough
        final int pitch = 0;


        // Handle MIDI overflow
        final MidiOverflowResult overflowRes = handleMidiOverflow(note);

        if (overflowRes != null) {
            note = overflowRes.fixedOctaveNote();

            // Handle overflowing from Minecraft pitch limitations
            int newInsPitch = overflowRes.pitchOffset() + instrument.getPitch();
            if ((newInsPitch < NoteSound.MIN_PITCH) || (newInsPitch > NoteSound.MIN_PITCH)) {
                // Reset their pitch to middle C.
                if (!ModClientConfigs.PITCH.get().equals(0))
                    ModClientConfigs.PITCH.set(0);
                instrument.setPitch(0);
            }
        }


        // Actually play the note
        int basePitch = instrument.getPitch();
        final NoteButton pressedNote = handleMidiPress(note, pitch);

        if (pressedNote != null) {
            pressedNote.unlockInput();
            PressedMIDINote pressedMidiNote = playNote(pressedNote, overflowRes, basePitch);
            if (pressedMidiNote != null) {
                // Remember the note to later release it
                pressedMidiNotes.put(midiNote, pressedMidiNote);
            }
        }

        instrument.setVolume(prevVolume);
    }

    /**
     * Plays the note button, accounting for the provided overflow.
     * @param noteBtn The note button to play
     * @param midiOverflow The MIDI overflow context
     * @param basePitch The pitch of the instrument, before any transformations
     * @return A pressed MIDI note result if succeeded; null otherwise
     */
    protected PressedMIDINote playNote(NoteButton noteBtn, @Nullable MidiOverflowResult midiOverflow, int basePitch) {
        if (midiOverflow == null) {
            noteBtn.play();
            return new PressedMIDINote(noteBtn.getPitch(), noteBtn, noteBtn.getSound());
        } else {
            int newPitch = basePitch + midiOverflow.pitchOffset();
            noteBtn.play(midiOverflow.newNoteSound(), newPitch);
            return new PressedMIDINote(newPitch, noteBtn, midiOverflow.newNoteSound());
        }
    }


    protected boolean canPerformMidi(final MidiEvent event) {
        final byte[] message = event.message.getMessage();

        final PressedMIDINote prevNoteBtn = pressedMidiNotes.get(message[1]);
        NoteButton prevButton = null;
        boolean isHoldableBtn = false;
        if (prevNoteBtn != null) {
            prevButton = prevNoteBtn.pressedNote();
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

            //TODO perhaps separate this logic
            case -128: {
                // release
                if (isHoldableBtn) {
                    IHoldableNoteButton heldBtn = (IHoldableNoteButton)prevButton;

                    heldBtn.releaseHeld(
                        prevNoteBtn.notePitch(),
                        true,
                        heldBtn.toHeldSound(prevNoteBtn.sound()),
                        // Play the animation only if
                        // there are no more note buttons here
                        pressedMidiNotes.values().stream()
                            .noneMatch((midiNote) -> midiNote.pressedNote() == prevNoteBtn.pressedNote())
                    );
                }

                break;
            }
        }

        return false;
    }

    /**
     * Handles changing the volume of the instrument
     * depending on the velocity of the MIDI press.
     * @param velocity The MIDI-IN velocity
     */
    protected void handleDynamicTouch(int velocity) {
        final float sensitivity = ModClientConfigs.MIDI_IN_SENSITIVITY.get().floatValue();

        // 0 sensitivity = fixed touch:
        if (!ModClientConfigs.FIXED_TOUCH.get() && (sensitivity != 0)) {
            double volMultiplier = (velocity / 127D) / sensitivity;
            instrument.volume = (int)Mth.clamp(instrument.volume * volMultiplier, MIN_MIDI_VELOCITY, 100);
        }
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
     * @param note The MIDI note to check
     * @return Whether the provided {@code note} is not in bounds of:
     * <p>{@link InstrumentMidiReceiver#minMidiNote minMidiNote} < {@code note} < {@link InstrumentMidiReceiver#maxMidiNote maxMidiNote}</p>
     */
    protected boolean isMIDIOutOfBounds(int note) {
        final int minNote = canMidiOverflow() ? minMidiOverflow() : minMidiNote();
        final int maxNote = canMidiOverflow() ? maxMidiOverflow() : maxMidiNote();
        return (note < minNote) || (note >= maxNote);
    }


    /**
     * @return Whether both this instrument and the client agrees
     * that this instrument may be overflown
     */
    protected boolean canMidiOverflow() {
        return allowMidiOverflow() && ModClientConfigs.EXTEND_OCTAVES.get();
    }

    /**
     * Extends the usual limitation of octaves by providing the
     * {@link InstrumentMidiReceiver#getLowestNote lowest}/{@link InstrumentMidiReceiver#getHighestNote highest} note button,
     * pitched up or down.
     * @param note The current note
     * @return The MIDI overflow result, or null when not overflowing.
     */
    protected @Nullable MidiOverflowResult handleMidiOverflow(int note) {
        if (!canMidiOverflow())
            return null;

        if (note < minMidiNote()) {
            return new MidiOverflowResult(
                getLowestNote().getSound(),
                note - minMidiNote(),
                note + 12,
                OverflowType.BOTTOM
            );
        } else if (note >= maxMidiNote()) {
            return new MidiOverflowResult(
                getHighestNote().getSound(),
                note - maxMidiNote() + 1,
                note - 12,
                OverflowType.TOP
            );
        }

        return null;
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
