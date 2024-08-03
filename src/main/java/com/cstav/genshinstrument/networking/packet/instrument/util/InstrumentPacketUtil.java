package com.cstav.genshinstrument.networking.packet.instrument.util;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.event.InstrumentOpenStateChangedEvent;
import com.cstav.genshinstrument.networking.GIPacketHandler;
import com.cstav.genshinstrument.networking.OpenInstrumentPacketSender;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.NotifyInstrumentOpenPacket;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.OpenInstrumentPacket;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.S2CNotePacket;
import com.cstav.genshinstrument.util.CommonUtil;
import net.minecraft.core.BlockPos;
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
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.List;
import java.util.Optional;

/**
 * A helper for sending Genshin Instrument packets.
 * Server-side.
 */
public class InstrumentPacketUtil {
    public static final int PLAY_DISTANCE = 24;

    //#region Generic Note Sound impls

    /**
     * Sends play note packets in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
     * @param initiator The player producing the sounds
     * @param sound The sound to initiate
     * @param soundMeta Additional metadata of the used sound
     * @param notePacketDelegate The constructor of the sound packet to be sent
     * @param <T> The sound object type
     */
    public static <T> void sendPlayerPlayNotePackets(ServerPlayer initiator, T sound, NoteSoundMetadata soundMeta,
                                                 S2CNotePacketDelegate<T> notePacketDelegate) {

        final S2CNotePacket<T> packet = notePacketDelegate.create(
            Optional.of(initiator.getId()), sound, soundMeta
        );

        for (final Player listener : InstrumentPacketUtil.noteListeners(initiator.level(), soundMeta.pos()))
            GIPacketHandler.sendToClient(packet, (ServerPlayer)listener);


        // Trigger an instrument game event
        // This is done so that sculk sensors can pick up the instrument's sound
        initiator.level().gameEvent(
            GameEvent.INSTRUMENT_PLAY, soundMeta.pos(),
            GameEvent.Context.of(initiator)
        );
    }
    /**
     * Sends play note packets in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
     * @param initiator The player producing the sounds
     * @param sound The sound to initiate
     * @param instrumentId The ID of the instrument initiating the sound
     * @param pitch The pitch of the sound to initiate
     * @param volume The volume of the sound to initiate
     */
    public static <T> void sendPlayerPlayNotePackets(ServerPlayer initiator,
                                                     T sound, ResourceLocation instrumentId, int pitch, int volume,
                                                     S2CNotePacketDelegate<T> notePacketDelegate) {
        sendPlayerPlayNotePackets(
            initiator, sound, new NoteSoundMetadata(
                initiator.blockPosition(),
                pitch, volume,
                instrumentId,
                Optional.empty()
            ),
            notePacketDelegate
        );
    }

    /**
     * Sends play note packets in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
     * This method treats the sound as it was NOT produced by a player.
     * @param level The world that the sound should initiate in
     * @param sound The sound to initiate
     * @param soundMeta Additional metadata of the used sound
     * @param notePacketDelegate The constructor of the sound packet to be sent
     * @param <T> The sound object type
     */
    public static <T> void sendPlayNotePackets(Level level, T sound, NoteSoundMetadata soundMeta,
                                               S2CNotePacketDelegate<T> notePacketDelegate) {

        final S2CNotePacket<T> packet = notePacketDelegate.create(
            Optional.empty(), sound, soundMeta
        );

        for (final Player listener : InstrumentPacketUtil.noteListeners(level, soundMeta.pos()))
            GIPacketHandler.sendToClient(packet, (ServerPlayer)listener);


        final BlockState bs = level.getBlockState(soundMeta.pos());
        // The sound may have been coming from a block
        if (bs != Blocks.AIR.defaultBlockState())
            level.gameEvent(
                GameEvent.INSTRUMENT_PLAY, soundMeta.pos(),
                GameEvent.Context.of(bs)
            );
            // idk what else
        else
            level.gameEvent(null, GameEvent.INSTRUMENT_PLAY, soundMeta.pos());;
    }
    /**
     * Sends play note packets in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
     * This method treats the sound as it was NOT produced by a player.
     * @param level The world that the sound should initiate in
     * @param pos The position of the sound to initiate
     * @param sound The sound to initiate
     * @param instrumentId The ID of the instrument initiating the sound
     * @param pitch The pitch of the sound to initiate
     */
    public static <T> void sendPlayNotePackets(Level level, BlockPos pos, T sound, ResourceLocation instrumentId,
                                               int pitch, int volume,
                                               S2CNotePacketDelegate<T> notePacketDelegate) {
        sendPlayNotePackets(
            level, sound, new NoteSoundMetadata(
                pos,
                pitch, volume,
                instrumentId,
                Optional.empty()
            ),
            notePacketDelegate
        );
    }


    /**
     * @return The list of Note Sound listeners in the provided area
     */
    public static List<Player> noteListeners(Level level, BlockPos pos) {
        return CommonUtil.getPlayersInArea(level,
            new AABB(pos).inflate(PLAY_DISTANCE)
        );
    }

    //#endregion
    /*-----------------*/


    public static void setInstrumentClosed(final Player player) {
        // No need to go through the hassle if it's already closed
        if (!InstrumentOpenProvider.isOpen(player))
            return;

        // Update the capability on server
        InstrumentOpenProvider.setClosed(player);

        // And clients
        player.level().players().forEach((oPlayer) ->
            GIPacketHandler.sendToClient(
                new NotifyInstrumentOpenPacket(player.getUUID()),
                (ServerPlayer)oPlayer
            )
        );

        // Fire server event
        MinecraftForge.EVENT_BUS.post(new InstrumentOpenStateChangedEvent(false, player,
            Optional.ofNullable(InstrumentOpenProvider.getBlockPos(player)),
            Optional.ofNullable(InstrumentOpenProvider.getHand(player))
        ));
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
            GIPacketHandler.sendToClient(
                instrumentOpenPacket,
                (ServerPlayer)otherPlayer
            )
        );

        // Fire server-side event
        MinecraftForge.EVENT_BUS.post(new InstrumentOpenStateChangedEvent(true, player,
            (pos == null) ? Optional.empty() : Optional.of(pos),
            (pos == null) ? Optional.of(usedHand) : Optional.empty()
        ));

        // Send open packet after everyone is aware of the state
        onOpenRequest.send(player);
        return true;
    }

    /**
     * @apiNote This method should only be used by the internal Genshin Instruments mod!
     */
    @Internal
    public static void sendInternalOpenPacket(ServerPlayer player, String instrumentType) {
        GIPacketHandler.sendToClient(new OpenInstrumentPacket(instrumentType), player);
    }
}
