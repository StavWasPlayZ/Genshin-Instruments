package com.cstav.genshinstrument.item;

import java.util.function.Consumer;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.item.clientExtensions.ClientInstrumentItem;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.packets.instrument.NotifyInstrumentOpenPacket;
import com.cstav.genshinstrument.networking.packets.instrument.OpenInstrumentPacket;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.World;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/**
 * An item responsible for opening an {@link AbstractInstrumentScreen}.
 */
public class InstrumentItem extends Item {

    protected final ServerPlayerEntityRunnable onOpenRequest;
    /**
     * @param onOpenRequest A server-side event fired when the player has requested to interact
     * with the instrument.
     * It should should send a packet to the given player for opening this instrument's screen.
     */
    public InstrumentItem(final ServerPlayerEntityRunnable onOpenRequest) {
        super(new Properties()
            .stacksTo(1)
        );

        this.onOpenRequest = onOpenRequest;
    }

    static void sendOpenRequest(ServerPlayerEntity player, InteractionHand hand, String instrumentType) {
        ModPacketHandler.sendToClient(new OpenInstrumentPacket(instrumentType, hand), player);
    }
    

    // @Override
    // public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        // if (!pLevel.isClientSide) {
        //     onOpenRequest.run((ServerPlayerEntity)pPlayer, pUsedHand);

        //     // Update the the capabilty on server
        //     InstrumentOpenProvider.setOpen(pPlayer, true);
        //     // And clients
        //     pLevel.players().forEach((player) ->
        //         ModPacketHandler.sendToClient(
        //             new NotifyInstrumentOpenPacket(pPlayer.getUUID(), true),
        //             (ServerPlayerEntity)player
        //         )
        //     );
        // }
        
        // return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
    // }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            onOpenRequest.run((ServerPlayerEntity)user, hand);

            // Update the the capabilty on server
            InstrumentOpenProvider.setOpen(user, true);
            // And clients
            world.players().forEach((player) ->
                ModPacketHandler.sendToClient(
                    new NotifyInstrumentOpenPacket(pPlayer.getUUID(), true),
                    (ServerPlayerEntity)player
                )
            );
        }
        
        return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
    }
    

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.CUSTOM;
    }
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new ClientInstrumentItem());
    }
    
    

    @FunctionalInterface
    public static interface ServerPlayerEntityRunnable {
        void run(final ServerPlayerEntity player, final Hand hand);
    }
}
