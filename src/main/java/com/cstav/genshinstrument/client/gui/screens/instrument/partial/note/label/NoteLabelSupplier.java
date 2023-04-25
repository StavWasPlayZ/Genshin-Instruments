package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;

import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface NoteLabelSupplier {
    public static final NoteLabelSupplier EMPTY = (note) -> Component.empty();

    public Component get(final NoteButton note);
}
