package com.cstav.genshinstrument.event;

import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

/**
 * An event that fires when an instrument produces a sound.
 * This event is fired on the Forge event bus.
 */
public class InstrumentPlayedEvent extends Event {
    
    public final NoteSound sound;
    public final ServerPlayer player;
    public InstrumentPlayedEvent(final ServerPlayer player, final NoteSound sound) {
        this.player = player;
        this.sound = sound;
    }
    
}
