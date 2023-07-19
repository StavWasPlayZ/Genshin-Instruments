package com.cstav.genshinstrument.client.gui.screens.options.widget.copied;

import net.minecraft.client.gui.components.Button;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface OnPress {
    void onPress(Button pButton);
}