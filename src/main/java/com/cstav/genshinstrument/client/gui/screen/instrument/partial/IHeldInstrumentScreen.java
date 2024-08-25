package com.cstav.genshinstrument.client.gui.screen.instrument.partial;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held.IHoldableNoteButton;
import com.cstav.genshinstrument.event.HeldNoteSoundPlayedEvent;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.networking.packet.instrument.util.HeldSoundPhase;
import com.cstav.genshinstrument.sound.held.HeldNoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSound.Phase;
import com.cstav.genshinstrument.sound.held.HeldNoteSounds;
import com.cstav.genshinstrument.sound.held.InitiatorID;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.NoSuchElementException;

@OnlyIn(Dist.CLIENT)
public interface IHeldInstrumentScreen {
    HeldNoteSound[] getHeldNoteSounds();
    void setHeldNoteSounds(final HeldNoteSound[] heldNoteSounds);

    default InstrumentScreen asScreen() {
        return (InstrumentScreen) this;
    }

    default void foreignPlayHeld(final InstrumentPlayedEvent<?> event) {
        if (!(event instanceof HeldNoteSoundPlayedEvent e))
            return;
        // Release handled separately at IHeldInstrumentScreen#releaseForeign
        if (e.phase == HeldSoundPhase.RELEASE)
            return;

        try {

            final NoteButton note = asScreen().getNoteButton(
                event.soundMeta().noteIdentifier(),
                e.sound().getSound(Phase.ATTACK),
                event.soundMeta().pitch()
            );

            final IHoldableNoteButton heldNote = (IHoldableNoteButton) note;
            heldNote.playAttackAnimation(true);

        } catch (Exception ignore) {
            // Button was prolly just not found
        }
    }

    default void releaseForeign(final HeldNoteSoundPlayedEvent event) {
        try {

            final NoteButton note = asScreen().getNoteButton(
                event.soundMeta().noteIdentifier(),
                event.sound().getSound(Phase.ATTACK),
                event.soundMeta().pitch()
            );

            final IHoldableNoteButton heldNote = (IHoldableNoteButton) note;
            // Don't play release if already released
            if (!heldNote.isHeld())
                return;

            heldNote.playReleaseAnimation(true);

        } catch (Exception ignore) {
            // Button was prolly just not found
        }
    }

    default void closeHeldScreen() {
        final Player player = Minecraft.getInstance().player;

        HeldNoteSounds.getUnique(
            // Release all sounds relating to this player
            HeldNoteSounds.release(
                InitiatorID.fromEntity(player)
            )
        ).forEach((sound) -> {
            // Then, notify their release.
            //NOTE: We don't care for the 'notify' parameter.
            // The server will not know about this behaviour ever
            // if we don't tell it.
            HeldNoteSounds.notifyRelease(
                sound,
                ((NoteButton) getNoteButton(sound.heldSoundContainer, sound.notePitch))
                    .getIdentifier()
            );
        });
    }

    default IHoldableNoteButton getNoteButton(final HeldNoteSound noteSound, final int pitch) {
        //FIXME duplicate of InstrumentScreen#getNoteButton. Convert to generic of sound type.

        for (final NoteButton note : asScreen().notesIterable()) {
            final IHoldableNoteButton heldNote = (IHoldableNoteButton) note;
            final HeldNoteSound sound = heldNote.getHeldNoteSound();

            if (!noteSound.equals(sound))
                continue;

            if (!asScreen().identifyByPitch() || (note.getPitch() == pitch))
                return (IHoldableNoteButton) note;
        }

        throw new NoSuchElementException("Could not find a note in "+asScreen().getInstrumentId()+" based on the given identifier");
    }
}
