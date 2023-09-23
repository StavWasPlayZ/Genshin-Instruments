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

    private final int pitch;
    private final float volume;

    private final BlockPos blockPos;
    private final NoteSound sound;
    private final ResourceLocation instrumentId;
    private final NoteButtonIdentifier noteIdentifier;
    
    private final Optional<UUID> playerUUID;
    private final Optional<InteractionHand> hand;

    public PlayNotePacket(BlockPos pos, NoteSound sound, int pitch, float volume, ResourceLocation instrumentId,
        NoteButtonIdentifier noteIdentifier, Optional<UUID> playerUUID, Optional<InteractionHand> hand) {

        this.pitch = pitch;
        this.volume = volume;

        this.blockPos = pos;
        this.sound = sound;
        this.instrumentId = instrumentId;
        this.noteIdentifier = noteIdentifier;

        this.playerUUID = playerUUID;
        this.hand = hand;
    }
    public PlayNotePacket(FriendlyByteBuf buf) {
        pitch = buf.readInt();
        volume = buf.readFloat();

        blockPos = buf.readBlockPos();
        sound = NoteSound.readFromNetwork(buf);
        instrumentId = buf.readResourceLocation();
        noteIdentifier = readNoteIdentifierFromNetwork(buf);

        playerUUID = buf.readOptional(FriendlyByteBuf::readUUID);
        hand = buf.readOptional((fbb) -> fbb.readEnum(InteractionHand.class));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(pitch);
        buf.writeFloat(volume);

        buf.writeBlockPos(blockPos);
        sound.writeToNetwork(buf);
        buf.writeResourceLocation(instrumentId);
        noteIdentifier.writeToNetwork(buf);

        buf.writeOptional(playerUUID, FriendlyByteBuf::writeUUID);
        buf.writeOptional(hand, FriendlyByteBuf::writeEnum);
    }


    @Override
    public void handle(final Context context) {
        sound.playAtPos(
            pitch, volume, playerUUID.orElse(null), hand,
            instrumentId, noteIdentifier, blockPos
        );
    }
}