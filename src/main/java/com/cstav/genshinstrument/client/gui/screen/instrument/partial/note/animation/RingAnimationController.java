package com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.animation;

import com.cstav.genshinstrument.client.AnimationController;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteRing;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RingAnimationController extends AnimationController {

    protected final double initSize;
    protected final float initAlpha = -.08f;
    protected final NoteRing ring;
    
    protected final float ringSizeMultiplier;

    public RingAnimationController(float duration, final float ringSizeMultiplier, final NoteRing ring) {
        super(duration, 1.1f);
        this.ringSizeMultiplier = ringSizeMultiplier;
        this.ring = ring;

        initSize = ring.note.instrumentScreen.getNoteSize() * .8;
    }


    private double dSize;

    @Override
    protected void animFrame(final float targetTime, final float deltaValue) {
        ring.size = (int)(dSize += deltaValue * ringSizeMultiplier);

        if (getAnimTime() < targetTime / 1.75f)
            ring.alpha += deltaValue * 1.5f;
        else
            ring.alpha -= deltaValue;
    }


    @Override
    protected void resetAnimVars() {
        super.resetAnimVars();

        ring.size = (int)(dSize = initSize);
        ring.alpha = initAlpha;
    }

    public void play(final float initAlpha) {
        this.play();
        ring.alpha = initAlpha;
    }
    
}