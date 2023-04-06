package com.cstav.genshinstrument.sounds;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Main.MODID);
    public static void register(final IEventBus bus) {
        SOUNDS.register(bus);
    }

    public static RegistryObject<SoundEvent> register(final String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, name)));
    }


    public static final RegistryObject<SoundEvent>[]
        LYRE_NOTE_SOUNDS = createInstrumentNotes("lyre"),
        ZITHER_NEW_SOUNDS = createInstrumentNotes("zither_new")
    ;


    @SuppressWarnings("unchecked")
    public static RegistryObject<SoundEvent>[] createInstrumentNotes(final String namePrefix) {
        final RegistryObject<SoundEvent>[] sounds = new RegistryObject[AbstractInstrumentScreen.ROWS * AbstractInstrumentScreen.COLUMNS];

        for (int i = 0; i < sounds.length; i++)
            sounds[i] = register(namePrefix+"_note_"+i);

        return sounds;
    }


}
