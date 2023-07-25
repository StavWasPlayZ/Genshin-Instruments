package com.cstav.genshinstrument.event;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.block.partial.AbstractInstrumentBlock;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent.ByPlayer;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
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


    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent event) {
        AbstractInstrumentScreen.getCurrentScreen(MINECRAFT).ifPresent(AbstractInstrumentScreen::handleAbruptClosing);
    }
    

    // Handle block instrument arm pose
    @SubscribeEvent
    public static void prePlayerRenderEvent(final RenderPlayerEvent.Pre event) {
        final Player player = event.getEntity();

        if (!(InstrumentOpenProvider.isOpen(player) && !InstrumentOpenProvider.isItem(player)))
            return;


        final Block block = player.level().getBlockState(InstrumentOpenProvider.getBlockPos(player)).getBlock();
        if (!(block instanceof AbstractInstrumentBlock))
            return;

        final AbstractInstrumentBlock instrumentBlock = (AbstractInstrumentBlock) block;
        final PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();
        model.leftArmPose = model.rightArmPose = instrumentBlock.getClientBlockArmPose().getArmPose();
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
