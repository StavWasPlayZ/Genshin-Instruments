package com.cstav.genshinstrument.networking.packets.instrument;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import com.cstav.genshinstrument.networking.ModPacket;
import com.cstav.genshinstrument.networking.buttonidentifiers.NoteButtonIdentifier;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class PlayNotePacket implements ModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;


    private final BlockPos blockPos;
    private final NoteSound sound;
    private final float pitch;
    private final ResourceLocation instrumentId;
    private final NoteButtonIdentifier noteIdentifier;
    
    private final Optional<UUID> playerUUID;
    private final Optional<InteractionHand> hand;

    public PlayNotePacket(BlockPos pos, NoteSound sound, float pitch, ResourceLocation instrumentId,
        NoteButtonIdentifier noteIdentifier, Optional<UUID> playerUUID, Optional<InteractionHand> hand) {
        this.blockPos = pos;
        this.sound = sound;
        this.pitch = pitch;
        this.instrumentId = instrumentId;
        this.noteIdentifier = noteIdentifier;

        this.playerUUID = playerUUID;
        this.hand = hand;
    }
    public PlayNotePacket(FriendlyByteBuf buf) {
        blockPos = buf.readBlockPos();
        sound = NoteSound.readFromNetwork(buf);
        pitch = buf.readFloat();
        instrumentId = buf.readResourceLocation();
        noteIdentifier = NoteButtonIdentifier.readIdentifier(buf);

        playerUUID = buf.readOptional(FriendlyByteBuf::readUUID);
        hand = buf.readOptional((fbb) -> fbb.readEnum(InteractionHand.class));
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        sound.writeToNetwork(buf);
        buf.writeFloat(pitch);
        buf.writeResourceLocation(instrumentId);
        noteIdentifier.writeToNetwork(buf);

        buf.writeOptional(playerUUID, FriendlyByteBuf::writeUUID);
        buf.writeOptional(hand, FriendlyByteBuf::writeEnum);
    }


    @Override
    public boolean handle(final Supplier<Context> supplier) {
        supplier.get().enqueueWork(() ->

            sound.playAtPos(
                pitch, playerUUID.orElse(null), hand.orElse(null),
                instrumentId, noteIdentifier, blockPos
            )
            
        );

        return true;
    }
}