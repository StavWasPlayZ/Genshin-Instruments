package com.cstav.genshinstrument.event;

import javax.annotation.Nullable;

import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

/**
 * An event that fires when an instrument produces a sound.
 * This event is fired on the Forge event bus, on the logical server.
 */
public class InstrumentPlayedEvent extends Event {
    
    public final NoteSound sound;
    public final ServerPlayer player;
    /**
     * The hand where the instrument is at.
     * Null for when the sound was not produced by a player.
     */
    @Nullable
    public final InteractionHand hand;

    /**
     * The value of {@code player.getItemInHand(hand)}.
     * Null for when the sound was not produced by a player.
     */
    @Nullable
    public final ItemStack instrument;

    public InstrumentPlayedEvent(ServerPlayer player, NoteSound sound, @Nullable InteractionHand hand) {
        this.player = player;
        this.sound = sound;
        this.hand = hand;

        instrument = (hand == null) ? null : player.getItemInHand(hand);
    }
    
}
