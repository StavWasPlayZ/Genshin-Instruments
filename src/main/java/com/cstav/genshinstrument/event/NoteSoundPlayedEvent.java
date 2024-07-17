package com.cstav.genshinstrument.event;

import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Cancelable;

import java.util.Optional;

/**
 * An event fired when a {@link NoteSound} has been produced.
 * This event is fired on the Forge event bus
 */
@Cancelable
public class NoteSoundPlayedEvent extends InstrumentPlayedEvent<NoteSound> {
    public NoteSoundPlayedEvent(Level level, NoteSound sound, NoteSoundMetadata soundMeta) {
        super(level, sound, soundMeta);
    }

    @Cancelable
    public static class ByPlayer extends NoteSoundPlayedEvent implements IByPlayer<NoteSoundPlayedEvent> {
        private final Optional<InteractionHand> hand;
        private final Player player;

        public ByPlayer(Player player, NoteSound sound, NoteSoundMetadata soundMeta) {
            super(player.level(), sound, soundMeta);
            this.player = player;
            hand = parseHand(getPlayer());
        }

        @Override
        public Player getPlayer() {
            return player;
        }
        @Override
        public Optional<InteractionHand> getHand() {
            return hand;
        }
    }
}
