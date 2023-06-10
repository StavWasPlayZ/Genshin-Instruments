package com.cstav.genshinstrument.util;

import java.util.List;
import java.util.Optional;

import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.packets.instrument.PlayNotePacket;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;

public class ServerUtil {
    public static final int PLAY_DISTANCE = 16;

    
    /**
     * Sends {@link PlayNotePacket}s in the specified {@link ServerUtil#PLAY_DISTANCE}.
     * This method treats the sound as it was produced by a player.
     * @param player The player producing the sounds
     * @param hand The hand of the player producing the sounds
     * @param sound The sound tp initiate
     * @param pitch The pitch of the sound to initiate
     */
    public static void sendPlayNotePackets(ServerPlayer player, InteractionHand hand,
      NoteSound sound, ResourceLocation instrumentId, float pitch) {
        for (final Player listener : noteListeners(player.level(), player.blockPosition()))
            ModPacketHandler.sendToClient(
                new PlayNotePacket(
                    player.blockPosition(), sound, pitch, instrumentId,
                    Optional.of(player.getUUID()), Optional.of(hand)
                ),
                (ServerPlayer)listener
            );

        // Trigger an instrument game event
        // This is done so that sculk sensors can pick up the instrument's sound
        player.level().gameEvent(
            GameEvent.INSTRUMENT_PLAY, player.blockPosition(),
            GameEvent.Context.of(player)
        );

        MinecraftForge.EVENT_BUS.post(new InstrumentPlayedEvent.ByPlayer(sound, player, hand, instrumentId, false));
    }

    /**
     * Sends {@link PlayNotePacket}s in the specified {@link ServerUtil#PLAY_DISTANCE}.
     * This method treats the sound as it was NOT produced by a player.
     * @param level The world that the sound should initiate in
     * @param pos The position of the sound to initiate
     * @param sound The sound to initiate
     * @param pitch The pitch of the sound to initiate
     */
    public static void sendPlayNotePackets(Level level, BlockPos pos, NoteSound sound, ResourceLocation instrumentId, float pitch) {
        for (final Player listener : noteListeners(level, pos))
            ModPacketHandler.sendToClient(
                new PlayNotePacket(
                    pos, sound, pitch, instrumentId,
                    Optional.empty(), Optional.empty()
                ),
                (ServerPlayer)listener
            );


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


        MinecraftForge.EVENT_BUS.post(new InstrumentPlayedEvent(sound, (ServerLevel)level, pos, instrumentId, false));
    }


    private static List<Player> noteListeners(Level level, BlockPos pos) {
        return CommonUtil.getPlayersInArea(level,
            new AABB(pos).inflate(PLAY_DISTANCE)
        );
    }

}
