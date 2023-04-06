package com.cstav.genshinstrument.sounds;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.gui.screens.lyre.LyreScreen;

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

    public static RegistryObject<SoundEvent> register(final String name, final String locPrefix) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, name)));
    }

    private static final String LYRE_NOTES_NAME_PREFIX = "lyre_note_";
    public static final RegistryObject<SoundEvent>[] LYRE_NOTE_SOUNDS = lyreNotes();


    @SuppressWarnings("unchecked")
    private static RegistryObject<SoundEvent>[] lyreNotes() {
        final RegistryObject<SoundEvent>[] sounds = new RegistryObject[LyreScreen.ROWS * LyreScreen.COLUMNS];

        for (int i = 0; i < sounds.length; i++)
            sounds[i] = register(LYRE_NOTES_NAME_PREFIX+i, "lyre");

        return sounds;
    }


}
