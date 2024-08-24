package com.cstav.genshinstrument.networking.packet.instrument.util;

import com.cstav.genshinstrument.sound.held.HeldNoteSound;

/**
 * Different from {@link HeldNoteSound.Phase}!
 * Represents the packet state of a held note sound.
 */
public enum HeldSoundPhase {
    ATTACK, RELEASE
}
