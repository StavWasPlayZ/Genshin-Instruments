package com.cstav.genshinstrument.networking.packet.instrument;

import java.util.Optional;
import java.util.UUID;

import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.INoteIdentifierSender;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class PlayNotePacket implements INoteIdentifierSender {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;

    private final int pitch, volume;

    private final Optional<BlockPos> position;
    private final NoteSound sound;
    private final ResourceLocation instrumentId;
    private final Optional<NoteButtonIdentifier> noteIdentifier;
    
    private final Optional<UUID> playerUUID;
    private final Optional<InteractionHand> hand;

    public PlayNotePacket(Optional<BlockPos> pos, NoteSound sound, int pitch, int volume, ResourceLocation instrumentId,
        Optional<NoteButtonIdentifier> noteIdentifier, Optional<UUID> playerUUID, Optional<InteractionHand> hand) {

        this.pitch = pitch;
        this.volume = volume;

        this.position = pos;
        this.sound = sound;
        this.instrumentId = instrumentId;
        this.noteIdentifier = noteIdentifier;

        this.playerUUID = playerUUID;
        this.hand = hand;
    }
    public PlayNotePacket(FriendlyByteBuf buf) {
        pitch = buf.readInt();
        volume = buf.readInt();

        position = buf.readOptional(FriendlyByteBuf::readBlockPos);
        sound = NoteSound.readFromNetwork(buf);
        instrumentId = buf.readResourceLocation();
        noteIdentifier = buf.readOptional(this::readNoteIdentifierFromNetwork);

        playerUUID = buf.readOptional(FriendlyByteBuf::readUUID);
        hand = buf.readOptional((fbb) -> fbb.readEnum(InteractionHand.class));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(pitch);
        buf.writeInt(volume);

        buf.writeOptional(position, FriendlyByteBuf::writeBlockPos);
        sound.writeToNetwork(buf);
        buf.writeResourceLocation(instrumentId);
        buf.writeOptional(noteIdentifier, (fbb, identifier) -> identifier.writeToNetwork(fbb));

        buf.writeOptional(playerUUID, FriendlyByteBuf::writeUUID);
        buf.writeOptional(hand, FriendlyByteBuf::writeEnum);
    }


    @Override
    public void handle(final Context context) {
        sound.play(
            pitch, volume, playerUUID, hand,
            instrumentId, noteIdentifier, position
        );
    }
}