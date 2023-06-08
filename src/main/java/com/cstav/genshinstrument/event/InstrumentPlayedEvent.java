package com.cstav.genshinstrument.event;

import javax.annotation.Nullable;

import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

/**
 * An event that fires when an instrument produces a sound.
 * This event is fired on the server-side Forge event bus
 */
public class InstrumentPlayedEvent extends Event {

    public final NoteSound sound;
    public final ServerLevel level;
    public final BlockPos pos;

    public InstrumentPlayedEvent(NoteSound sound, final ServerLevel level, final BlockPos pos) {
        this.sound = sound;
        this.level = level;
        this.pos = pos;
    }


    public static final class ByPlayer extends InstrumentPlayedEvent {
        public final ServerPlayer player;
        public final InteractionHand hand;
        public final ItemStack instrument;

        public ByPlayer(NoteSound sound, ServerPlayer player, @Nullable InteractionHand hand) {
            super(sound, player.serverLevel(), player.blockPosition());
            this.player = player;
            this.hand = hand;
    
            instrument = (hand == null) ? null : player.getItemInHand(hand);
        }
    }
    
}
