package com.cstav.genshinstrument.networking.packet.instrument;

import java.util.UUID;
import java.util.function.Supplier;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.networking.ModPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class NotifyInstrumentClosedPacket implements ModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;


    private final UUID playerUUID;
    public NotifyInstrumentClosedPacket(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }
    public NotifyInstrumentClosedPacket(final FriendlyByteBuf buf) {
        playerUUID = buf.readUUID();
    }
    
    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(playerUUID);
    }


    @Override
    public boolean handle(Supplier<Context> supplier) {
        final Context context = supplier.get();

        context.enqueueWork(() ->
            InstrumentOpenProvider.setClosed(Minecraft.getInstance().level.getPlayerByUUID(playerUUID))
        );

        return true;
    }
    
}
