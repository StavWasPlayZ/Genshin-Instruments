package com.cstav.genshinstrument.item.partials;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.capability.lyreOpen.LyreOpenProvider;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.packets.lyre.OpenLyrePacket;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Bus.FORGE)
public class LyreItem extends Item {

    public LyreItem(final Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide) {
            pPlayer.getCapability(LyreOpenProvider.LYRE_OPEN).ifPresent((lyreOpen) ->
                lyreOpen.setOpen(true)
            );
            ModPacketHandler.sendToClient(new OpenLyrePacket(), (ServerPlayer)pPlayer);
        }

        return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
    }
    
    public static void inputEvent(final InputEvent.Key event) {
        
    }
}