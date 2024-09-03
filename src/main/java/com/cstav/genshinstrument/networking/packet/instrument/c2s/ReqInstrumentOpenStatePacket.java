package com.cstav.genshinstrument.networking.packet.instrument.c2s;

import com.cstav.genshinstrument.capability.ModCapabilities;
import com.cstav.genshinstrument.networking.IModPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.network.NetworkDirection;

import java.util.UUID;

public class ReqInstrumentOpenStatePacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;

    private final UUID uuid;

    public ReqInstrumentOpenStatePacket(final UUID uuid) {
        this.uuid = uuid;
    }
    public ReqInstrumentOpenStatePacket(FriendlyByteBuf buf) {
        uuid = buf.readUUID();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
    }

    @Override
    public void handle(final Context context) {
        final ServerPlayer player = context.getSender();
        ModCapabilities.notifyOpenStateToPlayer(player.level().getPlayerByUUID(uuid), player);
    }
}
