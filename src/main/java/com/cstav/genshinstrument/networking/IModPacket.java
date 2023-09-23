package com.cstav.genshinstrument.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public interface IModPacket {
    public default void write(final FriendlyByteBuf buf) {}
    public void handle(final Context context);
}
