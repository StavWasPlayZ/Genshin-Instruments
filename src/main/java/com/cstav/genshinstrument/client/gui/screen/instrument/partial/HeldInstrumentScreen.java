package com.cstav.genshinstrument.client.gui.screen.instrument.partial;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held.IHoldableNoteButton;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class HeldInstrumentScreen extends InstrumentScreen {

    @Override
    public void onClose() {
        notesIterable().forEach((btn) -> ((IHoldableNoteButton)btn).releaseHeld(false));
        super.onClose();
    }

}
