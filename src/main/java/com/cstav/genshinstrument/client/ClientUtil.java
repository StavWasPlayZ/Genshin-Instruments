package com.cstav.genshinstrument.client;

import java.awt.Color;
import java.awt.Point;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientUtil {
    public static final int GRID_HORZ_PADDING = 4, GRID_VERT_PADDING = 2;

    
    /**
     * @return The point in the center of the described widget
     */
    public static Point getInitCenter(int initX, int initY, int initSize, int currSize) {
        return new Point(
            (initSize - currSize) / 2 + initX,
            (initSize - currSize) / 2 + initY
        );
    }

    public static void setShaderColor(final Color color, final float alpha) {
        RenderSystem.setShaderColor(
            color.getRed() / 255f,
            color.getGreen() / 255f,
            color.getBlue() / 255f,
            alpha
        );
    }
    public static void setShaderColor(final Color color) {
        setShaderColor(color, 1);
    }
    public static void resetShaderColor() {
        setShaderColor(Color.WHITE);
    }


    /**
     * Sets the render system's texture shader as the specified resource
     */
    public static void displaySprite(final ResourceLocation location) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, location);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
    }

    public static GridLayout createSettingsGrid() {
        final GridLayout grid = new GridLayout();
        grid.defaultCellSetting()
            .padding(GRID_HORZ_PADDING, GRID_VERT_PADDING)
            .alignVertically(.5f)
            .alignHorizontallyCenter();

        return grid;
    }

    public static void alignGrid(Layout layout, int screenWidth, int screenHeight) {
        layout.arrangeElements();
        FrameLayout.alignInRectangle(layout, 0, 0, screenWidth, screenHeight, 0.5f, 0);
        layout.setY(30);
        layout.arrangeElements();
    }

    public static int lowerButtonsY(int desiredY, int desiredHeight, int screenHeight) {
        return Math.min(desiredY + desiredHeight + 50, screenHeight - 20 - 15);
    }
}
