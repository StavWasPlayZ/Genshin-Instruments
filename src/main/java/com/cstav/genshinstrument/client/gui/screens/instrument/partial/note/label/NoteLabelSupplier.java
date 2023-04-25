package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label;

import java.util.function.Supplier;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;

import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface NoteLabelSupplier {
    public static final NoteLabelSupplier EMPTY = create(() -> (note) -> Component.empty());

    /**
     * Creates a new common-sided NoteLabelSupplier.
     * Its result will always return null if {@link FMLEnvironment#dist} is not set to {@link Dist#CLIENT}.
     * @param sup A supplier that holds the label supplier
     */
    public static NoteLabelSupplier create(final Supplier<NoteLabelSupplier> sup) {
        return (FMLEnvironment.dist == Dist.CLIENT)
            ? (note) -> sup.get().get(note)
            : null
        ;
    }

    public Component get(final NoteButton note);
}
