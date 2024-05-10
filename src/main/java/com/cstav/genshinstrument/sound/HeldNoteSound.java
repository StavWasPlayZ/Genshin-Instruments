package com.cstav.genshinstrument.sound;

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

    public static enum Phase {
        ATTACK, HOLD
    }

}
