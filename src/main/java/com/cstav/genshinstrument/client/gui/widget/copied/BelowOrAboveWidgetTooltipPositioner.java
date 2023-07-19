package com.cstav.genshinstrument.client.gui.widget.copied;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector2i;
import org.joml.Vector2ic;

@OnlyIn(Dist.CLIENT)
public class BelowOrAboveWidgetTooltipPositioner implements ClientTooltipPositioner {
   private final AbstractWidget widget;

   public BelowOrAboveWidgetTooltipPositioner(AbstractWidget pWidget) {
      this.widget = pWidget;
   }

   public Vector2ic positionTooltip(Screen pScreen, int pMouseX, int pMouseY, int pWidth, int pHeight) {
      Vector2i vector2i = new Vector2i();
      vector2i.x = this.widget.x + 3;
      vector2i.y = this.widget.y + this.widget.getHeight() + 3 + 1;
      if (vector2i.y + pHeight + 3 > pScreen.height) {
         vector2i.y = this.widget.y - pHeight - 3 - 1;
      }

      if (vector2i.x + pWidth > pScreen.width) {
         vector2i.x = Math.max(this.widget.x + this.widget.getWidth() - pWidth - 3, 4);
      }

      return vector2i;
   }
}