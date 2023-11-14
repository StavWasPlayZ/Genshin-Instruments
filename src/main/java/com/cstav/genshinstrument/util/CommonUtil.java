package com.cstav.genshinstrument.util;

import java.util.List;
import java.util.Optional;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public abstract class CommonUtil {

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getItemInHands(final Class<T> item, final Player player) {
        final Item mainItem = player.getItemInHand(InteractionHand.MAIN_HAND).getItem(),
            offItem = player.getItemInHand(InteractionHand.OFF_HAND).getItem();

        if (item.isInstance(mainItem))
            return Optional.of((T)mainItem);
        else if (item.isInstance(offItem))
            return Optional.of((T)offItem);

        return Optional.empty();
    }
    
    /**
     * @return What the default level shouldve returned, but without any conditions
     */
    public static List<Player> getPlayersInArea(final Level level, final AABB area) {
        final List<Player> list = Lists.newArrayList();

        for (Player player : level.players())
            if (area.contains(player.getX(), player.getY(), player.getZ()))
                list.add(player);

        return list;
    }

    /**
     * Converts the given {@code netPos} to the played position;
     * when said optional is empty, provides either the player's position
     * (if hand-held instrument) or the block's position (block instrument).
     * @param netPos The play position as provided by the network
     */
    public static BlockPos getPlayeredPosition(Player player, Optional<BlockPos> netPos) {
        return netPos.orElseGet(() -> !InstrumentOpenProvider.isItem(player)
            ? InstrumentOpenProvider.getBlockPos(player)
            : player.blockPosition()
        );
    }
    
    
    /**
     * @param dir The directory location at which to grab the specified resource
     * @param path The desired path to obtain from the {@code dir}
     * @return The resource contained in the specified directory
     */
    public static ResourceLocation getResourceFrom(final ResourceLocation dir, final String path) {
        return new ResourceLocation(
            dir.getNamespace(),
            dir.getPath() + "/" + path
        );
    }

    public static ResourceLocation withSuffix(final ResourceLocation resource, final String suffix) {
        return new ResourceLocation(resource.getNamespace(), resource.getPath() + suffix);
    }

    /**
     * Provides a similar behaviour to python's indexing,
     * where negatives are counted backwards.
     */
    public static int pyWrap(int index, final int arrLength) {
        while (index < 0)
            index += arrLength;

        return index;
    }
    /**
     * Wraps the index around an array
     */
    public static int wrapAround(int index, final int arrLength) {
        return index % arrLength;
    }
    /**
     * Performs both {@link LabelUtil#pyWrap} and {@link LabelUtil#wrapAround}
     */
    public static int doublyPyWrap(int index, final int arrLength) {
        return wrapAround(pyWrap(index, arrLength), arrLength);
    }
}
