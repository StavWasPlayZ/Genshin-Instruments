package com.cstav.genshinstrument.sound;

import java.util.Arrays;

public record HeldNoteSound(NoteSound attack, NoteSound hold, int holdFadeIn, int holdFadeOut, int holdDelay) {

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


    public void startPlaying() {

    }


    public static enum Phase {
        ATTACK, HOLD
    }

}
