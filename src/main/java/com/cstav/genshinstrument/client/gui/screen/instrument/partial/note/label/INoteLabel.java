package com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.label;


import com.cstav.genshinstrument.client.util.ClientUtil;
import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * An interface holding {@link NoteLabelSupplier}s for note buttons to cycle thorugh.
 */
public interface INoteLabel {
    // Useful stuff
    public static final String TRANSLATABLE_PATH = "genshinstrument.label.";
    public static final String BUTTON_TRANS_PATH = "button.genshinstrument.label.";

    public static MutableComponent upperComponent(final Component component) {
        return Component.literal(component.getString().toUpperCase());
    }


    public static MutableComponent getQwerty(final Key key) {
        final String keyName = key.getName();
        return Component.literal(
            // The QWERTY key is the last letter of the key name
            String.valueOf(keyName.charAt(keyName.length() - 1)).toUpperCase()
        );
    }

    /**
     * @return All the values of this note label type, filtering QWERTY if already using it.
     */
    public static INoteLabel[] filterQwerty(INoteLabel[] values, INoteLabel currentLabel, INoteLabel qwerty) {
        // Ignore QWERTY if already using this layout
        // Or if the user already selected it
        if (!ClientUtil.ON_QWERTY.get() || (currentLabel.equals(qwerty)))
            return values;


        final INoteLabel[] result = new INoteLabel[values.length - 1];

        // 2nd index to not go out of bounds
        int j = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(qwerty))
                i++;

            result[j] = values[i];
            j++;
        }

        return result;
    }


    public NoteLabelSupplier getLabelSupplier();
    /**
     * @return The translation key of this label
     */
    public default String getKey() {
        return BUTTON_TRANS_PATH + toString().toLowerCase();
    }
}
