package com.cstav.genshinstrument.networking.packet.instrument.util;

import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.S2CHeldNoteSoundPacket;
import com.cstav.genshinstrument.sound.held.HeldNoteSound;

import java.util.Optional;

/**
 * A delegate for a S2C Held Play Note packet
 */
@FunctionalInterface
public interface S2CHeldNotePacketDelegate {
    /**
     * Construct a new Held Play Note packet.
     * @param sound The sound to play
     * @param meta The sound metadata
     * @param initiatorID The ID of the player initiating the sound.
     *                    May be empty for a non-player trigger.
     * @param phase The phase for the packet to report
     */
    S2CHeldNoteSoundPacket create(Optional<Integer> initiatorID, HeldNoteSound sound, NoteSoundMetadata meta,
                                  HeldSoundPhase phase);

    default S2CNotePacketDelegate<HeldNoteSound> toReg(HeldSoundPhase phase) {
        return toReg(this, phase);
    }

    public static S2CNotePacketDelegate<HeldNoteSound> toReg(S2CHeldNotePacketDelegate del, HeldSoundPhase phase) {
        return (initiatorID, sound, meta) -> del.create(initiatorID, sound, meta, phase);
    }
}
