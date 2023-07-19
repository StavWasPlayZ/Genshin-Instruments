package com.cstav.genshinstrument.item;

import java.util.function.Consumer;

import com.cstav.genshinstrument.ModCreativeModeTabs;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.item.clientExtensions.ClientInstrumentItem;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.packet.instrument.NotifyInstrumentOpenPacket;
import com.cstav.genshinstrument.networking.packet.instrument.OpenInstrumentPacket;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/**
 * An item responsible for opening an {@link AbstractInstrumentScreen}.
 */
public class InstrumentItem extends Item {

    protected final ServerPlayerRunnable onOpenRequest;
    /**
     * Creates an instrument item and registers it to the Instruments item group.
     * @param onOpenRequest A server-side event fired when the player has requested to interact
     * with the instrument.
     * It should should send a packet to the given player for opening this instrument's screen.
     */
    public InstrumentItem(final ServerPlayerRunnable onOpenRequest) {
        this(onOpenRequest, new Properties().tab(ModCreativeModeTabs.instrumentsTab));
    }
    /**
     * @param onOpenRequest A server-side event fired when the player has requested to interact
     * with the instrument.
     * It should should send a packet to the given player for opening this instrument's screen.
     * @param properties The properties of this instrument item. {@link Properties#stacksTo stack size}
     * will always be set to 1.
     */
    public InstrumentItem(final ServerPlayerRunnable onOpenRequest, final Properties properties) {
        super(properties.stacksTo(1));
        this.onOpenRequest = onOpenRequest;
    }

    static void sendOpenRequest(ServerPlayer player, InteractionHand hand, String instrumentType) {
        ModPacketHandler.sendToClient(new OpenInstrumentPacket(instrumentType, hand), player);
    }
    

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide) {
            onOpenRequest.run((ServerPlayer)pPlayer, pUsedHand);

            // Update the the capabilty on server
            InstrumentOpenProvider.setOpen(pPlayer, true);
            // And clients
            pLevel.players().forEach((player) ->
                ModPacketHandler.sendToClient(
                    new NotifyInstrumentOpenPacket(pPlayer.getUUID(), true),
                    (ServerPlayer)player
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
        System.out.println("test");
    }
    
    

    @FunctionalInterface
    public static interface ServerPlayerRunnable {
        void run(final ServerPlayer player, final InteractionHand hand);
    }
}
