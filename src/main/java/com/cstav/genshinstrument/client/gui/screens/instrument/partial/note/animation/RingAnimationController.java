package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.animation;

import com.cstav.genshinstrument.client.AnimationController;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RingAnimationController extends AnimationController {

    protected final double initSize = NoteButton.getSize() * .8f, initAlpha = 0;
    protected double size = initSize, alpha = initAlpha;
    protected final float ringSizeMultiplier;

    public RingAnimationController(float duration, final float ringSizeMultiplier) {
        super(duration, 1.10f);
        this.ringSizeMultiplier = ringSizeMultiplier;
    }

    public int getSize() {
        return (int)size;
    }
    public float getAlpha() {
        return (float)alpha;
    }


    @Override
    protected void animFrame(final float targetTime, final float deltaValue) {
        size += deltaValue * ringSizeMultiplier;

        if (getAnimTime() < targetTime/1.5f)
            alpha += deltaValue;
        else
            alpha -= deltaValue * 2;
    }

    @Override
    protected void resetAnimVars() {
        super.resetAnimVars();

        size = initSize;
        alpha = initAlpha;
    }

    
}