package com.cstav.genshinstrument.client.util;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.widget.copied.GridWidget;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Lazy;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class ClientUtil {
    public static final int GRID_HORZ_PADDING = 4, GRID_VERT_PADDING = 2;
    /**
     * The range from which players will stop hearing Minecraft's background music on playing
     */
    public static final double STOP_SOUND_DISTANCE = 10;


    /**
     * Stops Minecraft's music if the client desires it and
     * {@code playDistSqr} < {@link ClientUtil#STOP_SOUND_DISTANCE}^2
     * @param playDistSqr The distance of the played sound from the player
     * @return Whether the music stopped
     */
    public static boolean stopMusicIfClose(final double playDistSqr) {
        if (ModClientConfigs.STOP_MUSIC_ON_PLAY.get() && (playDistSqr < Mth.square(STOP_SOUND_DISTANCE))) {
            Minecraft.getInstance().getMusicManager().stopPlaying();
            return true;
        }

        return false;
    }
    /**
     * Stops Minecraft's music if the client desires it and
     * the player's distance is lesser than the provided position.
     * @param pos The position to check the distance from
     * @return Whether the music stopped
     */
    public static boolean stopMusicIfClose(final BlockPos pos) {
        return stopMusicIfClose(pos.distToCenterSqr(Minecraft.getInstance().player.position()));
    }


    public static final Lazy<Boolean> ON_QWERTY = Lazy.of(() -> {
        final String qwerty = "QWERTY";
        final Key[] keyRow = InstrumentKeyMappings.GRID_INSTRUMENT_MAPPINGS[0];

        // Assuming there will be more than 6 entries here
        for (int i = 0; i < qwerty.length(); i++) {
            if (!charEquals(qwerty.charAt(i), keyRow[i].getDisplayName().getString(1).charAt(0)))
                return false;
        }

        return true;
    });
    private static boolean charEquals(final char char1, final char char2) {
        return Character.toLowerCase(char1) == Character.toLowerCase(char2);
    }

    
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

    public static GridWidget createSettingsGrid() {
        final GridWidget grid = new GridWidget();
        grid.defaultCellSetting()
            .padding(GRID_HORZ_PADDING, GRID_VERT_PADDING)
            .alignVertically(.5f)
            .alignHorizontallyCenter();

        return grid;
    }

    public static void alignGrid(GridWidget grid, int screenWidth, int screenHeight) {
        grid.pack();
        grid.setX((screenWidth - grid.getWidth())/2);
        grid.setY(30);
        grid.pack();
    }

    public static int lowerButtonsY(int desiredY, int desiredHeight, int screenHeight) {
        return Math.min(desiredY + desiredHeight + 50, screenHeight - 20 - 15);
    }
}
