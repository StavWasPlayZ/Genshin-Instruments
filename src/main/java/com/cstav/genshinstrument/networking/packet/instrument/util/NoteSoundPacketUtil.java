package com.cstav.genshinstrument.networking.packet.instrument.util;

import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.networking.GIPacketHandler;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.PlayNotePacket;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.util.CommonUtil;
import com.cstav.genshinstrument.util.PlayNotePacketDelegate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.Optional;

/**
 * A helper class for dealing with {@link NoteSound} packets.
 */
public class NoteSoundPacketUtil {

    /**
     * Sends {@link PlayNotePacket}s in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
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
     * Sends {@link PlayNotePacket}s in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
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

        for (final Player listener : InstrumentPacketUtil.noteListeners(player.level(), playeredPos))
            GIPacketHandler.sendToClient(packet, (ServerPlayer)listener);


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
                instrumentId, noteIdentifier
            )
        );
    }

    /**
     * Sends {@link PlayNotePacket}s in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
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
     * Sends {@link PlayNotePacket}s in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
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

        for (final Player listener : InstrumentPacketUtil.noteListeners(level, pos))
            GIPacketHandler.sendToClient(packet, (ServerPlayer)listener);


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
            new InstrumentPlayedEvent(sound, pitch, volume, level, pos, instrumentId, noteIdentifier)
        );
    }

}
