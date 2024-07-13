package com.cstav.genshinstrument.networking.packet.instrument.c2s;

import com.cstav.genshinstrument.networking.packet.INoteIdentifierSender;
import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

/**
 * A generic instrument note packet,
 * notifying that a specific note should be played in the level
 * @param <T> The sound object type
 */
public abstract class C2SNotePacket<T> implements INoteIdentifierSender {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;

    protected final T sound;
    protected final NoteSoundMetadata meta;


    public C2SNotePacket(T sound, NoteSoundMetadata meta) {
        this.sound = sound;
        this.meta = meta;
    }

    public C2SNotePacket(FriendlyByteBuf buf) {
        sound = readSound(buf);
        meta = NoteSoundMetadata.read(buf, this);
    }
    @Override
    public void write(final FriendlyByteBuf buf) {
        writeSound(buf);
        meta.write(buf);
    }

    protected abstract T readSound(FriendlyByteBuf buf);
    protected abstract void writeSound(FriendlyByteBuf buf);


    @Override
    public void handle(final Context context) {
        final ServerPlayer player = context.getSender();
        sendPlayNotePackets(player);
    }
    protected abstract void sendPlayNotePackets(final ServerPlayer player);
}
