package com.cstav.genshinstrument.networking.packet.instrument.util;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.networking.GIPacketHandler;
import com.cstav.genshinstrument.networking.OpenInstrumentPacketSender;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.NotifyInstrumentOpenPacket;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.OpenInstrumentPacket;
import com.cstav.genshinstrument.util.CommonUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.List;

/**
 * A helper for sending Genshin Instrument packets
 */
public class InstrumentPacketUtil {
    public static final int PLAY_DISTANCE = 24;


    /**
     * @return The list of Note Sound listeners in the provided area
     */
    public static List<Player> noteListeners(Level level, BlockPos pos) {
        return CommonUtil.getPlayersInArea(level,
            new AABB(pos).inflate(PLAY_DISTANCE)
        );
    }


    public static void setInstrumentClosed(final Player player) {
        // Update the capability on server
        InstrumentOpenProvider.setClosed(player);

        // And clients
        player.level().players().forEach((nearbyPlayer) ->
            GIPacketHandler.sendToClient(
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
            GIPacketHandler.sendToClient(
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
    @Internal
    public static void sendInternalOpenPacket(ServerPlayer player, String instrumentType) {
        GIPacketHandler.sendToClient(new OpenInstrumentPacket(instrumentType), player);
    }
}
