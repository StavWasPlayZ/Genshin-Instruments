package com.cstav.genshinstrument.event;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent.ByPlayer;
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
    @SubscribeEvent
    public static void onInstrumentPlayed(final InstrumentPlayedEvent event) {
        if (!event.isClientSide)
            return;
        if ((event instanceof ByPlayer) && ((ByPlayer)(event)).player.equals(MINECRAFT.player))
            return;

        if (!ModClientConfigs.SHARED_INSTRUMENT.get())
            return;


        if (!(MINECRAFT.screen instanceof AbstractInstrumentScreen))
            return;

        final AbstractInstrumentScreen screen = (AbstractInstrumentScreen) MINECRAFT.screen;

        if (!screen.getInstrumentId().equals(event.instrumentId))
            return;

        if (!event.pos.closerThan(MINECRAFT.player.blockPosition(), ServerUtil.PLAY_DISTANCE / 3))
            return;


        for (NoteButton note : screen.notesIterable())
            if (note.sound.equals(event.sound)) {
                note.playNoteAnimation(true);
                return;
            }
    }

}
