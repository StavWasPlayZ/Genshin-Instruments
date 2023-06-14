package com.cstav.genshinstrument.client.gui.screens.options.widget.copied;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GridWidget extends AbstractContainerWidget {
   private final List<AbstractWidget> children = new ArrayList<>();
   private final List<GridWidget.CellInhabitant> cellInhabitants = new ArrayList<>();
   private final LayoutSettings defaultCellSettings = LayoutSettings.defaults();

   public GridWidget() {
      this(0, 0);
   }

   public GridWidget(int pX, int pY) {
      this(pX, pY, Component.empty());
   }

   public GridWidget(int pX, int pY, Component pMessage) {
      super(pX, pY, 0, 0, pMessage);
   }

   public void pack() {
      int i = 0;
      int j = 0;

      for(GridWidget.CellInhabitant gridwidget$cellinhabitant : this.cellInhabitants) {
         i = Math.max(gridwidget$cellinhabitant.getLastOccupiedRow(), i);
         j = Math.max(gridwidget$cellinhabitant.getLastOccupiedColumn(), j);
      }

      int[] aint = new int[j + 1];
      int[] aint1 = new int[i + 1];

      for(GridWidget.CellInhabitant gridwidget$cellinhabitant1 : this.cellInhabitants) {
         Divisor divisor = new Divisor(gridwidget$cellinhabitant1.getHeight(), gridwidget$cellinhabitant1.occupiedRows);

         for(int k = gridwidget$cellinhabitant1.row; k <= gridwidget$cellinhabitant1.getLastOccupiedRow(); ++k) {
            aint1[k] = Math.max(aint1[k], divisor.nextInt());
         }

         Divisor divisor1 = new Divisor(gridwidget$cellinhabitant1.getWidth(), gridwidget$cellinhabitant1.occupiedColumns);

         for(int l = gridwidget$cellinhabitant1.column; l <= gridwidget$cellinhabitant1.getLastOccupiedColumn(); ++l) {
            aint[l] = Math.max(aint[l], divisor1.nextInt());
         }
      }

      int[] aint2 = new int[j + 1];
      int[] aint3 = new int[i + 1];
      aint2[0] = 0;

      for(int k1 = 1; k1 <= j; ++k1) {
         aint2[k1] = aint2[k1 - 1] + aint[k1 - 1];
      }

      aint3[0] = 0;

      for(int l1 = 1; l1 <= i; ++l1) {
         aint3[l1] = aint3[l1 - 1] + aint1[l1 - 1];
      }

      for(GridWidget.CellInhabitant gridwidget$cellinhabitant2 : this.cellInhabitants) {
         int i2 = 0;

         for(int i1 = gridwidget$cellinhabitant2.column; i1 <= gridwidget$cellinhabitant2.getLastOccupiedColumn(); ++i1) {
            i2 += aint[i1];
         }

         gridwidget$cellinhabitant2.setX(this.x + aint2[gridwidget$cellinhabitant2.column], i2);
         int j2 = 0;

         for(int j1 = gridwidget$cellinhabitant2.row; j1 <= gridwidget$cellinhabitant2.getLastOccupiedRow(); ++j1) {
            j2 += aint1[j1];
         }

         gridwidget$cellinhabitant2.setY(this.y + aint3[gridwidget$cellinhabitant2.row], j2);
      }

      this.width = aint2[j] + aint[j];
      this.height = aint3[i] + aint1[i];
   }

   public <T extends AbstractWidget> T addChild(T pChild, int pRow, int pColumn) {
      return this.addChild(pChild, pRow, pColumn, this.newCellSettings());
   }

   public <T extends AbstractWidget> T addChild(T pChild, int pRow, int pColumn, LayoutSettings pLayoutSettings) {
      return this.addChild(pChild, pRow, pColumn, 1, 1, pLayoutSettings);
   }

   public <T extends AbstractWidget> T addChild(T pChild, int pRow, int pColumn, int pOccupiedRows, int pOccupiedColumns) {
      return this.addChild(pChild, pRow, pColumn, pOccupiedRows, pOccupiedColumns, this.newCellSettings());
   }

   public <T extends AbstractWidget> T addChild(T pChild, int pRow, int pColumn, int pOccupiedRows, int pOccupiedColumns, LayoutSettings pLayoutSettings) {
      if (pOccupiedRows < 1) {
         throw new IllegalArgumentException("Occupied rows must be at least 1");
      } else if (pOccupiedColumns < 1) {
         throw new IllegalArgumentException("Occupied columns must be at least 1");
      } else {
         this.cellInhabitants.add(new GridWidget.CellInhabitant(pChild, pRow, pColumn, pOccupiedRows, pOccupiedColumns, pLayoutSettings));
         this.children.add(pChild);
         return pChild;
      }
   }

   protected List<? extends AbstractWidget> getContainedChildren() {
      return this.children;
   }

   public LayoutSettings newCellSettings() {
      return this.defaultCellSettings.copy();
   }

   public LayoutSettings defaultCellSetting() {
      return this.defaultCellSettings;
   }

   public GridWidget.RowHelper createRowHelper(int pColumns) {
      return new GridWidget.RowHelper(pColumns);
   }

   @OnlyIn(Dist.CLIENT)
   static class CellInhabitant extends AbstractContainerWidget.AbstractChildWrapper {
      final int row;
      final int column;
      final int occupiedRows;
      final int occupiedColumns;

      CellInhabitant(AbstractWidget pChild, int pRow, int pColumn, int pOccupiedRows, int pOccupiedColumns, LayoutSettings pLayoutSettings) {
         super(pChild, pLayoutSettings.getExposed());
         this.row = pRow;
         this.column = pColumn;
         this.occupiedRows = pOccupiedRows;
         this.occupiedColumns = pOccupiedColumns;
      }

      public int getLastOccupiedRow() {
         return this.row + this.occupiedRows - 1;
      }

      public int getLastOccupiedColumn() {
         return this.column + this.occupiedColumns - 1;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public final class RowHelper {
      private final int columns;
      private int index;

      RowHelper(int pColumns) {
         this.columns = pColumns;
      }

      public <T extends AbstractWidget> T addChild(T pChild) {
         return this.addChild(pChild, 1);
      }

      public <T extends AbstractWidget> T addChild(T pChild, int pOccupiedColumns) {
         return this.addChild(pChild, pOccupiedColumns, this.defaultCellSetting());
      }

      public <T extends AbstractWidget> T addChild(T pChild, LayoutSettings pLayoutSettings) {
         return this.addChild(pChild, 1, pLayoutSettings);
      }

      public <T extends AbstractWidget> T addChild(T pChild, int pOccupiedColumns, LayoutSettings pLayoutSettings) {
         int i = this.index / this.columns;
         int j = this.index % this.columns;
         if (j + pOccupiedColumns > this.columns) {
            ++i;
            j = 0;
            this.index = Mth.roundToward(this.index, this.columns);
         }

         this.index += pOccupiedColumns;
         return GridWidget.this.addChild(pChild, i, j, 1, pOccupiedColumns, pLayoutSettings);
      }

      public LayoutSettings newCellSettings() {
         return GridWidget.this.newCellSettings();
      }

      public LayoutSettings defaultCellSetting() {
         return GridWidget.this.defaultCellSetting();
      }
   }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
        //?
    }
}