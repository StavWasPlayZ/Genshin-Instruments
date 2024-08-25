package com.cstav.genshinstrument.item;

import com.cstav.genshinstrument.GICreativeModeTabs;
import com.cstav.genshinstrument.client.ModArmPose;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.event.PosePlayerArmEvent;
import com.cstav.genshinstrument.item.clientExtensions.InstrumentItemClientExt;
import com.cstav.genshinstrument.networking.OpenInstrumentPacketSender;
import com.cstav.genshinstrument.networking.packet.instrument.util.InstrumentPacketUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * An item responsible for opening an {@link InstrumentScreen}.
 */
public class InstrumentItem extends Item implements ItemPoseModifier {

    protected final OpenInstrumentPacketSender onOpenRequest;
    /**
     * Creates an instrument item and registers it to the Instruments item group.
     * @param onOpenRequest A server-side event fired when the player has requested to interact
     * with the instrument.
     * It should send a packet to the given player for opening this instrument's screen.
     */
    public InstrumentItem(final OpenInstrumentPacketSender onOpenRequest) {
        this(onOpenRequest, new Properties().tab(GICreativeModeTabs.instrumentsTab));
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
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        final ItemStack item = pPlayer.getItemInHand(pUsedHand);

        if (pLevel.isClientSide)
            return InteractionResultHolder.success(item);

        if (InstrumentPacketUtil.sendOpenPacket((ServerPlayer)pPlayer, pUsedHand, onOpenRequest)) {
            return InteractionResultHolder.success(item);
        } else {
            return InteractionResultHolder.fail(item);
        }
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void onPosePlayerArm(final PosePlayerArmEvent args) {
        ModArmPose.poseForItemInstrument(args);
    }
    
}
