package com.cstav.genshinstrument.client.keyMaps;

import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
// @EventBusSubscriber(modid = Main.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class KeyMappings {
    
    // Literally only doing this for key translations
    public static final Key[][] INSTRUMENT_MAPPINGS = createInstrumentMaps();

    private static Key[][] createInstrumentMaps() {
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

}
