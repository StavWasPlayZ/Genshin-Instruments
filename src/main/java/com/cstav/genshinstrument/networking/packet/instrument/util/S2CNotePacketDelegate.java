package com.cstav.genshinstrument.networking.packet.instrument.util;

import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.S2CNotePacket;

import java.util.Optional;

/**
 * A delegate for a S2C Play Note packet
 * @param <T> The sound object type
 */
@FunctionalInterface
public interface S2CNotePacketDelegate<T, P extends S2CNotePacket<T>> {
    /**
     * Construct a new Play Note packet.
     * @param sound The sound to play
     * @param meta The sound metadata
     * @param initiatorID The ID of the player initiating the sound.
     *                    May be empty for a non-player trigger.
     */
    P create(Optional<Integer> initiatorID, T sound, NoteSoundMetadata meta);
}
