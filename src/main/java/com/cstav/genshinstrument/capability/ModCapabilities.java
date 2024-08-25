package com.cstav.genshinstrument.capability;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.networking.GIPacketHandler;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.NotifyInstrumentOpenPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.FORGE, modid = GInstrumentMod.MODID)
public class ModCapabilities {

    // The below should've been named "attachCapabilities" but oh well
    @SubscribeEvent
    public static void registerCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {

            if (!player.getCapability(InstrumentOpenProvider.INSTRUMENT_OPEN).isPresent())
                event.addCapability(new ResourceLocation(GInstrumentMod.MODID, "instrument_caps"), new InstrumentOpenProvider());

            // If they previously had their instrument open, and unsuccessfully closed:
            InstrumentOpenProvider.setClosed(player);

        }
    }


    // Sync the open state of players to a new player

    @SubscribeEvent
    public static void onPlayerJoin(final PlayerEvent.PlayerLoggedInEvent event) {
        final Level level = event.getEntity().level();
        if (level.isClientSide)
            return;

        level.players().forEach((player) -> {
            if (player.equals(event.getEntity()))
                return;

            if (InstrumentOpenProvider.isOpen(player))
                notifyOpenStateToPlayer(player, (ServerPlayer) event.getEntity());
        });
    }

    private static void notifyOpenStateToPlayer(final Player player, final ServerPlayer target) {
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

    @SubscribeEvent
    public static void actuallyRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(InstrumentOpenProvider.class);
    }

}
