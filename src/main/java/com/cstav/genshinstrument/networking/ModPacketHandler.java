package com.cstav.genshinstrument.networking;

import java.util.List;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.networking.buttonidentifier.DefaultNoteButtonIdentifier;
import com.cstav.genshinstrument.networking.buttonidentifier.DrumNoteIdentifier;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteGridButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.instrument.CloseInstrumentPacket;
import com.cstav.genshinstrument.networking.packet.instrument.InstrumentPacket;
import com.cstav.genshinstrument.networking.packet.instrument.NotifyInstrumentOpenPacket;
import com.cstav.genshinstrument.networking.packet.instrument.OpenInstrumentPacket;
import com.cstav.genshinstrument.networking.packet.instrument.PlayNotePacket;
import com.cstav.genshinstrument.util.ServerUtil;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

@EventBusSubscriber(modid = GInstrumentMod.MODID, bus = Bus.MOD)
public class ModPacketHandler {
    @SuppressWarnings("unchecked")
    public static final List<Class<IModPacket>> ACCEPTABLE_PACKETS = List.of(new Class[] {
        InstrumentPacket.class, PlayNotePacket.class, OpenInstrumentPacket.class, CloseInstrumentPacket.class,
        NotifyInstrumentOpenPacket.class
    });

    private static int id = 0;
    public static void registerPackets() {
        ServerUtil.registerModPackets(INSTANCE, ACCEPTABLE_PACKETS, () -> id++);
    }


    @SuppressWarnings({"unchecked", "deprecation"})
    public static final List<Class<? extends NoteButtonIdentifier>> ACCEPTABLE_IDENTIFIERS = List.of(new Class[] {
        DefaultNoteButtonIdentifier.class,
        NoteButtonIdentifier.class, NoteGridButtonIdentifier.class, DrumNoteIdentifier.class
    });


    private static final String PROTOCOL_VERSION = "5.0";

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
