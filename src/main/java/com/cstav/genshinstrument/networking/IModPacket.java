package com.cstav.genshinstrument.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public interface IModPacket {
    public default void write(final FriendlyByteBuf buf) {}
    public void handle(final Context context);
}
