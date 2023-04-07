package com.cstav.genshinstrument.item;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.packets.lyre.OpenInstrumentPacket;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * An item responsible for opening an {@link AbstractInstrumentScreen}.
 */
public class InstrumentItem extends Item {

    protected final ServerPlayerRunnable onOpenRequest;
    /**
     * @param onOpenRequest A server-side event fired when the player has requested to interact
     * with the instrument.
     * It should should send a packet to the given player for opening this instrument's screen.
     */
    public InstrumentItem(final ServerPlayerRunnable onOpenRequest) {
        super(new Properties()
            .stacksTo(1)
        );

        this.onOpenRequest = onOpenRequest;
    }
    static void sendOpenRequest(final ServerPlayer player, final String instrumentType) {
        ModPacketHandler.sendToClient(new OpenInstrumentPacket(instrumentType), player);
    }
    

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide) {
            pPlayer.getCapability(InstrumentOpenProvider.INSTRUMENT_OPEN).ifPresent((lyreOpen) ->
                lyreOpen.setOpen(true)
            );
            
            onOpenRequest.run((ServerPlayer)pPlayer);
        }
        
        return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
    }
    

    @FunctionalInterface
    public static interface ServerPlayerRunnable {
        void run(final ServerPlayer player);
    }
}
