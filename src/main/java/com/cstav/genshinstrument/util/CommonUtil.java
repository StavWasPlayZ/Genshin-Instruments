package com.cstav.genshinstrument.util;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public abstract class CommonUtil {
    /**
     * @return What the default level shouldve returned, but without any conditions
     */
    public static List<PlayerEntity> getPlayersInArea(final World world, final Box area) {
        final List<PlayerEntity> list = Lists.newArrayList();

        for(PlayerEntity player : world.getPlayers())
            if (area.contains(player.getX(), player.getY(), player.getZ()))
                list.add(player);

        return list;
    }
    
    /**
     * @param dir The directory location at which to grab the specified resource
     * @param path The desired path to obtain from the {@code dir}
     * @return The resource contained in the specified directory
     */
    public static Identifier getResourceFrom(final Identifier dir, final String path) {
        return new Identifier(
            dir.getNamespace(),
            dir.getPath() + "/" + path
        );
    }
}
