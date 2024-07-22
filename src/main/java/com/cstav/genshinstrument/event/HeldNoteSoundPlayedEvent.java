package com.cstav.genshinstrument.event;

import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.networking.packet.instrument.util.HeldSoundPhase;
import com.cstav.genshinstrument.sound.held.HeldNoteSound;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * An event fired when a {@link HeldNoteSound} has been produced.
 * This event is fired on the Forge event bus
 */
@Cancelable
public class HeldNoteSoundPlayedEvent extends InstrumentPlayedEvent<HeldNoteSound> {
    public final HeldSoundPhase phase;
    public HeldNoteSoundPlayedEvent(Level level, HeldNoteSound sound, NoteSoundMetadata soundMeta, HeldSoundPhase phase) {
        super(level, sound, soundMeta);
        this.phase = phase;
    }
    public HeldNoteSoundPlayedEvent(Player player, HeldNoteSound sound, NoteSoundMetadata soundMeta, HeldSoundPhase phase) {
        super(player, sound, soundMeta);
        this.phase = phase;
    }
}
