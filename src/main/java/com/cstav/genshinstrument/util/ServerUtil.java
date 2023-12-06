package com.cstav.genshinstrument.util;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.networking.IModPacket;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.OpenInstrumentPacketSender;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.instrument.NotifyInstrumentOpenPacket;
import com.cstav.genshinstrument.networking.packet.instrument.OpenInstrumentPacket;
import com.cstav.genshinstrument.networking.packet.instrument.PlayNotePacket;
import com.cstav.genshinstrument.sound.NoteSound;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ServerUtil {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int PLAY_DISTANCE = 16;

    
    /**
     * Sends {@link PlayNotePacket}s in the specified {@link ServerUtil#PLAY_DISTANCE}.
     * This method treats the sound as it was produced by a player.
     * @param player The player producing the sounds
     * @param sound The sound to initiate
     * @param instrumentId The ID of the instrument initiating the sound
     * @param pitch The pitch of the sound to initiate
     * @param volume The volume of the sound to initiate
     */
    public static void sendPlayNotePackets(ServerPlayer player,
            NoteSound sound, ResourceLocation instrumentId, int pitch, int volume) {

        sendPlayNotePackets(
            player, Optional.empty(),
            sound, instrumentId, null,
            pitch, volume,
            PlayNotePacket::new
        );
    }
    /**
     * Sends {@link PlayNotePacket}s in the specified {@link ServerUtil#PLAY_DISTANCE}.
     * This method treats the sound as it was produced by a player.
     * @param player The player producing the sounds
     * @param pos The position of the sound being produced
     * @param sound The sound to initiate
     * @param instrumentId The ID of the instrument initiating the sound
     * @param noteIdentifier The identifier of the note
     * @param pitch The pitch of the sound to initiate
     * @param volume The volume of the sound to initiate
     * @param notePacketDelegate The initiator of the {@link PlayNotePacket} to be sent
     */
    public static void sendPlayNotePackets(ServerPlayer player, Optional<BlockPos> pos,
            NoteSound sound, ResourceLocation instrumentId, NoteButtonIdentifier noteIdentifier, int pitch, int volume,
            PlayNotePacketDelegate notePacketDelegate) {

        final PlayNotePacket packet = notePacketDelegate.create(
            pos, sound, pitch, volume,
            instrumentId, Optional.ofNullable(noteIdentifier),
            Optional.of(player.getUUID())
        );

        final BlockPos playeredPos = CommonUtil.getPlayeredPosition(player, pos);

        for (final Player listener : noteListeners(player.level(), playeredPos))
            ModPacketHandler.sendToClient(packet, (ServerPlayer)listener);


        // Trigger an instrument game event
        // This is done so that sculk sensors can pick up the instrument's sound
        player.level().gameEvent(
            GameEvent.INSTRUMENT_PLAY, playeredPos,
            GameEvent.Context.of(player)
        );

        MinecraftForge.EVENT_BUS.post(
            new InstrumentPlayedEvent.ByPlayer(
                sound, pitch, volume,
                player, playeredPos,
                instrumentId, noteIdentifier,
                false
            )
        );
    }

    /**
     * Sends {@link PlayNotePacket}s in the specified {@link ServerUtil#PLAY_DISTANCE}.
     * This method treats the sound as it was NOT produced by a player.
     * @param level The world that the sound should initiate in
     * @param pos The position of the sound to initiate
     * @param sound The sound to initiate
     * @param instrumentId The ID of the instrument initiating the sound
     * @param pitch The pitch of the sound to initiate
     */
    public static void sendPlayNotePackets(Level level, BlockPos pos, NoteSound sound, ResourceLocation instrumentId,
            int pitch, int volume) {
        sendPlayNotePackets(
            level, pos, sound,
            instrumentId, null, pitch, volume,
            PlayNotePacket::new
        );
    }
    /**
     * Sends {@link PlayNotePacket}s in the specified {@link ServerUtil#PLAY_DISTANCE}.
     * This method treats the sound as it was NOT produced by a player.
     * @param level The world that the sound should initiate in
     * @param pos The position of the sound to initiate
     * @param sound The sound to initiate
     * @param instrumentId The ID of the instrument initiating the sound
     * @param noteIdentifier The identifier of the note
     * @param pitch The pitch of the sound to initiate
     * @param volume The volume of the sound to initiate
     * @param notePacketDelegate The initiator of the {@link PlayNotePacket} to be sent
     */
    public static void sendPlayNotePackets(Level level, BlockPos pos, NoteSound sound,
            ResourceLocation instrumentId, NoteButtonIdentifier noteIdentifier, int pitch, int volume,
            PlayNotePacketDelegate notePacketDelegate) {

        final PlayNotePacket packet = notePacketDelegate.create(
            Optional.of(pos), sound, pitch, volume,
            instrumentId, Optional.ofNullable(noteIdentifier),
            Optional.empty()
        );

        for (final Player listener : noteListeners(level, pos))
            ModPacketHandler.sendToClient(packet, (ServerPlayer)listener);


        final BlockState bs = level.getBlockState(pos);
        // The sound may have been coming from a block
        if (bs != Blocks.AIR.defaultBlockState())
            level.gameEvent(
                GameEvent.INSTRUMENT_PLAY, pos,
                GameEvent.Context.of(bs)
            );
        // idk what else
        else
            level.gameEvent(null, GameEvent.INSTRUMENT_PLAY, pos);


        MinecraftForge.EVENT_BUS.post(
            new InstrumentPlayedEvent(sound, pitch, volume, level, pos, instrumentId, noteIdentifier, false)
        );
    }


    private static List<Player> noteListeners(Level level, BlockPos pos) {
        return CommonUtil.getPlayersInArea(level,
            new AABB(pos).inflate(PLAY_DISTANCE)
        );
    }


    /* ------------------ */


    public static void setInstrumentClosed(final Player player) {
        // Update the capability on server
        InstrumentOpenProvider.setClosed(player);

        // And clients
        player.level().players().forEach((nearbyPlayer) ->
            ModPacketHandler.sendToClient(
                new NotifyInstrumentOpenPacket(player.getUUID()),
                (ServerPlayer)nearbyPlayer
            )
        );
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
    public static Class<? extends NoteButtonIdentifier> getValidNoteIdentifier(String classType,
            List<Class<? extends NoteButtonIdentifier>> acceptableIdentifiers) throws ClassNotFoundException {

        for (final Class<? extends NoteButtonIdentifier> identifier : acceptableIdentifiers) {
            if (identifier.getName().equals(classType))
                return identifier;
        }

        throw new ClassNotFoundException("Class type "+classType+" could not be evaluated as part of the acceptable identifiers");
    }


    // Item/block stuff
    /**
     * Sends an instrument open packet as an item
     */
    public static boolean sendOpenPacket(ServerPlayer player, InteractionHand usedHand, OpenInstrumentPacketSender onOpenRequest) {
        return sendOpenPacket(player, usedHand, onOpenRequest, null);
    }
    /**
     * Sends an instrument open packet as a block
     */
    public static boolean sendOpenPacket(ServerPlayer player, OpenInstrumentPacketSender onOpenRequest, BlockPos pos) {
        return sendOpenPacket(player, null, onOpenRequest, pos);
    }
    private static boolean sendOpenPacket(ServerPlayer player, InteractionHand usedHand, OpenInstrumentPacketSender onOpenRequest,
            BlockPos pos) {

        NotifyInstrumentOpenPacket instrumentOpenPacket;

        // Update the capability on the server
        if (pos == null) {
            InstrumentOpenProvider.setOpen(player, usedHand);
            instrumentOpenPacket = new NotifyInstrumentOpenPacket(player.getUUID(), usedHand);
        } else {
            InstrumentOpenProvider.setOpen(player, pos);
            instrumentOpenPacket = new NotifyInstrumentOpenPacket(player.getUUID(), pos);
        }

        player.level().players().forEach((otherPlayer) ->
            ModPacketHandler.sendToClient(
                instrumentOpenPacket,
                (ServerPlayer)otherPlayer
            )
        );

        // Send open packet after everyone is aware of the state
        onOpenRequest.send(player);
        return true;
    }

    /**
     * @apiNote This method should only be used by the internal Genshin Instruments mod!
     */
    public static void sendInternalOpenPacket(ServerPlayer player, String instrumentType) {
        ModPacketHandler.sendToClient(new OpenInstrumentPacket(instrumentType), player);
    }


    public static void registerModPackets(SimpleChannel sc, List<Class<IModPacket>> acceptablePackets, Supplier<Integer> id) {
        for (final Class<IModPacket> packetType : acceptablePackets)
            try {
                
                sc.messageBuilder(packetType, id.get(), getDirection(packetType))
                    .encoder(IModPacket::write)
                    .decoder((buf) -> {
                        try {
                            return packetType.getDeclaredConstructor(FriendlyByteBuf.class).newInstance(buf);
                        } catch (Exception e) {
                            LOGGER.error("Error constructing packet of type "+packetType.getName(), e);
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
