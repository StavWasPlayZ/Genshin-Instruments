package com.cstav.genshinstrument.networking.packets.lyre;

import java.util.UUID;
import java.util.function.Supplier;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.networking.ModPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class NotifyInstrumentOpenPacket implements ModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;


    private final UUID playerUUID;
    private final boolean isOpen;
    public NotifyInstrumentOpenPacket(final UUID playerUUID, final boolean isOpen) {
        this.playerUUID = playerUUID;
        this.isOpen = isOpen;
    }
    public NotifyInstrumentOpenPacket(final FriendlyByteBuf buf) {
        playerUUID = buf.readUUID();
        isOpen = buf.readBoolean();
    }
    
    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(playerUUID);
        buf.writeBoolean(isOpen);
    }


    @Override
    public boolean handle(Supplier<Context> supplier) {
        final Context context = supplier.get();

        context.enqueueWork(() ->
            InstrumentOpenProvider.setOpen(Minecraft.getInstance().level.getPlayerByUUID(playerUUID), isOpen)
        );

        return true;
    }
    
}
