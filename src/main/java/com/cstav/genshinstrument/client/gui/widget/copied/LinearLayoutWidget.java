package com.cstav.genshinstrument.client.gui.widget.copied;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LinearLayoutWidget extends AbstractContainerWidget {
   private final LinearLayoutWidget.Orientation orientation;
   private final List<LinearLayoutWidget.ChildContainer> children = new ArrayList<>();
   private final List<AbstractWidget> containedChildrenView = Collections.unmodifiableList(Lists.transform(this.children, (p_254146_) -> {
      return p_254146_.child;
   }));
   private final LayoutSettings defaultChildLayoutSettings = LayoutSettings.defaults();

   public LinearLayoutWidget(int pWidth, int pHeight, LinearLayoutWidget.Orientation pOrientation) {
      this(0, 0, pWidth, pHeight, pOrientation);
   }

   public LinearLayoutWidget(int pX, int pY, int pWidth, int pHeight, LinearLayoutWidget.Orientation pOrientation) {
      super(pX, pY, pWidth, pHeight, Component.empty());
      this.orientation = pOrientation;
   }

   public void pack() {
      if (!this.children.isEmpty()) {
         int i = 0;
         int j = this.orientation.getSecondaryLength(this);

         for(LinearLayoutWidget.ChildContainer linearlayoutwidget$childcontainer : this.children) {
            i += this.orientation.getPrimaryLength(linearlayoutwidget$childcontainer);
            j = Math.max(j, this.orientation.getSecondaryLength(linearlayoutwidget$childcontainer));
         }

         int k = this.orientation.getPrimaryLength(this) - i;
         int l = this.orientation.getPrimaryPosition(this);
         Iterator<LinearLayoutWidget.ChildContainer> iterator = this.children.iterator();
         LinearLayoutWidget.ChildContainer linearlayoutwidget$childcontainer1 = iterator.next();
         this.orientation.setPrimaryPosition(linearlayoutwidget$childcontainer1, l);
         l += this.orientation.getPrimaryLength(linearlayoutwidget$childcontainer1);
         LinearLayoutWidget.ChildContainer linearlayoutwidget$childcontainer2;
         if (this.children.size() >= 2) {
            for(Divisor divisor = new Divisor(k, this.children.size() - 1); divisor.hasNext(); l += this.orientation.getPrimaryLength(linearlayoutwidget$childcontainer2)) {
               l += divisor.nextInt();
               linearlayoutwidget$childcontainer2 = iterator.next();
               this.orientation.setPrimaryPosition(linearlayoutwidget$childcontainer2, l);
            }
         }

         int i1 = this.orientation.getSecondaryPosition(this);

         for(LinearLayoutWidget.ChildContainer linearlayoutwidget$childcontainer3 : this.children) {
            this.orientation.setSecondaryPosition(linearlayoutwidget$childcontainer3, i1, j);
         }

         this.orientation.setSecondaryLength(this, j);
      }
   }

   protected List<? extends AbstractWidget> getContainedChildren() {
      return this.containedChildrenView;
   }

   public LayoutSettings newChildLayoutSettings() {
      return this.defaultChildLayoutSettings.copy();
   }

   public LayoutSettings defaultChildLayoutSetting() {
      return this.defaultChildLayoutSettings;
   }

   public <T extends AbstractWidget> T addChild(T pChild) {
      return this.addChild(pChild, this.newChildLayoutSettings());
   }

   public <T extends AbstractWidget> T addChild(T pChild, LayoutSettings pLayoutSettings) {
      this.children.add(new LinearLayoutWidget.ChildContainer(pChild, pLayoutSettings));
      return pChild;
   }

   @OnlyIn(Dist.CLIENT)
   static class ChildContainer extends AbstractContainerWidget.AbstractChildWrapper {
      protected ChildContainer(AbstractWidget p_253998_, LayoutSettings p_254445_) {
         super(p_253998_, p_254445_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Orientation {
      HORIZONTAL,
      VERTICAL;

      int getPrimaryLength(AbstractWidget pWidget) {
         int i;
         switch (this) {
            case HORIZONTAL:
               i = pWidget.getWidth();
               break;
            case VERTICAL:
               i = pWidget.getHeight();
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         return i;
      }

      int getPrimaryLength(LinearLayoutWidget.ChildContainer pContainer) {
         int i;
         switch (this) {
            case HORIZONTAL:
               i = pContainer.getWidth();
               break;
            case VERTICAL:
               i = pContainer.getHeight();
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         return i;
      }

      int getSecondaryLength(AbstractWidget pWidget) {
         int i;
         switch (this) {
            case HORIZONTAL:
               i = pWidget.getHeight();
               break;
            case VERTICAL:
               i = pWidget.getWidth();
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         return i;
      }

      int getSecondaryLength(LinearLayoutWidget.ChildContainer pContainer) {
         int i;
         switch (this) {
            case HORIZONTAL:
               i = pContainer.getHeight();
               break;
            case VERTICAL:
               i = pContainer.getWidth();
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         return i;
      }

      void setPrimaryPosition(LinearLayoutWidget.ChildContainer pContainer, int pPosition) {
         switch (this) {
            case HORIZONTAL:
               pContainer.setX(pPosition, pContainer.getWidth());
               break;
            case VERTICAL:
               pContainer.setY(pPosition, pContainer.getHeight());
         }

      }

      void setSecondaryPosition(LinearLayoutWidget.ChildContainer pContainer, int pPosition, int pSize) {
         switch (this) {
            case HORIZONTAL:
               pContainer.setY(pPosition, pSize);
               break;
            case VERTICAL:
               pContainer.setX(pPosition, pSize);
         }

      }

      int getPrimaryPosition(AbstractWidget pWidget) {
         int i;
         switch (this) {
            case HORIZONTAL:
               i = pWidget.x;
               break;
            case VERTICAL:
               i = pWidget.y;
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         return i;
      }

      int getSecondaryPosition(AbstractWidget pWidget) {
         int i;
         switch (this) {
            case HORIZONTAL:
               i = pWidget.y;
               break;
            case VERTICAL:
               i = pWidget.x;
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         return i;
      }

      void setSecondaryLength(AbstractWidget pWidget, int pLength) {
         switch (this) {
            case HORIZONTAL:
               pWidget.setHeight(pLength);
               break;
            case VERTICAL:
               pWidget.setWidth(pLength);
         }

      }
   }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
        // pNarrationElementOutput.add(null, null);
    }
}