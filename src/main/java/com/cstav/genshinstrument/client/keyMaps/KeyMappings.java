package com.cstav.genshinstrument.client.keyMaps;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.AbstractGridInstrumentScreen;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

// Literally only doing this for key translations
@OnlyIn(Dist.CLIENT)
public class KeyMappings {
    
    public static final Key[][] GRID_INSTRUMENT_MAPPINGS = createInstrumentMaps(new int[][] {
        {81, 87, 69, 82, 84, 89, 85},
        {65, 83, 68, 70, 71, 72, 74},
        {90, 88, 67, 86, 66, 78, 77}
    });

    // Glorious drum
    public static final DrumKeys
        DON = new DrumKeys(83, 75),
        KA = new DrumKeys(65, 76)
    ;

    @OnlyIn(Dist.CLIENT)
    public static final class DrumKeys {
        public final Key left, right;

        private DrumKeys(final int left, final int right) {
            this.left = create(left);
            this.right = create(right);
        }
    }



    /**
     * Creates a grid of keys.
     * used by {@link AbstractGridInstrumentScreen} for managing keyboard input.
     * @param keyCodes A 2D array representing a key grid. Each cell should correspond to a note.
     * @return A 2D key array as described in {@code keyCodes}.
     */
    public static Key[][] createInstrumentMaps(final int[][] keyCodes) {
        final int rows = keyCodes[0].length, columns = keyCodes.length;

        final Key[][] result = new Key[columns][rows];
        for (int i = 0; i < columns; i++)
            for (int j = 0; j < rows; j++)
                result[i][j] = create(keyCodes[i][j]);

        return result;
    }

    private static Key create(final int keyCode) {
        return Type.KEYSYM.getOrCreate(keyCode);
    }

}
