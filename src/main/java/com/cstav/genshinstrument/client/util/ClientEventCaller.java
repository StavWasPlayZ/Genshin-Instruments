package com.cstav.genshinstrument.client.util;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.SoundTypeOptionsScreen;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings;
import com.cstav.genshinstrument.event.MidiEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * A patch class for all events who previously were annotated with
 * {@literal @}SubscribeEvent, but their parents' annotation collided with
 * {@literal @}EventBusSubscriber and {@literal @}OnlyIn.
 * <p>
 * Necessary since 1.20.6.
 */
@EventBusSubscriber(bus = Bus.FORGE, modid = GInstrumentMod.MODID, value = Dist.CLIENT)
public class ClientEventCaller {

    @SubscribeEvent
    public static void onMidiReceivedEvent(final MidiEvent event) {
        SoundTypeOptionsScreen.onMidiReceivedEvent(event);
    }

    @SubscribeEvent
    public static void registerKeybinds(final RegisterKeyMappingsEvent event) {
        InstrumentKeyMappings.registerKeybinds(event);
    }
    
}
