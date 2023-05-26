package com.cstav.genshinstrument.event;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.item.InstrumentItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = Bus.FORGE, modid = Main.MODID, value = Dist.CLIENT)
public class ClientEvents {
    
    @SubscribeEvent
    public static void onPlayerTick(final ClientTickEvent event) {
        final Minecraft minecraft = Minecraft.getInstance();

        final Screen screen = minecraft.screen;
        if (!(screen instanceof AbstractInstrumentScreen))
            return;

        if (minecraft.player.getItemInHand(((AbstractInstrumentScreen)(screen)).interactionHand).getItem() instanceof InstrumentItem)
            return;

        screen.onClose();
    }

}
