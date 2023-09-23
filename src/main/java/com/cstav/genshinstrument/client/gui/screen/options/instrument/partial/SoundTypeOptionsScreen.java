package com.cstav.genshinstrument.client.gui.screen.options.instrument.partial;

import java.awt.Color;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.config.enumType.SoundType;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.AbstractGridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.GridInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.util.TogglablePedalSound;
import com.cstav.genshinstrument.event.MidiEvent;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.GridLayout.RowHelper;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * A subclass of {@link GridInstrumentOptionsScreen} that implements a button to cycle through an instrument's sounds
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = Bus.FORGE, modid = GInstrumentMod.MODID, value = Dist.CLIENT)
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
    public void setPerferredSoundType(T perferredSoundType) {
        this.perferredSoundType = perferredSoundType;

        // Update the sound for this instrument
        if (isValidForSet(instrumentScreen))
            instrumentScreen.setNoteSounds(perferredSoundType.getSoundArr().get());
    }

    
    protected abstract T getInitSoundType();
    protected abstract T[] values();

    protected abstract String soundTypeButtonKey();
    protected abstract String optionsLabelKey();


    private int heightBefore;

    @Override
    protected void initOptionsGrid(GridLayout grid, RowHelper rowHelper) {
        super.initOptionsGrid(grid, rowHelper);
        
        rowHelper.addChild(SpacerElement.height(SPACER_HEIGHT), 2);
        grid.arrangeElements();
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
        
        GuiComponent.drawCenteredString(stack, font,
            Component.translatable(optionsLabelKey()),
            width/2, heightBefore + SPACE_BEFORE
        , Color.WHITE.getRGB());
    }

    
    protected void onSoundTypeChange(final CycleButton<T> btn, final T soundType) {
        setPerferredSoundType(soundType);

        queueToSave(instrumentScreen.getInstrumentId().getPath()+"_sound_type", () -> saveSoundType(soundType));
    }

    protected abstract void saveSoundType(final T soundType);

    protected abstract boolean isValidForSet(final AbstractInstrumentScreen screen);



    /* ----------- MIDI Pedal Behaviour ----------- */

    /**
     * @return The sounds that should be used upon MIDI pedal events by this instrument. Null for none.
     */
    public TogglablePedalSound<T> midiPedalListener() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void onMidiRecievedEvent(final MidiEvent event) {
        final AbstractInstrumentScreen instrumentScreen = AbstractInstrumentScreen.getCurrentScreen(Minecraft.getInstance()).orElse(null);
        
        if ((instrumentScreen == null)
            || !(instrumentScreen.optionsScreen instanceof SoundTypeOptionsScreen optionsScreen)
        ) return;

        final var pedalSounds = optionsScreen.midiPedalListener();
        if (optionsScreen.midiPedalListener() == null)
            return;


        final byte[] message = event.message.getMessage();

        // Only listen for pedal events
        // Check 80 too bc FreePiano
        if (((message[0] != -80) && (message[0] != -176)) || (message[1] != 64))
            return;


        //NOTE: I did not test this on an actual pedal, this value might need to be flipped
        optionsScreen.setPerferredSoundType((message[2] >= 64) ? pedalSounds.enabled : pedalSounds.disabled);
    }
    
}
