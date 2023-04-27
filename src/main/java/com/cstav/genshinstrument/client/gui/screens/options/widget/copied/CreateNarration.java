package com.cstav.genshinstrument.client.gui.screens.options.widget.copied;

import java.util.function.Supplier;

import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface CreateNarration {
    MutableComponent createNarrationMessage(Supplier<MutableComponent> p_253695_);
}