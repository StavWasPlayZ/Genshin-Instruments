package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.animation;

import com.cstav.genshinstrument.client.AnimationController;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoteAnimationController extends AnimationController {

    /** The size of this button in double */
    protected double dSize;
    protected NoteButton button;
    public NoteAnimationController(float duration, float targetValue, final NoteButton button) {
        super(duration, targetValue);
        this.button = button;
    }


    @Override
    protected void animFrame(final float targetTime, final float deltaValue) {
        // Assuming the shape will always be a square
        if (getAnimTime() > targetTime/2)
            dSize += deltaValue * 1.5;
        else
            dSize -= deltaValue * 1.5;
        
        final int size = (int)dSize;
        button.setWidth(size);
        button.setHeight(size);

        button.moveToCenter();
    }

    @Override
    protected void resetAnimVars() {
        super.resetAnimVars();

        button.setWidth(button.instrumentScreen.getNoteSize());
        button.setHeight(button.instrumentScreen.getNoteSize());
        dSize = button.instrumentScreen.getNoteSize();

        button.x = button.getInitX();
        button.y = button.getInitY();
    }
    
    public void play(final boolean isForeign) {
        play();
        if (isForeign)
            targetValue /= 1.75f;
    }
}
