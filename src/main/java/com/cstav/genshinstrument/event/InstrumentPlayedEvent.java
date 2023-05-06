package com.cstav.genshinstrument.event;

import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

/**
 * An event that fires when an instrument produces a sound.
 * This event is fired on the Forge event bus, on the logical server.
 */
public class InstrumentPlayedEvent extends Event {
    
    public final NoteSound sound;
    public final ServerPlayer player;
    public final ItemStack instrument;

    public InstrumentPlayedEvent(ServerPlayer player, NoteSound sound, ItemStack instrument) {
        this.player = player;
        this.sound = sound;
        this.instrument = instrument;
    }
    
}
