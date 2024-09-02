package com.cstav.genshinstrument.util;

import com.cstav.genshinstrument.networking.IModPacket;
import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.function.Supplier;

public class ServerUtil {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int PLAY_DISTANCE = 16;


    public static void registerModPackets(SimpleChannel sc, List<Class<IModPacket>> acceptablePackets, Supplier<Integer> id) {
        for (final Class<IModPacket> packetType : acceptablePackets)
            try {

                final Constructor<IModPacket> packetConstructor = packetType.getDeclaredConstructor(FriendlyByteBuf.class);
                
                sc.messageBuilder(packetType, id.get(), getDirection(packetType))
                    .encoder(IModPacket::write)
                    .decoder((buf) -> {
                        try {
                            return packetConstructor.newInstance(buf);
                        } catch (Exception e) {
                            LOGGER.error("Error constructing packet of type {}", packetType.getName(), e);
                            return null;
                        }
                    })
                    .consumerMainThread(IModPacket::handle)
                .add();

            } catch (Exception e) {
                LOGGER.error(
                    "Error registering packet of type "+packetType.getName()
                        +". Make sure to have a NETWORK_DIRECTION static field of type NetworkDirection."
                , e);
            }
    }
    private static NetworkDirection getDirection(final Class<IModPacket> packetType)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        return (NetworkDirection)packetType.getField("NETWORK_DIRECTION").get(null);
    }
}
