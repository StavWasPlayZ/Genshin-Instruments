package com.cstav.genshinstrument.event;

import com.cstav.genshinstrument.block.partial.InstrumentBlockEntity;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
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
    private final Optional<ByEntityArgs> entityInfo;


    /**
     * Constructor for creating a non-player event
     */
    public InstrumentPlayedEvent(Level level, T sound, NoteSoundMetadata soundMeta) {
        this.level = level;
        this.sound = sound;
        this.soundMeta = soundMeta;

        this.entityInfo = Optional.empty();
    }

    /**
     * Constructor for creating a by-entity event
     */
    public InstrumentPlayedEvent(Entity entity, T sound, NoteSoundMetadata soundMeta) {
        this.level = entity.level();
        this.sound = sound;
        this.soundMeta = soundMeta;

        this.entityInfo = Optional.of(new ByEntityArgs(entity));
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
    public Optional<ByEntityArgs> entityInfo() {
        return entityInfo;
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
    public class ByEntityArgs {
        public final Entity entity;

        /**
         * The hand carrying the <b>item</b> instrument.
         * Empty for when not played by an instrument
         * or is not a player.
         */
        public final Optional<InteractionHand> hand;

        protected final InstrumentPlayedEvent<T> baseEvent = InstrumentPlayedEvent.this;

        public ByEntityArgs(Entity entity) {
            this.entity = entity;

            if (
                (entity instanceof Player player)
                && (InstrumentOpenProvider.isItem(player))
            ) {
                hand = Optional.of(InstrumentOpenProvider.getHand(player));
            } else {
                hand = Optional.empty();
            }
        }

        /**
         * <p>Returns whether this event was fired by an item instrument.</p>
         * A {@code false} result does NOT indicate a block instrument.
         * @see ByEntityArgs#isBlockInstrument
         */
        public boolean isItemInstrument() {
            return hand.isPresent();
        }
        /**
         * <p>Returns whether this event was fired by a block instrument.</p>
         * A {@code false} result does NOT indicate an instrument item.
         * @see ByEntityArgs#isItemInstrument()
         */
        public boolean isBlockInstrument() {
            return !isItemInstrument()
                && entity.level().getBlockEntity(baseEvent.soundMeta.pos())
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
