package com.cstav.genshinstrument.networking.packets;

import java.util.function.Supplier;

import com.cstav.genshinstrument.networking.ModPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class StopMusicPacket implements ModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;


    public StopMusicPacket() {}
    public StopMusicPacket(FriendlyByteBuf buf) {}


    @Override
    public boolean handle(final Supplier<Context> supplier) {
        supplier.get().enqueueWork(() ->
            Minecraft.getInstance().getMusicManager().stopPlaying()
        );

        return true;
    }
    
}