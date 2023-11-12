package com.cstav.genshinstrument.client.gui.screen.options.instrument.partial;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.config.enumType.SoundType;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.AbstractGridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.GridInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.util.TogglablePedalSound;
import com.cstav.genshinstrument.event.MidiEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
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
public abstract class SoundTypeOptionsScreen<T extends SoundType> extends SingleButtonOptionsScreen {

    public SoundTypeOptionsScreen(final AbstractGridInstrumentScreen screen) {
        super(screen);
    }
    public SoundTypeOptionsScreen(final Screen lastScreen) {
        super(lastScreen);
    }


    private T preferredSoundType = getInitSoundType();

    public T getPreferredSoundType() {
        return preferredSoundType;
    }
    public void setPreferredSoundType(T preferredSoundType) {
        this.preferredSoundType = preferredSoundType;

        // Update the sound for this instrument
        if (isValidForSet(instrumentScreen))
            instrumentScreen.setNoteSounds(preferredSoundType.getSoundArr().get());
    }

    protected abstract T getInitSoundType();
    protected abstract T[] values();

    protected abstract String soundTypeButtonKey();


    @Override
    protected AbstractButton constructButton() {
        return CycleButton.<T>builder((type) ->
                    Component.translatable(soundTypeButtonKey()+"."+type.toString().toLowerCase())
            )
            .withValues(values())
            .withInitialValue(getPreferredSoundType())
            .create(0, 0,
                getBigButtonWidth(), getButtonHeight(),
                Component.translatable(soundTypeButtonKey()),
                this::onSoundTypeChange
            );

        rowHelper.addChild(soundTypeButton, 2);
    }

    @Override
    public void render(GuiGraphics gui, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(gui, pMouseX, pMouseY, pPartialTick);
        
        gui.drawCenteredString(font,
            Component.translatable(optionsLabelKey()),
            width/2, heightBefore + SPACE_BEFORE
        , Color.WHITE.getRGB());
    }


    protected void onSoundTypeChange(final CycleButton<T> btn, final T soundType) {
        setPreferredSoundType(soundType);

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
    public static void onMidiReceivedEvent(final MidiEvent event) {
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
        optionsScreen.setPreferredSoundType((message[2] >= 64) ? pedalSounds.enabled : pedalSounds.disabled);
    }

}
