package com.cstav.genshinstrument.client.keyMaps;

import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
// @EventBusSubscriber(modid = Main.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class KeyMappings {
    
    // Literally only doing this for key translations
    public static final Key[][] LYRE_MAPPINGS = createLyreMaps();

    private static Key[][] createLyreMaps() {
        final int[][] QWERTY = {
            {81, 87, 69, 82, 84, 89, 85},
            {65, 83, 68, 70, 71, 72, 74},
            {90, 88, 67, 86, 66, 78, 77}
        };
        final int rows = QWERTY[0].length, columns = QWERTY.length;

        final Key[][] result = new Key[columns][rows];
        for (int i = 0; i < columns; i++)
            for (int j = 0; j < rows; j++)
                result[i][j] = Type.KEYSYM.getOrCreate(QWERTY[i][j]);

        return result;
    }
    

    // Due to Mojang complaining that "gUi keY ModIfIErs ConFlIcT iN gaME keYs",
    // I cannot make this happen.
    // @SubscribeEvent
    // public static void registerMappings(final RegisterKeyMappingsEvent event) {
    //     for (int i = 0; i < LYRE_MAPPINGS.length; i++)
    //         for (int j = 0; j < LYRE_MAPPINGS[0].length; j++)
    //             event.register(LYRE_MAPPINGS[i][j].get());
    // }

}
