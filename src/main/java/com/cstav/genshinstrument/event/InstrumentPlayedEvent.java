package com.cstav.genshinstrument.event;

import java.util.Optional;

import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * An event fired when an instrument sound has been produced.
 * This event is fired on the Forge event bus
 */
@Cancelable
public class InstrumentPlayedEvent extends Event {

    public final NoteSound sound;
    public final int pitch;

    public final Level level;
    public final boolean isClientSide;
    
    public final ResourceLocation instrumentId;
    public final NoteButtonIdentifier noteIdentifier;
    public final BlockPos pos;
    

    public InstrumentPlayedEvent(NoteSound sound, int pitch, Level level, BlockPos pos,
            ResourceLocation instrumentId, NoteButtonIdentifier noteIdentifier, boolean isClientSide) {

        this.sound = sound;
        this.pitch = pitch;

        this.level = level;
        this.pos = pos;
        this.isClientSide = isClientSide;

        this.instrumentId = instrumentId;
        this.noteIdentifier = noteIdentifier;
    }

    @Cancelable
    public static class ByPlayer extends InstrumentPlayedEvent {
        public final Player player;

        // The values below will only be supplied if initiated from an item

        /** The instrument held by the player who initiated the sound */
        public final Optional<ItemStack> instrument;
        /** The hand holding the instrument played by this player */
        public final Optional<InteractionHand> hand;


        public ByPlayer(NoteSound sound, int pitch, Player player, BlockPos pos, Optional<InteractionHand> hand,
                ResourceLocation instrumentId, NoteButtonIdentifier noteIdentifier, boolean isClientSide) {
            super(sound, pitch, player.level(), pos, instrumentId, noteIdentifier, isClientSide);
            this.player = player;

            if (hand == null) {
                instrument = Optional.empty();
                this.hand = Optional.empty();
            } else {
                this.hand = hand;
                instrument = Optional.of((hand == null) ? null : player.getItemInHand(hand.get()));
            }
        }
    }
    
}
