package com.cstav.genshinstrument.client.gui.screens.instrument.drum;

import com.cstav.genshinstrument.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;

public class DrumNoteButton extends NoteButton {

    public final DrumButtonType btnType;
    public final boolean isRight;

    public DrumNoteButton(final DrumButtonType btnType, boolean isLeft, AratakisGreatAndGloriousDrumScreen drumScreen) {
        super(
            btnType.getSound(), ModClientConfigs.DRUM_LABEL_TYPE.get().getLabelSupplier(),
            btnType.getIndex(), 2,
            drumScreen, 13, .3336f
        );

        this.btnType = btnType;
        this.isRight = isLeft;
    }
    
}
