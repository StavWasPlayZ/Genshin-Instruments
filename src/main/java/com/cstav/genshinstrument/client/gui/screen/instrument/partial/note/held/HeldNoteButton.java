package com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class HeldNoteButton extends NoteButton implements IHoldableNoteButton {
    private boolean isHeld = false;

    public HeldNoteButton(NoteSound sound, NoteLabelSupplier labelSupplier, InstrumentScreen instrumentScreen, int pitch) {
        super(sound, labelSupplier, instrumentScreen, pitch);
    }
    public HeldNoteButton(NoteSound sound, NoteLabelSupplier labelSupplier, InstrumentScreen instrumentScreen) {
        super(sound, labelSupplier, instrumentScreen);
    }

    @Override
    public boolean isPlaying() {
        return isHeld();
    }

    @Override
    public boolean isHeld() {
        return isHeld;
    }

    @Override
    public void release() {
        super.release();
        isHeld = false;
        ((HeldNoteButtonRenderer)noteRenderer).playRelease();
    }

    @Override
    public boolean play() {
        if (!super.play())
            return false;

        isHeld = true;
        return true;
    }

    @Override
    protected abstract HeldNoteButtonRenderer initNoteRenderer();
}
