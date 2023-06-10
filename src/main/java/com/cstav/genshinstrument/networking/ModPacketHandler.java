package com.cstav.genshinstrument.networking;

import java.util.List;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.networking.packets.instrument.CloseInstrumentPacket;
import com.cstav.genshinstrument.networking.packets.instrument.InstrumentPacket;
import com.cstav.genshinstrument.networking.packets.instrument.NotifyInstrumentOpenPacket;
import com.cstav.genshinstrument.networking.packets.instrument.OpenInstrumentPacket;
import com.cstav.genshinstrument.networking.packets.instrument.PlayNotePacket;

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
    @SuppressWarnings("unchecked")
    private static final List<Class<ModPacket>> ACCEPTABLE_PACKETS = List.of(new Class[] {
        InstrumentPacket.class, PlayNotePacket.class, OpenInstrumentPacket.class, CloseInstrumentPacket.class,
        NotifyInstrumentOpenPacket.class
    });


    private static final String PROTOCOL_VERSION = "3.7";
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
                                e.printStackTrace();
                                return null;
                            }
                        })
                        .encoder(ModPacket::toBytes)
                        .consumerMainThread(ModPacket::handle)
                        .add();

                } catch (Exception e) {
                    e.printStackTrace();
                }

        });
    }

}
