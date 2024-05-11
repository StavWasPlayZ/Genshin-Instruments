package com.cstav.genshinstrument.sound;

import java.util.Arrays;

public class HeldNoteSound {

    NoteSound attack, hold;

    public HeldNoteSound(NoteSound attack, NoteSound hold) {
        this.attack = attack;
        this.hold = hold;
    }

    public NoteSound getSound(final Phase phase) {
        return switch (phase) {
            case HOLD -> hold;
            case ATTACK -> attack;
        };
    }
    public static NoteSound[] getSounds(final HeldNoteSound[] sounds, final Phase phase) {
        return Arrays.stream(sounds)
            .map((sound) -> sound.getSound(phase))
            .toArray(NoteSound[]::new);
    }

    public static enum Phase {
        ATTACK, HOLD
    }

}
