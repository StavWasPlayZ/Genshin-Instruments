package com.cstav.genshinstrument.networking.packet.instrument.util;

import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.S2CHeldNoteSoundPacket;
import com.cstav.genshinstrument.sound.held.HeldNoteSound;
import com.cstav.genshinstrument.sound.held.InitiatorID;
import net.minecraft.world.entity.Entity;

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
     * @param initiatorID The ID of the entity initiating the sound.
     *                    May be empty for a non-entity trigger.
     * @param oInitiatorID The ID of the non-entity initiating the sound.
     *                     May be empty for an entity trigger.
     * @param phase The phase for the packet to report
     */
    S2CHeldNoteSoundPacket create(Optional<Integer> initiatorID, Optional<InitiatorID> oInitiatorID,
                                  HeldNoteSound sound, NoteSoundMetadata meta,
                                  HeldSoundPhase phase);

    default S2CNotePacketDelegate<HeldNoteSound> toReg(InitiatorID oInitiatorID, HeldSoundPhase phase) {
        return toReg(this, oInitiatorID, phase);
    }
    default S2CNotePacketDelegate<HeldNoteSound> toReg(HeldSoundPhase phase, Entity initiator) {
        return toReg(this, phase, initiator);
    }

    static S2CNotePacketDelegate<HeldNoteSound> toReg(S2CHeldNotePacketDelegate del,
                                                      InitiatorID oInitiatorID, HeldSoundPhase phase) {
        return (initiatorID, sound, meta) -> del.create(initiatorID, Optional.of(oInitiatorID), sound, meta, phase);
    }
    static S2CNotePacketDelegate<HeldNoteSound> toReg(S2CHeldNotePacketDelegate del, HeldSoundPhase phase,
                                                      Entity initiator) {
        return (initiatorID, sound, meta) -> del.create(
            initiatorID,
            initiatorID.isPresent() ? Optional.of(InitiatorID.fromObj(initiator)) : Optional.empty(),
            sound, meta, phase
        );
    }
}
