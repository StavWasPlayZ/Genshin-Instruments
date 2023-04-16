package com.cstav.genshinstrument.client.gui.screens.instrument.partial.label;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.NoteButton;

import net.minecraft.network.chat.Component;

@FunctionalInterface
public interface NoteLabelSupplier {
    public static final NoteLabelSupplier EMPTY = (note) -> Component.empty();

    Component get(final NoteButton note);
}
