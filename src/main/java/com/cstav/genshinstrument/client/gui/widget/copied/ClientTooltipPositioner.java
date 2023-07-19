package com.cstav.genshinstrument.client.gui.widget.copied;

import org.joml.Vector2ic;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ClientTooltipPositioner {
   Vector2ic positionTooltip(Screen pScreen, int pMouseX, int pMouseY, int pWidth, int pHeight);
}