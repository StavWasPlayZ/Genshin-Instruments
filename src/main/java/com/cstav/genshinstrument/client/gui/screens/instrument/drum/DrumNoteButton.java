package com.cstav.genshinstrument.client.gui.screens.instrument.drum;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButtonRenderer;
import com.cstav.genshinstrument.networking.buttonidentifier.DrumNoteIdentifier;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DrumNoteButton extends NoteButton {

    public final DrumButtonType btnType;
    public final boolean isRight;

    public DrumNoteButton(DrumButtonType btnType, boolean isRight, AratakisGreatAndGloriousDrumScreen drumScreen) {
        super(
            btnType.getSound(),
            ModClientConfigs.DRUM_LABEL_TYPE.get().getLabelSupplier(),
            drumScreen
        );

        this.btnType = btnType;
        this.isRight = isRight;
    }

    @Override
    public DrumNoteIdentifier getIdentifier() {
        return new DrumNoteIdentifier(this);
    }


    @Override
    protected NoteButtonRenderer initNoteRenderer() {
        return new NoteButtonRenderer(this,
            btnType.getSpriteIndex(), 2,
            13, .34f, 1.01f
        );
    }
    
}
