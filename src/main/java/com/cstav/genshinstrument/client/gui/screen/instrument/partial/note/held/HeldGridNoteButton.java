package com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButtonRenderer;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.GridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.NoteGridButton;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeldGridNoteButton extends NoteGridButton {
    private boolean isHeld = false;
    protected HeldNoteButtonRenderer renderer;

    public HeldGridNoteButton(int row, int column, GridInstrumentScreen instrumentScreen) {
        super(row, column, instrumentScreen);
    }
    public HeldGridNoteButton(int row, int column, GridInstrumentScreen instrumentScreen, int pitch) {
        super(row, column, instrumentScreen, pitch);
    }

    @Override
    public boolean isPlaying() {
        return isHeld();
    }

    public boolean isHeld() {
        return isHeld;
    }
    public void release() {
        super.release();
        isHeld = false;
        renderer.playRelease();
    }

    @Override
    public boolean play() {
        if (!super.play())
            return false;

        isHeld = true;
        return true;
    }

    @Override
    protected NoteButtonRenderer initNoteRenderer() {
        return renderer = new HeldNoteButtonRenderer(this, this::getTextureAtRow);
    }
}
