package com.cstav.genshinstrument.util;

import java.util.List;

import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.packets.instrument.PlayNotePacket;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class ServerUtil {
    public static final int PLAY_DISTANCE = 16;

    
    public static void sendPlayNotePackets(final Player player, final NoteSound sound) {
        final List<Player> listeners = CommonUtil.getPlayersInArea(player.level,
            player.getBoundingBox().inflate(PLAY_DISTANCE)
        );
        for (final Player listener : listeners)
            ModPacketHandler.sendToClient(
                new PlayNotePacket(player.blockPosition(), sound, player.getUUID()), (ServerPlayer)listener
            );
    }
    public static void sendPlayNotePackets(final Level level, final BlockPos pos, final NoteSound sound) {
        final List<Player> listeners = CommonUtil.getPlayersInArea(level,
            new AABB(pos).inflate(PLAY_DISTANCE)
        );
        for (final Player listener : listeners)
            ModPacketHandler.sendToClient(
                new PlayNotePacket(pos, sound, null), (ServerPlayer)listener
            );
    }

}
