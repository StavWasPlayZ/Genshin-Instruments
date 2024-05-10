package com.cstav.genshinstrument.sound;

public class HeldNoteSound {

    NoteSound attack, hold, release;

    public HeldNoteSound(NoteSound attack, NoteSound hold, NoteSound release) {
        this.attack = attack;
        this.hold = hold;
        this.release = release;
    }

    public NoteSound getSound(final Phase phase) {
        return switch (phase) {
            case HOLD -> hold;
            case ATTACK -> attack;
            case RELEASE -> release;
        };
    }

    public static enum Phase {
        ATTACK, HOLD, RELEASE
    }

}
