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

    public final T sound;
    public final NoteSoundMetadata soundMeta;
    public final Level level;

    /**
     * Convenience method to convert the volume of the note
     * into a {@code float} percentage
     */
    public float volume() {
        return soundMeta.volume() / 100f;
    }
    

    public InstrumentPlayedEvent(Level level, T sound, NoteSoundMetadata soundMeta) {
        this.level = level;
        this.sound = sound;
        this.soundMeta = soundMeta;
    }

    /**
     * An instrument played event variant,
     * specifically when played by the player.
     * @param <T> The base play event
     */
    @Cancelable
    public static interface IByPlayer<T extends InstrumentPlayedEvent<?>> {
        Player getPlayer();
        Optional<InteractionHand> getHand();

        @SuppressWarnings("unchecked")
        default T asPlayedEvent() {
            return (T) this;
        }

        /**
         * <p>Returns whether this event was fired by an item instrument.</p>
         * A {@code false} result does NOT indicate a block instrument.
         * @see IByPlayer#isBlockInstrument
         */
        default boolean isItemInstrument() {
            return getHand().isPresent();
        }
        /**
         * <p>Returns whether this event was fired by a block instrument.</p>
         * A {@code false} result does NOT indicate an instrument item.
         * @see IByPlayer#isItemInstrument()
         */
        default boolean isBlockInstrument() {
            return !isItemInstrument()
                && getPlayer().level().getBlockEntity(asPlayedEvent().soundMeta.pos())
                    instanceof InstrumentBlockEntity;
        }

        /**
         * @return Whether the played sound was not produced by an instrument
         */
        default boolean isNotInstrument() {
            return !isBlockInstrument() && !isItemInstrument();
        }

    }

    /**
     * Parses the required hand to be passed to the
     * hand field for the {@link IByPlayer} variant - and returns it.
     */
    protected static Optional<InteractionHand> parseHand(final Player player) {
        if (InstrumentOpenProvider.isItem(player)) {
            return Optional.of(InstrumentOpenProvider.getHand(player));
        } else {
            return Optional.empty();
        }
    }

}
