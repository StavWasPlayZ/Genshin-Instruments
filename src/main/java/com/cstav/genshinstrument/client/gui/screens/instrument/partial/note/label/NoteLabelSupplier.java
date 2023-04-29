package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;

import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A functional interface for supplying a label for a note button
 */
@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface NoteLabelSupplier {
    public static final NoteLabelSupplier EMPTY = (note) -> Component.empty();

    /**
     * @param note The button to compute the label for
     * @return The label that should be associated with the given {@code note}
     */
    public Component get(final NoteButton note);
}
