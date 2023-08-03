package com.cstav.genshinstrument.networking.packet.instrument;

import java.util.Optional;
import java.util.function.Supplier;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpen;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.INoteIdentifierSender;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.util.ServerUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class InstrumentPacket implements INoteIdentifierSender {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;


    private final BlockPos pos;
    private final NoteSound sound;
    private final Optional<InteractionHand> hand;
    private final int pitch;

    private final ResourceLocation instrumentId;
    private final NoteButtonIdentifier noteIdentifier;

    public InstrumentPacket(BlockPos pos, NoteSound sound, int pitch, Optional<InteractionHand> hand,
            ResourceLocation instrumentId, NoteButtonIdentifier noteIdentifier) {
        this.pos = pos;
        this.sound = sound;
        this.hand = hand;
        this.pitch = pitch;

        this.instrumentId = instrumentId;
        this.noteIdentifier = noteIdentifier;
    }
    public InstrumentPacket(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
        sound = NoteSound.readFromNetwork(buf);
        hand = buf.readOptional((fbb) -> buf.readEnum(InteractionHand.class));
        pitch = buf.readInt();

        instrumentId = buf.readResourceLocation();
        noteIdentifier = readNoteIdentifierFromNetwork(buf);
    }

    @Override
    public void toBytes(final FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        sound.writeToNetwork(buf);
        buf.writeOptional(hand, FriendlyByteBuf::writeEnum);
        buf.writeInt(pitch);

        buf.writeResourceLocation(instrumentId);
        noteIdentifier.writeToNetwork(buf);
    }


    @Override
    public void handle(final Supplier<Context> supplier) {
        final Context context = supplier.get();
        
        context.enqueueWork(() -> {
            final ServerPlayer player = context.getSender();

            if (!InstrumentOpen.isOpen(player))
                return;

            ServerUtil.sendPlayNotePackets(player, pos, hand, sound, instrumentId, noteIdentifier, pitch);
        });

        context.setPacketHandled(true);
    }
    
}