package com.cstav.genshinstrument.event;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.item.InstrumentItem;
import com.cstav.genshinstrument.util.CommonUtil;
import com.cstav.genshinstrument.util.ServerUtil;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.FORGE, modid = GInstrumentMod.MODID)
public abstract class ServerEvents {
    
    @SubscribeEvent
    public static void onServerTick(final LevelTickEvent event) {
        if ((event.phase != Phase.END) && (event.side == LogicalSide.SERVER))
            event.level.players().forEach(ServerEvents::handleAbruptInstrumentClose);
    }

    private static void handleAbruptInstrumentClose(final Player player) {
        if (!InstrumentOpenProvider.isOpen(player))
            return;

        if (InstrumentOpenProvider.isItem(player)) {
            // This is done like so because there is no event (that I know of) for when an item is moved/removed
            if (CommonUtil.getItemInHands(InstrumentItem.class, player).isEmpty())
                ServerUtil.setInstrumentClosed(player);
        } else {
            // Close an instrument block if the player is too far away
            if (!InstrumentOpenProvider.getBlockPos(player).closerToCenterThan(player.position(), 6))
                ServerUtil.setInstrumentClosed(player);
        }
    }

}
