package com.cstav.genshinstrument.util;

import java.util.List;

import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.packets.instrument.PlayNotePacket;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ServerUtil {
    public static final int PLAY_DISTANCE = 16;

    
    public static void sendPlayNotePackets(final ServerPlayer player, final NoteSound sound) {
        final List<Player> listeners = CommonUtil.getPlayersInArea(player.level,
            player.getBoundingBox().inflate(PLAY_DISTANCE)
        );
        for (final Player listener : listeners)
            ModPacketHandler.sendToClient(
                new PlayNotePacket(player.blockPosition(), sound, player.getUUID()), (ServerPlayer)listener
            );
    }

}
