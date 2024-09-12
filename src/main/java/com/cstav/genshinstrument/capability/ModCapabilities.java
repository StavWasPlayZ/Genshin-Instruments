package com.cstav.genshinstrument.capability;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.networking.GIPacketHandler;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.NotifyInstrumentOpenPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.FORGE, modid = GInstrumentMod.MODID)
public class ModCapabilities {

    @SubscribeEvent
    public static void registerCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {

            if (!player.getCapability(InstrumentOpenProvider.INSTRUMENT_OPEN).isPresent())
                event.addCapability(GInstrumentMod.loc("instrument_caps"), new InstrumentOpenProvider());

            // If they previously had their instrument open, and unsuccessfully closed:
            InstrumentOpenProvider.setClosed(player);

        }
    }


    // Moved to ClientEvents
//    // Sync the open state of players to a new player
//    @SubscribeEvent
//    public static void onPlayerJoin(final PlayerEvent.PlayerLoggedInEvent event) {
//        notifyOpenStateToPlayers((ServerPlayer) event.getEntity());
//    }
//
//    // And on dimension traversal
//    @SubscribeEvent
//    public static void onDimensionChangedEvent(final PlayerChangedDimensionEvent event) {
//        notifyOpenStateToPlayers((ServerPlayer) event.getEntity());
//    }
//
//    private static void notifyOpenStateToPlayers(final ServerPlayer target) {
//        final Level level = target.level();
//
//        level.players().forEach((player) -> {
//            if (player.equals(target))
//                return;
//
//            notifyOpenStateToPlayer(player, target);
//        });
//    }
    public static void notifyOpenStateToPlayer(final Player player, final ServerPlayer target) {
        if (!InstrumentOpenProvider.isOpen(player))
            return;

        final NotifyInstrumentOpenPacket packet;

        if (InstrumentOpenProvider.isItem(player)) {
            packet = new NotifyInstrumentOpenPacket(
                player.getUUID(),
                InstrumentOpenProvider.getHand(player)
            );
        } else {
            packet = new NotifyInstrumentOpenPacket(
                player.getUUID(),
                InstrumentOpenProvider.getBlockPos(player)
            );
        }

        GIPacketHandler.sendToClient(packet, target);
    }
    
}
