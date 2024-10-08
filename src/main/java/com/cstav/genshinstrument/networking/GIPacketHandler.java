package com.cstav.genshinstrument.networking;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.networking.packet.instrument.c2s.C2SHeldNoteSoundPacket;
import com.cstav.genshinstrument.networking.packet.instrument.c2s.C2SNoteSoundPacket;
import com.cstav.genshinstrument.networking.packet.instrument.c2s.CloseInstrumentPacket;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.NotifyInstrumentOpenPacket;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.OpenInstrumentPacket;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.S2CHeldNoteSoundPacket;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.S2CNoteSoundPacket;
import com.cstav.genshinstrument.util.ServerUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;

@EventBusSubscriber(modid = GInstrumentMod.MODID, bus = Bus.MOD)
public class GIPacketHandler {
    @SuppressWarnings("unchecked")
    public static final List<Class<IModPacket>> ACCEPTABLE_PACKETS = List.of(new Class[] {
        NotifyInstrumentOpenPacket.class,
        C2SNoteSoundPacket.class, S2CNoteSoundPacket.class,
        OpenInstrumentPacket.class, CloseInstrumentPacket.class,
        C2SHeldNoteSoundPacket.class, S2CHeldNoteSoundPacket.class
    });

    private static int id = 0;
    public static void registerPackets() {
        ServerUtil.registerModPackets(INSTANCE, ACCEPTABLE_PACKETS, () -> id++);
    }


    private static final String PROTOCOL_VERSION = "5.1";

    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(GInstrumentMod.MODID, "main"),
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
}
