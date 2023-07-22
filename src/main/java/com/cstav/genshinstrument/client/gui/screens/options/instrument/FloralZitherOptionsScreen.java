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
    private final static int SPACE_BEFORE = 30, SPACER_HEIGHT = 13;

    public FloralZitherOptionsScreen(final FloralZitherScreen screen) {
        super(screen);
    }

    private ZitherSoundType perferredSoundType = ModClientConfigs.ZITHER_SOUND_TYPE.get();
    public ZitherSoundType getPerferredSoundType() {
        return perferredSoundType;
    }


    private int heightBefore;

    @Override
    protected void initOptionsGrid(GridWidget grid, RowHelper rowHelper) {
        super.initOptionsGrid(grid, rowHelper);
        
        rowHelper.addChild(SpacerWidget.height(SPACER_HEIGHT), 2);
        grid.pack();
        heightBefore = grid.getHeight();

        final CycleButton<ZitherSoundType> soundTypeButton = CycleButton.<ZitherSoundType>builder((type) ->
            Component.translatable(SOUND_TYPE_KEY+"."+type.toString().toLowerCase())
        )
            .withValues(ZitherSoundType.values())
            .withInitialValue(getPerferredSoundType())
            .create(0, 0,
                getBigButtonWidth(), getButtonHeight()
            , Component.translatable(SOUND_TYPE_KEY), this::onSoundTypeChange);

        rowHelper.addChild(soundTypeButton, 2);
    }

    @Override
    public void render(PoseStack stack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(stack, pMouseX, pMouseY, pPartialTick);
        
        drawCenteredString(stack, font,
            Component.translatable("label.genshinstrument.zither_options"),
            width/2, heightBefore + SPACE_BEFORE
        , Color.WHITE.getRGB());
    }

    private void onSoundTypeChange(final CycleButton<ZitherSoundType> btn, final ZitherSoundType soundType) {
        if ((instrumentScreen != null) && (instrumentScreen instanceof FloralZitherScreen))
            ((FloralZitherScreen)instrumentScreen).noteGrid.setNoteSounds(soundType.soundArr().get());

        queueToSave("zither_sound_type", () -> ModClientConfigs.ZITHER_SOUND_TYPE.set(soundType));
    }
}
