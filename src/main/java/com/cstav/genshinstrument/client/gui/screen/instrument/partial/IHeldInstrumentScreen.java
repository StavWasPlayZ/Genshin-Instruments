package com.cstav.genshinstrument.client.gui.screen.instrument.partial;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held.IHoldableNoteButton;
import com.cstav.genshinstrument.event.HeldNoteSoundPlayedEvent;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.networking.packet.instrument.util.HeldSoundPhase;
import com.cstav.genshinstrument.sound.held.HeldNoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSound.Phase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
}
