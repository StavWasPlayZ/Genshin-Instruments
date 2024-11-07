package com.cstav.genshinstrument.event;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.block.partial.AbstractInstrumentBlock;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.IHeldInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.midi.MidiController;
import com.cstav.genshinstrument.networking.GIPacketHandler;
import com.cstav.genshinstrument.networking.packet.instrument.c2s.ReqInstrumentOpenStatePacket;
import com.cstav.genshinstrument.networking.packet.instrument.util.HeldSoundPhase;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.GameShuttingDownEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import java.util.Optional;

@EventBusSubscriber(bus = Bus.FORGE, modid = GInstrumentMod.MODID, value = Dist.CLIENT)
public class ClientEvents {

    private static final Minecraft MINECRAFT = Minecraft.getInstance();


    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent event) {
        InstrumentScreen.getCurrentScreen(MINECRAFT).ifPresent(InstrumentScreen::handleAbruptClosing);
    }


    //Handle block instrument arm pose
    //NOTE: `RenderPlayerEvent` is *NOT HOOKED TO ANYTHING!*
    @SubscribeEvent
    public static void onExtractedRenderState(final ExctractRenderStateEvent.Post event) {
        if (!(event.entity instanceof Player player))
            return;

        if (!(InstrumentOpenProvider.isOpen(player) && !InstrumentOpenProvider.isItem(player)))
            return;

        final Block block = player.level().getBlockState(InstrumentOpenProvider.getBlockPos(player)).getBlock();
        if (!(block instanceof AbstractInstrumentBlock instrumentBlock))
            return;

        final PlayerRenderState renderState = (PlayerRenderState) event.state;
        final ArmPose pose = instrumentBlock.getClientBlockArmPose();

        renderState.mainHandState.pose = renderState.offhandState.pose = pose;
        renderState.mainHandState.isEmpty = renderState.offhandState.isEmpty = false;

        // The INSTRUMENT_BLOCK_PLAYED state was used to determine if we
        // should pose the arm a certain way. It originally used RenderLivingEvent.
        // This has the benefit that we need not know the actual entity behind
        // the state, but rather just the state itself - matching with the
        // actual vanilla behavior.

//        ((ICustomRenderFieldProvider)renderState)
//            .genshin_Instruments$getCustomField(GIRenderStates.INSTRUMENT_BLOCK_PLAYED)
//            .ifPresent((pose) -> {
//                renderState.mainHandState.pose = renderState.offhandState.pose = pose;
//            });
    }

    
    //#region Shared Instrument Screen implementation
    // Responsible for showing the notes other players play

    @SubscribeEvent
    public static void onInstrumentPlayed(final InstrumentPlayedEvent<?> event) {
        if (!validateSharedScreen(event))
            return;

        // Only show play notes in the local range
        if (!event.soundMeta().pos().closerThan(MINECRAFT.player.blockPosition(), NoteSound.LOCAL_RANGE))
            return;

        foreignPlayableInstrumentScreen(event)
            .ifPresent((screen) -> screen.foreignPlay(event));
    }

    // Also shared screen impl
    // Handle separately because unlike in the above *initiate*
    // method, we want to release it - which for all we know, could be
    // blocks away for some reason.
    @SubscribeEvent
    public static void onHeldNoteSound(final HeldNoteSoundPlayedEvent event) {
        if (event.phase != HeldSoundPhase.RELEASE)
            return;
        if (!validateSharedScreen(event))
            return;

        foreignPlayableInstrumentScreen(event)
            .filter((screen) -> screen instanceof IHeldInstrumentScreen)
            .map((screen) -> (IHeldInstrumentScreen) screen)
            .ifPresent((screen) -> screen.releaseForeign(event));
    }

    /**
     * @return Whether the provided instrument event is eligible
     * for a shared screen play event
     */
    private static boolean validateSharedScreen(final InstrumentPlayedEvent<?> event) {
        if (!event.level().isClientSide)
            return false;
        if (!ModClientConfigs.SHARED_INSTRUMENT.get())
            return false;

        // If this sound was produced by a player, and that player is ourselves - omit.
        if (event.isByPlayer()) {
            final Entity initiator = event.entityInfo().get().entity;

            if (initiator.equals(MINECRAFT.player))
                return false;
        }

        return true;
    }
    /**
     * @return The current instrument screen (if present)
     * that matches the played sound described by the provided sound event.
     */
    private static Optional<InstrumentScreen> foreignPlayableInstrumentScreen(final InstrumentPlayedEvent<?> event) {
        return InstrumentScreen.getCurrentScreen(MINECRAFT)
            // Filter instruments that do not match the one we're on.
            // If the note identifier is empty, it matters not - as the check
            // will be performed on the sound itself, which is bound to be unique for every note.
            .filter((screen) ->
                event.soundMeta().noteIdentifier().isEmpty()
                || screen.getInstrumentId().equals(event.soundMeta().instrumentId())
            )
        ;
    }

    //#endregion


    @SubscribeEvent
    public static void onEntityJoinLevel(final EntityJoinLevelEvent event) {
        // This is a replacement to ModCapabilities' sync mechanism.
        // Since for some reason it sends the info BEFORE players load in.
        if (event.getEntity() instanceof RemotePlayer player) {
            GIPacketHandler.sendToServer(new ReqInstrumentOpenStatePacket(player.getUUID()));
        }
    }


    @SubscribeEvent
    public static void onLevelUnload(final LevelEvent.Unload event) {
        HeldNoteSounds.releaseAll();
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

}
