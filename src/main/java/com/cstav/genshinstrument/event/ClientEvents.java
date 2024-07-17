package com.cstav.genshinstrument.event;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.block.partial.AbstractInstrumentBlock;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held.IHoldableNoteButton;
import com.cstav.genshinstrument.client.midi.MidiController;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent.IByPlayer;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSound.Phase;
import com.cstav.genshinstrument.sound.held.HeldNoteSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.GameShuttingDownEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = Bus.FORGE, modid = GInstrumentMod.MODID, value = Dist.CLIENT)
public class ClientEvents {

    private static final Minecraft MINECRAFT = Minecraft.getInstance();


    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent event) {
        InstrumentScreen.getCurrentScreen(MINECRAFT).ifPresent(InstrumentScreen::handleAbruptClosing);
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
        model.leftArmPose = model.rightArmPose = instrumentBlock.getClientBlockArmPose();
    }

    
    // Responsible for showing the notes other players play
    @SubscribeEvent
    public static void onInstrumentPlayed(final InstrumentPlayedEvent<?> event) {
        if (!event.level.isClientSide)
            return;
        if (!ModClientConfigs.SHARED_INSTRUMENT.get())
            return;

        // If this sound was produced by a player, and that player is ourselves - omit.
        if (
            (event instanceof InstrumentPlayedEvent.IByPlayer)
            && ((IByPlayer<?>)(event)).getPlayer().equals(MINECRAFT.player)
        ) return;

        // Only show play notes in the local range
        if (!event.soundMeta.pos().closerThan(MINECRAFT.player.blockPosition(), NoteSound.LOCAL_RANGE))
            return;


        InstrumentScreen.getCurrentScreen(MINECRAFT)
            // Filter instruments that do not match the one we're on.
            // If the note identifier is empty, it matters not - as the check
            // will be performed on the sound itself, which is bound to be unique for every note.
            .filter((screen) ->
                event.soundMeta.noteIdentifier().isEmpty()
                || screen.getInstrumentId().equals(event.soundMeta.instrumentId())
            )
            .ifPresent((screen) -> foreignPlay(screen, event));
    }
    private static void foreignPlay(final InstrumentScreen screen, InstrumentPlayedEvent<?> event) {
        try {

            final NoteSound sound;
            if (event instanceof NoteSoundPlayedEvent e) {
                sound = e.sound;
            } else if (event instanceof HeldNoteSoundPlayedEvent e) {
                sound = e.sound.getSound(Phase.ATTACK);
            }
            else
                return;


            final NoteButton note = screen.getNoteButton(
                event.soundMeta.noteIdentifier(),
                sound, event.soundMeta.pitch()
            );

            if (event instanceof HeldNoteSoundPlayedEvent e) {
                final IHoldableNoteButton heldNote = (IHoldableNoteButton) note;

                switch (e.phase) {
                    case ATTACK -> heldNote.playAttackAnimation(true);
                    case RELEASE -> heldNote.playReleaseAnimation();
                }
            } else {
                note.playNoteAnimation(true);
            }

        } catch (Exception e) {
            // Button was prolly just not found
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
