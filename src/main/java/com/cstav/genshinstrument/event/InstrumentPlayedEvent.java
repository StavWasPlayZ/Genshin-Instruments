package com.cstav.genshinstrument.event;

import com.cstav.genshinstrument.block.partial.InstrumentBlockEntity;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.util.Optional;

/**
 * An abstract implementation of a sound played event.
 * @param <T> The sound object type
 */
@Cancelable
public abstract class InstrumentPlayedEvent<T> extends Event {

    private final T sound;
    private final NoteSoundMetadata soundMeta;
    private final Level level;

    /**
     * Information about the player initiator.
     * Present if there is indeed a player initiator.
     */
    private final Optional<ByPlayerArgs> playerInfo;


    /**
     * Constructor for creating a non-player event
     */
    public InstrumentPlayedEvent(Level level, T sound, NoteSoundMetadata soundMeta) {
        this.level = level;
        this.sound = sound;
        this.soundMeta = soundMeta;

        this.playerInfo = Optional.empty();
    }

    /**
     * Constructor for creating a by-player event
     */
    public InstrumentPlayedEvent(Player player, T sound, NoteSoundMetadata soundMeta) {
        this.level = player.level();
        this.sound = sound;
        this.soundMeta = soundMeta;

        this.playerInfo = Optional.of(new ByPlayerArgs(player));
    }


    public T sound() {
        return sound;
    }
    public NoteSoundMetadata soundMeta() {
        return soundMeta;
    }
    public Level level() {
        return level;
    }
    public Optional<ByPlayerArgs> playerInfo() {
        return playerInfo;
    }

    /**
     * Convenience method to convert the volume of the note
     * into a {@code float} percentage
     */
    public float volume() {
        return soundMeta.volume() / 100f;
    }


    /**
     * An object containing information
     * about the player who initiated the event
     */
    public class ByPlayerArgs {
        public final Player player;
        public final Optional<InteractionHand> hand;

        protected final InstrumentPlayedEvent<T> baseEvent = InstrumentPlayedEvent.this;

        public ByPlayerArgs(Player player) {
            this.player = player;

            if (InstrumentOpenProvider.isItem(player)) {
                hand = Optional.of(InstrumentOpenProvider.getHand(player));
            } else {
                hand = Optional.empty();
            }
        }

        /**
         * <p>Returns whether this event was fired by an item instrument.</p>
         * A {@code false} result does NOT indicate a block instrument.
         * @see ByPlayerArgs#isBlockInstrument
         */
        public boolean isItemInstrument() {
            return hand.isPresent();
        }
        /**
         * <p>Returns whether this event was fired by a block instrument.</p>
         * A {@code false} result does NOT indicate an instrument item.
         * @see ByPlayerArgs#isItemInstrument()
         */
        public boolean isBlockInstrument() {
            return !isItemInstrument()
                && player.level().getBlockEntity(baseEvent.soundMeta.pos())
                    instanceof InstrumentBlockEntity;
        }

        /**
         * @return Whether the played sound was not produced by an instrument
         */
        public boolean isNotInstrument() {
            return !isBlockInstrument() && !isItemInstrument();
        }
    }

}
