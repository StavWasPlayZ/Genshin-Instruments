package com.cstav.genshinstrument.event;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent.ByPlayer;
import com.cstav.genshinstrument.item.InstrumentItem;
import com.cstav.genshinstrument.item.clientExtensions.ClientInstrumentItem;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = Bus.FORGE, modid = GInstrumentMod.MODID, value = Dist.CLIENT)
public class ClientEvents {

    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    
    // Responsible for closing the instrument screen when the player no longer plays
    @SubscribeEvent
    public static void onPlayerTick(final ClientTickEvent event) {
        AbstractInstrumentScreen.getCurrentScreen(MINECRAFT).ifPresent((screen) -> {
            final Player player = MINECRAFT.player;

            if (!InstrumentOpenProvider.isOpen(player))
                screen.onClose(false);

            // Handle item not in hand seperately
            // This is done like so because there is no event (that I know of) for when an item is moved/removed
            else if (
                InstrumentOpenProvider.isItem(player)
                && !(MINECRAFT.player.getItemInHand(screen.interactionHand).getItem() instanceof InstrumentItem)
            )
                screen.onClose(true);

        });
    }


    // Handle block instrument arm pose
    @SubscribeEvent
    public static void prePlayerRenderEvent(final RenderPlayerEvent.Pre event) {
        final LocalPlayer player = MINECRAFT.player;

        if (!(InstrumentOpenProvider.isOpen(player) && !InstrumentOpenProvider.isItem(player)))
            return;

        final PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();
        model.leftArmPose = model.rightArmPose = ClientInstrumentItem.PLAYING_BLOCK_INSTRUMENT;
    }

    
    // Responsible for showing the notes other players play
    @SubscribeEvent
    public static void onInstrumentPlayed(final InstrumentPlayedEvent event) {
        if (!event.isClientSide)
            return;
        if (!ModClientConfigs.SHARED_INSTRUMENT.get())
            return;

        // If this sound was produced by a player, and that player is ourselves - omit.
        if ((event instanceof ByPlayer) && ((ByPlayer)(event)).player.equals(MINECRAFT.player))
            return;

        // Only show play notes in the local range
        if (!event.pos.closerThan(MINECRAFT.player.blockPosition(), NoteSound.LOCAL_RANGE))
            return;


        AbstractInstrumentScreen.getCurrentScreen(MINECRAFT).ifPresent((screen) -> {
            // The produced instrument sound has to match the current screen's sounds
            if (!screen.getInstrumentId().equals(event.instrumentId))
                return;

            try {

                screen.getNoteButton(event.noteIdentifier).playNoteAnimation(true);

            } catch (Exception e) {}
        });
    }

}
