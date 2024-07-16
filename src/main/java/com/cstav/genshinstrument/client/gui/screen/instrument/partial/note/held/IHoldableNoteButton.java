package com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.IHeldInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.networking.GIPacketHandler;
import com.cstav.genshinstrument.networking.packet.instrument.c2s.C2SHeldNoteSoundPacket;
import com.cstav.genshinstrument.networking.packet.instrument.util.HeldSoundPhase;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSounds;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public interface IHoldableNoteButton {
    boolean isHeld();
    void setHeld(final boolean held);

    HeldNoteSound getHeldNoteSound();
    void setHeldNoteSound(final HeldNoteSound sound);

    boolean releaseAnimationPlayable();


    /**
     * Releases the active note
     * @param notePitch The note pitch to target
     * @param targetPitch Should only release the provided pitch?
     * @param heldSound The sound being released
     */
    default void releaseHeld(int notePitch, boolean targetPitch, HeldNoteSound heldSound) {
        final String initiatorId = HeldNoteSounds.getInitiatorId(Minecraft.getInstance().player);

        if (targetPitch) {
            HeldNoteSounds.release(initiatorId, heldSound, notePitch);
            sendNoteHeldPacket(heldSound, notePitch, HeldSoundPhase.RELEASE);
        } else {
            HeldNoteSounds.release(initiatorId, heldSound).stream()
                .map((soundInstance) -> soundInstance.notePitch)
                .distinct()
                .forEach((instPitch) ->
                    sendNoteHeldPacket(heldSound, instPitch, HeldSoundPhase.RELEASE)
                );
        }

        if (releaseAnimationPlayable())
            playReleaseAnimation();
    }
    /**
     * Releases the active note
     * @param notePitch The note pitch to target
     * @param targetPitch Should only release the provided pitch?
     */
    default void releaseHeld(int notePitch, boolean targetPitch) {
        releaseHeld(notePitch, targetPitch, getHeldNoteSound());
    }
    /**
     * Releases all notes of the matching sound type
     * @param targetPitch Should only release the active pitch?
     */
    default void releaseHeld(boolean targetPitch) {
        releaseHeld(asNoteBtn().getPitch(), targetPitch);
    }


    private void playReleaseAnimation() {
        setHeld(false);
        ((HeldNoteButtonRenderer) asNoteBtn().getRenderer()).playRelease();
    }


    /**
     * Plays the attack animation as a foreign
     */
    default void foreignAttack() {
        asNoteBtn().getRenderer().playNoteAnimation(true);
    }
    /**
     * Plays the release animation (if applicable) as a foreign
     */
    default void foreignRelease() {
        if (releaseAnimationPlayable())
            playReleaseAnimation();
    }


    default void playLocalHeldSound(final NoteSound sound, final int pitch) {
        toHeldSound(sound).startPlaying(pitch, asNoteBtn().instrumentScreen.volume());
    }

    default void sendNoteHeldPacket(HeldNoteSound sound, int pitch, HeldSoundPhase phase) {
        GIPacketHandler.sendToServer(new C2SHeldNoteSoundPacket(
            asNoteBtn(),
            sound, pitch,
            phase
        ));
    }
    default void sendNoteHeldPacket(NoteSound sound, int pitch, HeldSoundPhase phase) {
        sendNoteHeldPacket(toHeldSound(sound), pitch, phase);
    }


    /**
     * @return The first-matching note sound
     * of the provided held sound array.
     */
    default HeldNoteSound toHeldSound(NoteSound noteSound) {
        // Requested notes sound SHOULD be in heldNoteSounds array
        // as a form of attack phase sounds.
        return Arrays.stream(heldInstrumentScreen().getHeldNoteSounds())
            .filter((heldSound) -> heldSound.attack().equals(noteSound))
            .findFirst().orElseThrow();
    }


    default NoteButton asNoteBtn() {
        return (NoteButton) this;
    }
    default IHeldInstrumentScreen heldInstrumentScreen() {
        return (IHeldInstrumentScreen) asNoteBtn().instrumentScreen;
    }
}
