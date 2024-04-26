package com.cstav.genshinstrument.util;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

public abstract class CommonUtil {
    
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


    /**
     * Provides a similar behaviour to Python's indexing,
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
     * Performs both {@link CommonUtil#pyWrap} and {@link CommonUtil#wrapAround}
     */
    public static int doublyPyWrap(int index, final int arrLength) {
        return wrapAround(pyWrap(index, arrLength), arrLength);
    }


    public static void loadClasses(final Class<?>[] classes) {
        for (final Class<?> loadMe : classes) {

            try {
                Class.forName(loadMe.getName());
            } catch (ClassNotFoundException e) {
                GInstrumentMod.LOGGER.error("Failed to load class "+ loadMe.getSimpleName() +": class not found", e);
            }

        }
    }


    /**
     * @return The given {@code value} rounded by the provided {@code places}.
     */
    public static double round(double value, int places) {
        return BigDecimal.valueOf(value)
            .setScale(places, RoundingMode.HALF_UP)
            .doubleValue();
    }
}
