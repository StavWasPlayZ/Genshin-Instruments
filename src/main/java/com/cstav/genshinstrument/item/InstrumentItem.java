package com.cstav.genshinstrument.item;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.item.clientExtensions.InstrumentItemClientExt;
import com.cstav.genshinstrument.networking.OpenInstrumentPacketSender;
import com.cstav.genshinstrument.networking.packet.instrument.util.InstrumentPacketUtil;
import com.nimbusds.jose.util.Resource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

/**
 * An item responsible for opening an {@link InstrumentScreen}.
 */
public class InstrumentItem extends Item {

    protected final OpenInstrumentPacketSender onOpenRequest;
    /**
     * @param onOpenRequest A server-side event fired when the player has requested to interact
     * with the instrument.
     * It should send a packet to the given player for opening this instrument's screen.
     */
    public InstrumentItem(final OpenInstrumentPacketSender onOpenRequest, ResourceKey<Item> id) {
        this(onOpenRequest, new Properties().setId(id));
    }
    /**
     * @param onOpenRequest A server-side event fired when the player has requested to interact
     * with the instrument.
     * It should send a packet to the given player for opening this instrument's screen.
     * @param properties The properties of this instrument item. {@link Properties#stacksTo stack size}
     * will always be set to 1.
     */
    public InstrumentItem(final OpenInstrumentPacketSender onOpenRequest, final Properties properties) {
        super(properties.stacksTo(1));
        this.onOpenRequest = onOpenRequest;
    }
    

    @Override
    public InteractionResult use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pLevel.isClientSide)
            return InteractionResult.SUCCESS;

        if (InstrumentPacketUtil.sendOpenPacket((ServerPlayer)pPlayer, pUsedHand, onOpenRequest)) {
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.FAIL;
        }
    }

    //TODO see if working
//    @Override
//    public @NotNull ItemUseAnimation getUseAnimation(ItemStack pStack) {
//        return ItemUseAnimation.NONE;
//    }
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new InstrumentItemClientExt());
    }
    
}
