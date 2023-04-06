package com.cstav.genshinstrument.client.gui.screens.instrument.label;

import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface NoteLabelSupplier {
    Component get(final int noteRow, final int noteColumn);
}
