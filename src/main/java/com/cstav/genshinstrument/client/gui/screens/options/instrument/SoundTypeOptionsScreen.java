package com.cstav.genshinstrument.client.gui.screens.options.instrument;

import java.awt.Color;

import com.cstav.genshinstrument.client.config.enumType.SoundType;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.AbstractGridInstrumentScreen;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.GridWidget;
import net.minecraft.client.gui.components.SpacerWidget;
import net.minecraft.client.gui.components.GridWidget.RowHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A subclass of {@link GridInstrumentOptionsScreen} that implements a button to cycle through an instrument's sounds
 */
@OnlyIn(Dist.CLIENT)
public abstract class SoundTypeOptionsScreen<T extends SoundType> extends GridInstrumentOptionsScreen {
    private final static int SPACE_BEFORE = 20, SPACER_HEIGHT = 13;

    public SoundTypeOptionsScreen(final AbstractGridInstrumentScreen screen) {
        super(screen);
    }
    public SoundTypeOptionsScreen(final Screen lastScreen) {
        super(lastScreen);
    }

    private T perferredSoundType = getInitSoundType();
    public T getPerferredSoundType() {
        return perferredSoundType;
    }

    protected abstract T getInitSoundType();
    protected abstract T[] values();

    protected abstract String soundTypeButtonKey();
    protected abstract String optionsLabelKey();


    private int heightBefore;

    @Override
    protected void initOptionsGrid(GridWidget grid, RowHelper rowHelper) {
        super.initOptionsGrid(grid, rowHelper);
        
        rowHelper.addChild(SpacerWidget.height(SPACER_HEIGHT), 2);
        grid.pack();
        heightBefore = grid.getHeight();

        final CycleButton<T> soundTypeButton = CycleButton.<T>builder((type) ->
            Component.translatable(soundTypeButtonKey()+"."+type.toString().toLowerCase())
        )
            .withValues(values())
            .withInitialValue(getPerferredSoundType())
            .create(0, 0,
                getBigButtonWidth(), getButtonHeight()
            , Component.translatable(soundTypeButtonKey()), this::onSoundTypeChange);

        rowHelper.addChild(soundTypeButton, 2);
    }

    @Override
    public void render(PoseStack stack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(stack, pMouseX, pMouseY, pPartialTick);
        
        drawCenteredString(stack, font,
            Component.translatable(optionsLabelKey()),
            width/2, heightBefore + SPACE_BEFORE
        , Color.WHITE.getRGB());
    }

    
    protected void onSoundTypeChange(final CycleButton<T> btn, final T soundType) {
        if ((instrumentScreen instanceof AbstractGridInstrumentScreen gridInstrument) && isValidForSet(gridInstrument))
            gridInstrument.noteGrid.setNoteSounds(soundType.getSoundArr().get());

        queueToSave("zither_sound_type", () -> {
            perferredSoundType = soundType;
            saveSoundType(soundType);
        });
    }
    protected abstract void saveSoundType(final T soundType);

    protected abstract boolean isValidForSet(final AbstractGridInstrumentScreen screen);
}
