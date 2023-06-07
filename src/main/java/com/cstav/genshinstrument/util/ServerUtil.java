package com.cstav.genshinstrument.util;

import java.util.List;

import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.packets.instrument.PlayNotePacket;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.World;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;

public class ServerUtil {
    public static final int PLAY_DISTANCE = 16;

    
    public static void sendPlayNotePackets(ServerPlayerEntity player, InteractionHand hand, NoteSound sound, float pitch) {
        for (final Player listener : noteListeners(player.level, player.blockPosition()))
            ModPacketHandler.sendToClient(
                new PlayNotePacket(player.blockPosition(), sound, pitch, player.getUUID()),
                (ServerPlayerEntity)listener
            );

        // Trigger an instrument game event
        // This is done so that sculk sensors can pick up the instrument's sound
        player.level.gameEvent(
            GameEvent.INSTRUMENT_PLAY, player.blockPosition(),
            GameEvent.Context.of(player)
        );

        MinecraftForge.EVENT_BUS.post(new InstrumentPlayedEvent.ByPlayer(sound, player, hand));
    }
    public static void sendPlayNotePackets(World world, BlockPos pos, NoteSound sound, float pitch) {
        for (final Player listener : noteListeners(level, pos))
            ModPacketHandler.sendToClient(
                new PlayNotePacket(pos, sound, pitch, null), (ServerPlayerEntity)listener
            );

        final BlockState bs = level.getBlockState(pos);
        if (bs != Blocks.AIR.defaultBlockState())
            level.gameEvent(
                GameEvent.INSTRUMENT_PLAY, pos,
                GameEvent.Context.of(bs)
            );
        else
            level.gameEvent(null, GameEvent.INSTRUMENT_PLAY, pos);

        MinecraftForge.EVENT_BUS.post(new InstrumentPlayedEvent(sound, level, pos));
    }
    private static List<Player> noteListeners(World world, BlockPos pos) {
        return CommonUtil.getPlayersInArea(level,
            new AABB(pos).inflate(PLAY_DISTANCE)
        );
    }

}
