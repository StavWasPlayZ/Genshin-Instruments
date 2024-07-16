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

    @Cancelable
    public static abstract class ByPlayer<T> extends InstrumentPlayedEvent<T> {
        public final Player player;

        // The value below will only be supplied if initiated from an item
        /** The hand holding the instrument played by this player */
        public final Optional<InteractionHand> hand;

        /**
         * <p>Returns whether this event was fired by an item instrument.</p>
         * A {@code false} result does NOT indicate a block instrument.
         * @see ByPlayer#isBlockInstrument
         */
        public boolean isItemInstrument() {
            return hand.isPresent();
        }
        /**
         * <p>Returns whether this event was fired by a block instrument.</p>
         * A {@code false} result does NOT indicate an instrument item.
         * @see ByPlayer#isItemInstrument()
         */
        public boolean isBlockInstrument() {
            return !isItemInstrument()
                && player.level().getBlockEntity(soundMeta.pos()) instanceof InstrumentBlockEntity;
        }

        /**
         * @return Whether the played sound was not produced by an instrument
         */
        public boolean isNotInstrument() {
            return !isBlockInstrument() && !isItemInstrument();
        }


        public ByPlayer(Player player, T sound, NoteSoundMetadata soundMeta) {
            super(player.level(), sound, soundMeta);

            this.player = player;

            if (InstrumentOpenProvider.isItem(player)) {
                this.hand = Optional.of(InstrumentOpenProvider.getHand(player));
            } else {
                this.hand = Optional.empty();
            }
        }
    }
    
}
