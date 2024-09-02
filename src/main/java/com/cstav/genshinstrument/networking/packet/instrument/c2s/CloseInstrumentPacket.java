package com.cstav.genshinstrument.networking.packet.instrument.c2s;

import com.cstav.genshinstrument.networking.IModPacket;
import com.cstav.genshinstrument.networking.packet.instrument.util.InstrumentPacketUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.network.NetworkDirection;

/**
 * A C2S packet for notifying the sever that the
 * client has closed their instrument screen
 */
public class CloseInstrumentPacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;

    public CloseInstrumentPacket() {}
    public CloseInstrumentPacket(FriendlyByteBuf buf) {}


    @Override
    public void handle(final Context context) {
        InstrumentPacketUtil.setInstrumentClosed(context.getSender());
    }
    
}