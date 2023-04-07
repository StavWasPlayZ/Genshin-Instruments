package com.cstav.genshinstrument.client.gui.screens.instrument.partial.label;

import net.minecraft.network.chat.Component;

@FunctionalInterface
public interface NoteLabelSupplier {
    Component get(final int noteRow, final int noteColumn);
}
