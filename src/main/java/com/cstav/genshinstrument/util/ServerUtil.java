package com.cstav.genshinstrument.util;

import java.util.List;

import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.packets.instrument.PlayNotePacket;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.core.BlockPos;
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

    
    public static void sendPlayNotePackets(ServerPlayer player, InteractionHand hand, NoteSound sound) {
        for (final Player listener : noteListeners(player.level, player.blockPosition()))
            ModPacketHandler.sendToClient(
                new PlayNotePacket(player.blockPosition(), sound, player.getUUID()), (ServerPlayer)listener
            );

        // Trigger an instrument game event
        // This is done so that sculk sensors can pick up the instrument's sound
        player.level.gameEvent(
            GameEvent.INSTRUMENT_PLAY, player.blockPosition(),
            GameEvent.Context.of(player)
        );

        MinecraftForge.EVENT_BUS.post(new InstrumentPlayedEvent.ByPlayer(sound, player, hand));
    }
    public static void sendPlayNotePackets(final Level level, final BlockPos pos, final NoteSound sound) {
        for (final Player listener : noteListeners(level, pos))
            ModPacketHandler.sendToClient(
                new PlayNotePacket(pos, sound, null), (ServerPlayer)listener
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
    private static List<Player> noteListeners(Level level, BlockPos pos) {
        return CommonUtil.getPlayersInArea(level,
            new AABB(pos).inflate(PLAY_DISTANCE)
        );
    }

}
