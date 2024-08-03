package com.cstav.genshinstrument.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

import java.util.Optional;

/**
 * An event when the open state of an instrument screen has changed.
 */
@OnlyIn(Dist.CLIENT)
public class InstrumentOpenStateChangedEvent extends Event {
    public final boolean isOpen;
    public final Player player;

    /**
     * The position of the block instrument
     */
    public final Optional<BlockPos> pos;
    /**
     * The hand of the item instrument
     */
    public final Optional<InteractionHand> hand;

    public InstrumentOpenStateChangedEvent(boolean isOpen, Player player, Optional<BlockPos> pos, Optional<InteractionHand> hand) {
        this.isOpen = isOpen;
        this.player = player;
        this.pos = pos;
        this.hand = hand;
    }
}
