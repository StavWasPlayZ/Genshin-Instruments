package com.cstav.genshinstrument.event;

import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * An event fired when an instrument sound has been produced.
 * This event is fired on the Forge event bus
 */
@Cancelable
public class InstrumentPlayedEvent extends Event {

    public final NoteSound sound;
    public final Level level;
    public final boolean isClientSide;

    public final ResourceLocation instrumentId;
    public final BlockPos pos;
    

    public InstrumentPlayedEvent(NoteSound sound, Level level, BlockPos pos, ResourceLocation instrumentId, boolean isClientSide) {
        this.sound = sound;
        this.level = level;
        this.pos = pos;
        this.isClientSide = isClientSide;
        this.instrumentId = instrumentId;

        // Handle provided invalid id
        if (!ForgeRegistries.ITEMS.containsKey(instrumentId))
            setCanceled(true);
    }

    @Cancelable
    public static class ByPlayer extends InstrumentPlayedEvent {
        public final Player player;
        /** The instrument held by the player who initiated the sound */
        public final ItemStack instrument;
        public final InteractionHand hand;

        public ByPlayer(NoteSound sound, Player player, InteractionHand hand, ResourceLocation instrumentId, boolean isClientSide) {
            super(sound, player.level(), player.blockPosition(), instrumentId, isClientSide);
            this.player = player;
            this.hand = hand;

            instrument = (hand == null) ? null : player.getItemInHand(hand);

            // Handle provided unmatching id
            if (!instrumentId.equals(ForgeRegistries.ITEMS.getKey(instrument.getItem())))
                setCanceled(true);
        }
    }
    
}
