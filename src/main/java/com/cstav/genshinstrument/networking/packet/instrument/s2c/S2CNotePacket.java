package com.cstav.genshinstrument.networking.packet.instrument.s2c;

import com.cstav.genshinstrument.networking.packet.INoteIdentifierSender;
import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;

import java.util.Optional;
import java.util.UUID;

/**
 * A generic S2C packet notifying the client to play
 * a specific note.
 * @param <T> The sound object type
 */
public abstract class S2CNotePacket<T> implements INoteIdentifierSender {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;

    protected final Optional<UUID> initiatorUUID;
    protected final T sound;
    protected final NoteSoundMetadata meta;

    /**
     * Constructs a new {@link S2CNoteSoundPacket}.
     * @param initiatorUUID The UUID of the player initiating the sound.
     *                      May be empty for a non-player trigger.
     */
    public S2CNotePacket(Optional<UUID> initiatorUUID, T sound, NoteSoundMetadata meta) {
        this.initiatorUUID = initiatorUUID;
        this.sound = sound;
        this.meta = meta;
    }
    public S2CNotePacket(FriendlyByteBuf buf) {
        initiatorUUID = buf.readOptional(FriendlyByteBuf::readUUID);
        sound = readSound(buf);
        meta = NoteSoundMetadata.read(buf, this);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeOptional(initiatorUUID, FriendlyByteBuf::writeUUID);
        writeSound(buf);
        meta.write(buf);
    }

    protected abstract T readSound(FriendlyByteBuf buf);
    protected abstract void writeSound(FriendlyByteBuf buf);
}
