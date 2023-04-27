package com.cstav.genshinstrument.client.gui.screens.options.widget.copied;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FrameWidget extends AbstractContainerWidget {
   private final List<FrameWidget.ChildContainer> children = new ArrayList<>();
   private final List<AbstractWidget2> containedChildrenView = Collections.unmodifiableList(Lists.transform(this.children, (p_254331_) -> {
      return p_254331_.child;
   }));
   private int minWidth;
   private int minHeight;
   private final LayoutSettings defaultChildLayoutSettings = LayoutSettings.defaults().align(0.5F, 0.5F);

   public static FrameWidget withMinDimensions(int pMinWidth, int pMinHeight) {
      return (new FrameWidget(0, 0, 0, 0)).setMinDimensions(pMinWidth, pMinHeight);
   }

   public FrameWidget() {
      this(0, 0, 0, 0);
   }

   public FrameWidget(int pX, int pY, int pWidth, int pHeight) {
      super(pX, pY, pWidth, pHeight, Component.empty());
   }

   public FrameWidget setMinDimensions(int pMinWidth, int pMinHeight) {
      return this.setMinWidth(pMinWidth).setMinHeight(pMinHeight);
   }

   public FrameWidget setMinHeight(int pMinHeight) {
      this.minHeight = pMinHeight;
      return this;
   }

   public FrameWidget setMinWidth(int pMinWidth) {
      this.minWidth = pMinWidth;
      return this;
   }

   public LayoutSettings newChildLayoutSettings() {
      return this.defaultChildLayoutSettings.copy();
   }

   public LayoutSettings defaultChildLayoutSetting() {
      return this.defaultChildLayoutSettings;
   }

   public void pack() {
      int i = this.minWidth;
      int j = this.minHeight;

      for(FrameWidget.ChildContainer framewidget$childcontainer : this.children) {
         i = Math.max(i, framewidget$childcontainer.getWidth());
         j = Math.max(j, framewidget$childcontainer.getHeight());
      }

      for(FrameWidget.ChildContainer framewidget$childcontainer1 : this.children) {
         framewidget$childcontainer1.setX(this.x, i);
         framewidget$childcontainer1.setY(this.y, j);
      }

      this.width = i;
      this.height = j;
   }

   public <T extends AbstractWidget2> T addChild(T pChild) {
      return this.addChild(pChild, this.newChildLayoutSettings());
   }

   public <T extends AbstractWidget2> T addChild(T pChild, LayoutSettings pLayoutSettings) {
      this.children.add(new FrameWidget.ChildContainer(pChild, pLayoutSettings));
      return pChild;
   }

   protected List<AbstractWidget2> getContainedChildren() {
      return this.containedChildrenView;
   }

   public static void centerInRectangle(AbstractWidget2 pWidget, int pX, int pY, int pWidth, int pHeight) {
      alignInRectangle(pWidget, pX, pY, pWidth, pHeight, 0.5F, 0.5F);
   }

   public static void alignInRectangle(AbstractWidget2 pWidget, int pX, int pY, int pWidth, int pHeight, float pHorizontalAlignment, float pVerticalAlignment) {
      alignInDimension(pX, pWidth, pWidget.getWidth(), pWidget::setX, pHorizontalAlignment);
      alignInDimension(pY, pHeight, pWidget.getHeight(), pWidget::setY, pVerticalAlignment);
   }

   public static void alignInDimension(int pMin, int pMax, int pSize, Consumer<Integer> pSetPosition, float pAlignment) {
      int i = (int)Mth.lerp(pAlignment, 0.0F, (float)(pMax - pSize));
      pSetPosition.accept(pMin + i);
   }

   @OnlyIn(Dist.CLIENT)
   static class ChildContainer extends AbstractContainerWidget.AbstractChildWrapper {
      protected ChildContainer(AbstractWidget2 p_254443_, LayoutSettings p_254403_) {
         super(p_254443_, p_254403_);
      }
   }

@Override
public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'updateNarration'");
}
}