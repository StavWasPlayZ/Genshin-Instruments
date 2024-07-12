package com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.grid.HeldGridNoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.grid.NoteGridButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held.IHoldableNoteButton;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSound.Phase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class HeldGridInstrumentScreen extends GridInstrumentScreen {

    private HeldNoteSound[] heldNoteSounds = getInitHeldNoteSounds();
    public abstract HeldNoteSound[] getInitHeldNoteSounds();

    public void setHeldNoteSounds(HeldNoteSound[] heldNoteSounds) {
        this.heldNoteSounds = heldNoteSounds;
        notesIterable().forEach((btn) ->
            ((IHoldableNoteButton)btn).setHeldNoteSound(heldNoteSounds[((NoteGridButton)btn).posToIndex()])
        );
    }
    public HeldNoteSound[] getHeldNoteSounds() {
        return heldNoteSounds;
    }

    @Override
    public NoteSound[] getInitSounds() {
        return HeldNoteSound.getSounds(getInitHeldNoteSounds(), Phase.ATTACK);
    }

    @Override
    public NoteGridButton createNote(int row, int column) {
        return new HeldGridNoteButton(row, column, this, getInitHeldNoteSounds());
    }


    @Override
    public void onClose() {
        notesIterable().forEach((btn) -> ((IHoldableNoteButton)btn).releaseHeld(false));
        super.onClose();
    }
}
