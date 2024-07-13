package com.cstav.genshinstrument.networking.packet.instrument;

import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.INoteIdentifierSender;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.UUID;

/**
 * Metadata to be passed around in instrument packets; usually tied to a {@link NoteSound}.
 */
public record NoteSoundMetadata(
    Optional<UUID> playerUUID,
    /* Optionally pass a position that defers from the player's */
    Optional<BlockPos> pos,

    int pitch,
    int volume,
    ResourceLocation instrumentId,
    Optional<NoteButtonIdentifier> noteIdentifier
) {
    public static NoteSoundMetadata read(final FriendlyByteBuf buf, INoteIdentifierSender noteIdentifierSender) {
        return new NoteSoundMetadata(
            buf.readOptional(FriendlyByteBuf::readUUID),
            buf.readOptional(FriendlyByteBuf::readBlockPos),

            buf.readInt(),
            buf.readInt(),
            buf.readResourceLocation(),
            buf.readOptional(noteIdentifierSender::readNoteIdentifierFromNetwork)
        );
    }

    public void write(final FriendlyByteBuf buf) {
        buf.writeOptional(playerUUID, FriendlyByteBuf::writeUUID);
        buf.writeOptional(pos, FriendlyByteBuf::writeBlockPos);

        buf.writeInt(pitch);
        buf.writeInt(volume);

        buf.writeResourceLocation(instrumentId);
        buf.writeOptional(noteIdentifier, (fbb, identifier) -> identifier.writeToNetwork(fbb));
    }
}
