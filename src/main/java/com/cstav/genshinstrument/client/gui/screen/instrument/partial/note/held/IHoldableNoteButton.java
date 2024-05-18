package com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.sound.held.HeldNoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSoundInstance;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IHoldableNoteButton {
    public boolean isHeld();
    void setHeld(final boolean held);

    public HeldNoteSound getHeldNoteSound();
    public void setHeldNoteSound(final HeldNoteSound sound);

    default void releaseHeld() {
        releaseHeld(asNoteBtn().getPitch());
    }
    default void releaseHeld(int notePitch) {
        setHeld(false);

        ((HeldNoteButtonRenderer) asNoteBtn().getRenderer()).playRelease();
        HeldNoteSoundInstance.triggerRelease(getHeldNoteSound().getKey(Minecraft.getInstance().player, notePitch));
    }

    default NoteButton asNoteBtn() {
        return (NoteButton) this;
    }
}
