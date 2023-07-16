package com.cstav.genshinstrument.networking;

import java.util.List;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.gui.screens.instrument.drum.DrumNoteIdentifier;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButtonIdentifier;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.NoteGridButtonIdentifier;
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


    @SuppressWarnings("unchecked")
    private static final List<Class<? extends NoteButtonIdentifier>> ACCEPTABLE_IDENTIFIERS = List.of(new Class[] {
        DrumNoteIdentifier.class, NoteGridButtonIdentifier.class
    });

    /**
     * @see ModPacketHandler#getValidIdentifier(String, List)
     */
    public static Class<? extends NoteButtonIdentifier> getValidIdentifier(String classType)
            throws ClassNotFoundException {
        return getValidIdentifier(classType, ACCEPTABLE_IDENTIFIERS);
    }

    /**
     * Gets a {@link NoteButtonIdentifier} as described by the {@code classType} destination.
     * Will only return a class type if it is valid and included in the {@code acceptableIdentifiers} list.
     * @param classType The class name of the requested identifiers
     * @param acceptableIdentifiers
     * 
     * @return The class of the requested identifier
     * @throws ClassNotFoundException If the requested class was not found in the provided {@code acceptableIdentifiers} list
     */
    public static Class<? extends NoteButtonIdentifier> getValidIdentifier(String classType,
            List<Class<? extends NoteButtonIdentifier>> acceptableIdentifiers) throws ClassNotFoundException {

        for (final Class<? extends NoteButtonIdentifier> identifier : acceptableIdentifiers) {
            if (identifier.getName().equals(classType))
                return identifier;
        }

        throw new ClassNotFoundException("Class type "+classType+" could not be evaluated as part of the acceptable identifiers");
    }


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
