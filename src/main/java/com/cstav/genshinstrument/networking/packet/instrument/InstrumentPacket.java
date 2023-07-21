package com.cstav.genshinstrument.networking.packet.instrument;

import java.util.function.Supplier;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpen;
import com.cstav.genshinstrument.networking.ModPacket;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.util.ServerUtil;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class InstrumentPacket implements ModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;


    private final NoteSound sound;
    private final InteractionHand hand;
    private final int pitch;

    private final ResourceLocation instrumentId;
    private final NoteButtonIdentifier noteIdentifier;

    public InstrumentPacket(NoteSound sound, int pitch, InteractionHand hand,
            ResourceLocation instrumentId, NoteButtonIdentifier noteIdentifier) {
        this.sound = sound;
        this.hand = hand;
        this.pitch = pitch;

        this.instrumentId = instrumentId;
        this.noteIdentifier = noteIdentifier;
    }
    public InstrumentPacket(FriendlyByteBuf buf) {
        sound = NoteSound.readFromNetwork(buf);
        hand = buf.readEnum(InteractionHand.class);
        pitch = buf.readInt();

        instrumentId = buf.readResourceLocation();
        noteIdentifier = NoteButtonIdentifier.readIdentifier(buf);
    }

    @Override
    public void toBytes(final FriendlyByteBuf buf) {
        sound.writeToNetwork(buf);
        buf.writeEnum(hand);
        buf.writeInt(pitch);

        buf.writeResourceLocation(instrumentId);
        noteIdentifier.writeToNetwork(buf);
    }


    @Override
    public boolean handle(final Supplier<Context> supplier) {
        final Context context = supplier.get();
        
        context.enqueueWork(() -> {
            final ServerPlayer player = context.getSender();

            if (!InstrumentOpen.isOpen(player))
                return;

            ServerUtil.sendPlayNotePackets(player, hand, sound, instrumentId, noteIdentifier, pitch);
        });

        return true;
    }
    
}