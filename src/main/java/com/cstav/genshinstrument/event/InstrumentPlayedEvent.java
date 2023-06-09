package com.cstav.genshinstrument.event;

import javax.annotation.Nullable;

import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;

/**
 * An event that fires when an instrument produces a sound.
 * This event is fired on the server-side Forge event bus
 */
public class InstrumentPlayedEvent extends Event {

    public final NoteSound sound;
    public final Level level;
    public final BlockPos pos;
    public final boolean isClientSide;

    public InstrumentPlayedEvent(NoteSound sound, Level level, BlockPos pos, boolean isClientSide) {
        this.sound = sound;
        this.level = level;
        this.pos = pos;
        this.isClientSide = isClientSide;
    }


    public static final class ByPlayer extends InstrumentPlayedEvent {
        public final Player player;
        public final @Nullable InteractionHand hand;
        public final ItemStack instrument;

        public ByPlayer(NoteSound sound, Player player, @Nullable InteractionHand hand, boolean isClientSide) {
            super(sound, player.level(), player.blockPosition(), isClientSide);
            this.player = player;
            this.hand = hand;
    
            instrument = (hand == null) ? null : player.getItemInHand(hand);
        }
    }
    
}
