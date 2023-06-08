package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.animation;

import com.cstav.genshinstrument.client.AnimationController;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteRing;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RingAnimationController extends AnimationController {

    protected final double initSize = NoteButton.getSize() * .8, initAlpha = -.08;
    protected final NoteRing ring;

    protected final float ringSizeMultiplier;

    public RingAnimationController(float duration, final float ringSizeMultiplier, final NoteRing ring) {
        super(duration, 1.1f);
        this.ringSizeMultiplier = ringSizeMultiplier;
        this.ring = ring;
    }


    @Override
    protected void animFrame(final float targetTime, final float deltaValue) {
        ring.size += deltaValue * ringSizeMultiplier;

        if (getAnimTime() < targetTime / 1.75f)
            ring.alpha += deltaValue * 1.5f;
        else
            ring.alpha -= deltaValue;
    }

    @Override
    protected void resetAnimVars() {
        super.resetAnimVars();

        ring.size = (int)initSize;
        ring.alpha = (int)initAlpha;
    }

    
}