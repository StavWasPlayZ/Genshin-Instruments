package com.cstav.genshinstrument.client.gui.screen.instrument.drum;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButtonRenderer;
import com.cstav.genshinstrument.networking.buttonidentifier.DrumNoteIdentifier;
import com.mojang.blaze3d.platform.InputConstants.Key;

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


    public Key getKey() {
        return btnType.getKeys().getKey(isRight);
    }


    @Override
    protected NoteButtonRenderer initNoteRenderer() {
        return new NoteButtonRenderer(this, btnType.getSpriteIndex(isRight), 3);
    }


    @Override
    public int getNoteOffset() {
        return (btnType == DrumButtonType.DON) ? 0 : 1;
    }
    
}
