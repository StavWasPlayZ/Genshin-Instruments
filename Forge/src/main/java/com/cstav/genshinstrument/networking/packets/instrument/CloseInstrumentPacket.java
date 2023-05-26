package com.cstav.genshinstrument.networking.packets.instrument;

import java.util.function.Supplier;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.networking.ModPacket;
import com.cstav.genshinstrument.networking.ModPacketHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class CloseInstrumentPacket implements ModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;

    public CloseInstrumentPacket() {}
    public CloseInstrumentPacket(FriendlyByteBuf buf) {}


    @Override
    public boolean handle(final Supplier<Context> supplier) {
        final Context context = supplier.get();

        context.enqueueWork(() -> {
            final ServerPlayer player = context.getSender();
            InstrumentOpenProvider.setOpen(player, false);

            for (final Player oPlayer : player.level.players())
                ModPacketHandler.sendToClient(new NotifyInstrumentOpenPacket(player.getUUID(), false), (ServerPlayer)oPlayer);
        });

        return true;
    }
    
}