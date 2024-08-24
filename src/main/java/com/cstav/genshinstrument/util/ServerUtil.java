package com.cstav.genshinstrument.util;

import com.cstav.genshinstrument.networking.IModPacket;
import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ServerUtil {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void registerModPackets(SimpleChannel sc, List<Class<IModPacket>> acceptablePackets, Supplier<Integer> id) {
        for (final Class<IModPacket> packetType : acceptablePackets) {

            final NetworkDirection netDirection;
            try {
                netDirection = getDirection(packetType);
            } catch (Exception e) {
                LOGGER.error(
                    "Error registering packet of type " + packetType.getName()
                        + ". Make sure to have a NETWORK_DIRECTION static field of type NetworkDirection.",
                    e
                );
                continue;
            }

            final Constructor<IModPacket> packetConstructor = CommonUtil.getExpectedConstructor(packetType, FriendlyByteBuf.class);

            sc.registerMessage(
                id.get(), packetType, IModPacket::write,

                // Packet out
                (buf) -> {
                    try {
                        return packetConstructor.newInstance(buf);
                    } catch (Exception e) {
                        LOGGER.error("Error constructing packet of type {}", packetType.getName(), e);
                        return null;
                    }
                },
                // Packet in
                (msg, contextSup) -> {
                    final Context context = contextSup.get();
                    context.enqueueWork(() ->
                        msg.handle(context)
                    );

                    context.setPacketHandled(true);
                },

                Optional.of(netDirection)
            );
        }
    }
    private static NetworkDirection getDirection(final Class<IModPacket> packetType)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        return (NetworkDirection)packetType.getField("NETWORK_DIRECTION").get(null);
    }
}
