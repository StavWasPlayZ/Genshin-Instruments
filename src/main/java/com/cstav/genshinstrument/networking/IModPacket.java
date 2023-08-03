package com.cstav.genshinstrument.networking;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public interface IModPacket {
    public default void toBytes(final FriendlyByteBuf buf) {}
    public void handle(final Supplier<Context> supplier);
}
