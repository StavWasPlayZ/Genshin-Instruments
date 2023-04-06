package com.cstav.genshinstrument.networking.packets.lyre;

import java.util.function.Supplier;

import com.cstav.genshinstrument.client.gui.screens.lyre.LyreScreen;
import com.cstav.genshinstrument.networking.ModPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class OpenLyrePacket implements ModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;

    public OpenLyrePacket() {}
    public OpenLyrePacket(FriendlyByteBuf buf) {}


    @Override
    public boolean handle(final Supplier<Context> supplier) {
        final Context context = supplier.get();

        context.enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> LyreScreen.open())
        );

        return true;
    }
}