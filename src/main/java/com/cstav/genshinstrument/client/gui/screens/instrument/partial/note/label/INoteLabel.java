package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label;


import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public interface INoteLabel {
    // Useful stuff
    public static final String TRANSLATABLE_PATH = "genshinstrument.label.";
    public static final String BUTTON_TRANS_PATH = "button.genshinstrument.label.";

    public static MutableComponent upperComponent(final Component component) {
        return Component.literal(component.getString().toUpperCase());
    }


    public NoteLabelSupplier getLabelSupplier();
    /**
     * @return The translation key of this label
     */
    public default String getKey() {
        return BUTTON_TRANS_PATH + toString().toLowerCase();
    }

    /**
     * @return All possible values of labels
     */
    public INoteLabel[] getValues();
}
