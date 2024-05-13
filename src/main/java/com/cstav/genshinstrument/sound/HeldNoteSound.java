package com.cstav.genshinstrument.sound;

import java.util.Arrays;

public class HeldNoteSound {

    public final NoteSound attack, hold;
    public final int holdFadeIn, holdFadeOut, holdDelay;

    public HeldNoteSound(NoteSound attack, NoteSound hold, int holdFadeIn, int holdFadeOut, int holdDelay) {
        this.attack = attack;
        this.hold = hold;
        this.holdFadeIn = holdFadeIn;
        this.holdFadeOut = holdFadeOut;
        this.holdDelay = holdDelay;
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


    public void startPlaying() {

    }


    public static enum Phase {
        ATTACK, HOLD
    }

}
