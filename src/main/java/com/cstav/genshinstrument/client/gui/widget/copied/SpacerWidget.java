package com.cstav.genshinstrument.client.gui.widget.copied;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpacerWidget extends AbstractWidget {
   public SpacerWidget(int pWidth, int pHeight) {
      this(0, 0, pWidth, pHeight);
   }

   public SpacerWidget(int pX, int pY, int pWidth, int pHeight) {
      super(pX, pY, pWidth, pHeight, TextComponent.EMPTY);
   }

   public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
   }

   public boolean changeFocus(boolean pFocus) {
      return false;
   }

   public static AbstractWidget width(int pWidth) {
      return new SpacerWidget(pWidth, 0);
   }

   public static AbstractWidget height(int pHeight) {
      return new SpacerWidget(0, pHeight);
   }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
        // throw new UnsupportedOperationException("Unimplemented method 'updateNarration'");
    }
}