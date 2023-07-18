package com.cstav.genshinstrument.networking;

import java.util.List;

import org.slf4j.Logger;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.networking.buttonidentifier.DrumNoteIdentifier;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteGridButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.instrument.CloseInstrumentPacket;
import com.cstav.genshinstrument.networking.packet.instrument.InstrumentPacket;
import com.cstav.genshinstrument.networking.packet.instrument.NotifyInstrumentOpenPacket;
import com.cstav.genshinstrument.networking.packet.instrument.OpenInstrumentPacket;
import com.cstav.genshinstrument.networking.packet.instrument.PlayNotePacket;
import com.cstav.genshinstrument.util.ServerUtil;
import com.mojang.logging.LogUtils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

@EventBusSubscriber(modid = Main.MODID, bus = Bus.MOD)
public class ModPacketHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("unchecked")
    private static final List<Class<ModPacket>> ACCEPTABLE_PACKETS = List.of(new Class[] {
        InstrumentPacket.class, PlayNotePacket.class, OpenInstrumentPacket.class, CloseInstrumentPacket.class,
        NotifyInstrumentOpenPacket.class
    });


    @SuppressWarnings("unchecked")
    private static final List<Class<? extends NoteButtonIdentifier>> ACCEPTABLE_IDENTIFIERS = List.of(new Class[] {
        NoteButtonIdentifier.class, NoteGridButtonIdentifier.class, DrumNoteIdentifier.class
    });

    /**
     * @see ServerUtil#getValidNoteIdentifier
     */
    public static Class<? extends NoteButtonIdentifier> getValidIdentifier(String classType)
            throws ClassNotFoundException {
        return ServerUtil.getValidNoteIdentifier(classType, ACCEPTABLE_IDENTIFIERS);
    }


    private static final String PROTOCOL_VERSION = "4";
    private static int id;

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(Main.MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );


    public static <T> void sendToServer(final T packet) {
        INSTANCE.sendToServer(packet);
    }
    public static <T> void sendToClient(final T packet, final ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }


    @SubscribeEvent
    public static void registerPackets(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

            for (final Class<ModPacket> packetType : ACCEPTABLE_PACKETS)
                try {
                    
                    INSTANCE.messageBuilder(packetType, id++, (NetworkDirection)packetType.getField("NETWORK_DIRECTION").get(null))
                        .decoder((buf) -> {
                            try {
                                return packetType.getDeclaredConstructor(FriendlyByteBuf.class).newInstance(buf);
                            } catch (Exception e) {
                                LOGGER.error("Error constructing packet of type "+packetType.getName(), e);
                                return null;
                            }
                        })
                        .encoder(ModPacket::toBytes)
                        .consumerMainThread(ModPacket::handle)
                    .add();

                } catch (Exception e) {
                    LOGGER.error(
                        "Error registring packet of type "+packetType.getName()
                            +". Make sure to have a NETWORK_DIRECTION static field of type NetworkDirection."
                    , e);
                }

        });
    }

}
