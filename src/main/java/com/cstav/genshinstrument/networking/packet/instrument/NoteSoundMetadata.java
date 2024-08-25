package com.cstav.genshinstrument.networking.packet.instrument;

import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * Metadata to be passed around in instrument packets; usually tied to a {@link NoteSound}.
 * @param pos The position of the sound being produced
 * @param instrumentId The ID of the instrument initiating the sound
 * @param pitch The pitch of the sound to initiate
 * @param volume The volume of the sound to initiate
 * @param noteIdentifier The identifier of the note
 */
public record NoteSoundMetadata(
    BlockPos pos,

    int pitch,
    int volume,
    ResourceLocation instrumentId,
    Optional<NoteButtonIdentifier> noteIdentifier
) {
    public static NoteSoundMetadata read(final FriendlyByteBuf buf) {
        return new NoteSoundMetadata(
            buf.readBlockPos(),

            buf.readInt(),
            buf.readInt(),
            buf.readResourceLocation(),
            buf.readOptional(NoteButtonIdentifier::readFromNetwork)
        );
    }

    public void write(final FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);

        buf.writeInt(pitch);
        buf.writeInt(volume);

        buf.writeResourceLocation(instrumentId);
        buf.writeOptional(noteIdentifier, (fbb, identifier) -> identifier.writeToNetwork(fbb));
    }
}
