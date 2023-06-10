package com.cstav.genshinstrument.event;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.item.InstrumentItem;
import com.cstav.genshinstrument.util.ServerUtil;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = Bus.FORGE, modid = Main.MODID, value = Dist.CLIENT)
public class ClientEvents {

    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    
    // Responsible for closing the instrument screen when
    // an instrument item is missing from the player's hands
    @SubscribeEvent
    public static void onPlayerTick(final ClientTickEvent event) {
        if (!(MINECRAFT.screen instanceof AbstractInstrumentScreen))
            return;
            
        final AbstractInstrumentScreen screen = (AbstractInstrumentScreen) MINECRAFT.screen;
        if (!(MINECRAFT.player.getItemInHand(screen.interactionHand).getItem() instanceof InstrumentItem))
            screen.onClose();
    }

    
    // Responsible for showing the notes other players play
    @SuppressWarnings("resource")
    @SubscribeEvent
    public static void onInstrumentPlayed(final InstrumentPlayedEvent.ByPlayer event) {
        // Assuming we're always on the physical client
        if (!event.player.equals(Minecraft.getInstance().player))
            onInstrumentPlayed((InstrumentPlayedEvent)event);
    }
    @SubscribeEvent
    public static void onInstrumentPlayed(final InstrumentPlayedEvent event) {
        //TODO: Add a safeguard for client configs
        if (!event.isClientSide)
            return;

        if (!(MINECRAFT.screen instanceof AbstractInstrumentScreen))
            return;

        final AbstractInstrumentScreen screen = (AbstractInstrumentScreen) MINECRAFT.screen;

        //TODO Make event pass the instrument ID rather than the instrument ItemStack.
        // Also make it so that the general event recieves it, not just ByPlayer
        // if (screen.getInstrumentId().equals(event.))

        if (event.pos.closerThan(MINECRAFT.player.blockPosition(), ServerUtil.PLAY_DISTANCE / 2)) {

            
        }
    }

}
