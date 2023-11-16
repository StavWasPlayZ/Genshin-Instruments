package com.cstav.genshinstrument.event;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.block.partial.AbstractInstrumentBlock;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings;
import com.cstav.genshinstrument.client.midi.MidiController;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent.ByPlayer;
import com.cstav.genshinstrument.item.ItemPoseModifier;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.util.CommonUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = Bus.FORGE, modid = GInstrumentMod.MODID, value = Dist.CLIENT)
public class ClientEvents {

    private static final Minecraft MINECRAFT = Minecraft.getInstance();


    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent event) {
        InstrumentScreen.getCurrentScreen(MINECRAFT).ifPresent(InstrumentScreen::handleAbruptClosing);
    }


    // Behaviour copied from Fabric:
    private static boolean poseForBlockInstrument(final PosePlayerArmEvent event) {
        final Player player = event.player;

        if (!InstrumentOpenProvider.isOpen(player) || InstrumentOpenProvider.isItem(player))
            return false;

        final Block block = player.level.getBlockState(InstrumentOpenProvider.getBlockPos(player)).getBlock();
        if (!(block instanceof AbstractInstrumentBlock blockInstrument))
            return false;

        blockInstrument.onPosePlayerArm(event);
        return true;
    }

    @SubscribeEvent
    public static void posePlayerArmEvent(final PosePlayerArmEvent event) {
        if (poseForBlockInstrument(event))
            return;

        // For items
        CommonUtil.getItemInHands(ItemPoseModifier.class, event.player).ifPresent((item) ->
            item.onPosePlayerArm(event)
        );
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
        if (!event.playPos.closerThan(MINECRAFT.player.blockPosition(), NoteSound.LOCAL_RANGE))
            return;


        InstrumentScreen.getCurrentScreen(MINECRAFT)
            // Filter instruments that do not match the one we're on
            .filter((screen) -> screen.getInstrumentId().equals(event.instrumentId))
            .ifPresent((screen) -> {
                try {
                    screen.getNoteButton(event.noteIdentifier).playNoteAnimation(true);
                } catch (Exception e) {
                    // Button was prolly just not found
                }
            }
        );
    }


    // Subscribe active instruments to a MIDI event
    @SubscribeEvent
    public static void onMidiEvent(final MidiEvent event) {
        InstrumentScreen.getCurrentScreen(Minecraft.getInstance())
            .filter(InstrumentScreen::isMidiInstrument)
            .ifPresent((instrument) -> instrument.midiReceiver.onMidi(event));
    }

    // Safely close MIDI streams upon game shutdown
    @SubscribeEvent
    public static void onGameShutdown(final GameShuttingDownEvent event) {
        MidiController.unloadDevice();
    }


    // To accommodate for my laziness to move the client initiator from the main class
    // on dev branch
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        InstrumentKeyMappings.registerKeybinds();
    }

}
