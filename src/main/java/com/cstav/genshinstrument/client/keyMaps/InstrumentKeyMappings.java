package com.cstav.genshinstrument.client.keyMaps;

import org.lwjgl.glfw.GLFW;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.AbstractGridInstrumentScreen;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = Bus.MOD, modid = GInstrumentMod.MODID, value = Dist.CLIENT)
public class InstrumentKeyMappings {
    public static final String CATEGORY = GInstrumentMod.MODID+".keymaps";

    public static final IKeyConflictContext INSTRUMENT_KEY_CONFLICT_CONTEXT = new IKeyConflictContext() {

        @SuppressWarnings("resource")
        @Override
        public boolean isActive() {
            return Minecraft.getInstance().screen instanceof AbstractInstrumentScreen;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other) {
            return this == other;
        }

    };

    
    public static final Lazy<KeyMapping> TRANSPOSE_UP_MODIFIER = Lazy.of(
        () -> new KeyMapping(CATEGORY+".transpose_up_modifier",
            INSTRUMENT_KEY_CONFLICT_CONTEXT,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT
        , CATEGORY)
    );
    public static final Lazy<KeyMapping> TRANSPOSE_DOWN_MODIFIER = Lazy.of(
        () -> new KeyMapping(CATEGORY+".transpose_down_modifier",
            INSTRUMENT_KEY_CONFLICT_CONTEXT,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_ALT
        , CATEGORY)
    );


    @SubscribeEvent
    public static void registerKeybinds(final RegisterKeyMappingsEvent event) {
        event.register(TRANSPOSE_UP_MODIFIER.get());
        event.register(TRANSPOSE_DOWN_MODIFIER.get());
    }
    

    /* --------------- Builtin Keys --------------- */

    public static final Key[][] GRID_INSTRUMENT_MAPPINGS = createInstrumentMaps(new int[][] {
        {81, 87, 69, 82, 84, 89, 85, 73},
        {65, 83, 68, 70, 71, 72, 74, 75},
        {90, 88, 67, 86, 66, 78, 77, 44}
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
