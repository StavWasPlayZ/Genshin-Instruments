package com.cstav.genshinstrument.networking.packet.instrument.util;

import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.S2CNotePacket;

import java.util.Optional;
import java.util.UUID;

@FunctionalInterface
public interface S2CNotePacketDelegate<T> {
    /**
     * Construct a new Play Note packet.
     * @param sound The sound to play
     * @param meta The sound metadata
     * @param initiatorUUID The UUID of the player initiating the sound.
     *                      May be empty for a non-player trigger.
//     * @param <T> The sound object type
     */
    S2CNotePacket<T> create(Optional<UUID> initiatorUUID, T sound, NoteSoundMetadata meta);
}
