package com.cstav.genshinstrument.networking.packets.lyre;

import java.util.function.Supplier;

import com.cstav.genshinstrument.capability.lyreOpen.LyreOpenProvider;
import com.cstav.genshinstrument.networking.ModPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class CloseLyrePacket implements ModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;

    public CloseLyrePacket() {}
    public CloseLyrePacket(FriendlyByteBuf buf) {}


    @Override
    public boolean handle(final Supplier<Context> supplier) {
        final Context context = supplier.get();

        context.enqueueWork(() ->
            context.getSender().getCapability(LyreOpenProvider.LYRE_OPEN).ifPresent((lyreOpen) ->
                lyreOpen.setOpen(false)
            )
        );

        return true;
    }
    
}