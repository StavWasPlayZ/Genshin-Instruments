package com.cstav.genshinstrument.client.gui.screens.instrument.drum;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DrumNoteButton extends NoteButton {

    public final DrumButtonType btnType;
    public final boolean isRight;

    public DrumNoteButton(final DrumButtonType btnType, boolean isLeft, AratakisGreatAndGloriousDrumScreen drumScreen) {
        super(
            btnType.getSound(), ModClientConfigs.DRUM_LABEL_TYPE.get().getLabelSupplier(),
            btnType.getIndex(), 2,
            drumScreen, 13, .34f, 1.01f
        );

        this.btnType = btnType;
        this.isRight = isLeft;
    }
    
}
