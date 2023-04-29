package com.cstav.genshinstrument.client.gui.screens.options.instrument;

import java.awt.Color;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.config.enumType.ZitherSoundType;
import com.cstav.genshinstrument.client.gui.screens.instrument.floralzither.FloralZitherScreen;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.GridWidget;
import net.minecraft.client.gui.components.GridWidget.RowHelper;
import net.minecraft.client.gui.components.SpacerWidget;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FloralZitherOptionsScreen extends GridInstrumentOptionsScreen {
    private static final String SOUND_TYPE_KEY = "button.genshinstrument.zither.soundType";

    public FloralZitherOptionsScreen(final FloralZitherScreen screen) {
        super(screen);
    }

    private ZitherSoundType perferredSoundType = ModClientConfigs.ZITHER_SOUND_TYPE.get();
    public ZitherSoundType getPerferredSoundType() {
        return perferredSoundType;
    }


    private final int spaceBefore = 55, spacerHeight = 10;
    private int heightBefore;
    @Override
    protected void initOptionsGrid(GridWidget grid, RowHelper rowHelper) {
        super.initOptionsGrid(grid, rowHelper);
        rowHelper.addChild(SpacerWidget.height(spacerHeight), 2);
        heightBefore = grid.getHeight();

        final CycleButton<ZitherSoundType> soundTypeButton = CycleButton.<ZitherSoundType>builder((type) ->
            Component.translatable(SOUND_TYPE_KEY+"."+type.toString().toLowerCase())
        )
            .withValues(ZitherSoundType.values())
            .withInitialValue(getPerferredSoundType())
            .create(0, 0,
                getBigButtonWidth(), getButtonHeight()
            , Component.translatable(SOUND_TYPE_KEY), this::onSoundTypeChange);

        rowHelper.addChild(soundTypeButton, 2, rowHelper.newCellSettings().paddingTop(30));

        grid.pack();
    }
    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (!active)
            return;

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        drawCenteredString(pPoseStack, font,
            Component.translatable("button.genshinstrument.zither_options"),
            width/2, heightBefore + spaceBefore + spacerHeight
        , Color.WHITE.getRGB());
    }

    private void onSoundTypeChange(final CycleButton<ZitherSoundType> btn, final ZitherSoundType soundType) {
        if ((screen != null) && (screen instanceof FloralZitherScreen))
            ((FloralZitherScreen)screen).noteGrid.setNoteSounds(soundType.soundArr().get());

        queueToSave("zither_sound_type", () -> ModClientConfigs.ZITHER_SOUND_TYPE.set(soundType));
    }
}
