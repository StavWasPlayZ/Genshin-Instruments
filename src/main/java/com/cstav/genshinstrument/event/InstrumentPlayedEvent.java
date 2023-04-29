package com.cstav.genshinstrument.event;

import com.cstav.genshinstrument.sounds.NoteSound;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

public class InstrumentPlayedEvent extends Event {
    
    public final NoteSound sound;
    public final ServerPlayer player;
    public InstrumentPlayedEvent(final ServerPlayer player, final NoteSound sound) {
        this.player = player;
        this.sound = sound;
    }
    
}
