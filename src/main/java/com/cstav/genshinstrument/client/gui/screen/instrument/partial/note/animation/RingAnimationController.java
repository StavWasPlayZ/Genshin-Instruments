package com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.animation;

import com.cstav.genshinstrument.client.AnimationController;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteRing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RingAnimationController extends AnimationController {
    public static final float INIT_ALPHA = -.08f;

    protected final double initSize;
    protected final NoteRing ring;
    
    protected final double ringSizeMultiplier;

    public RingAnimationController(double duration, double ringSizeMultiplier, final NoteRing ring) {
        super(duration, 1.1);
        this.ringSizeMultiplier = ringSizeMultiplier;
        this.ring = ring;

        initSize = ring.note.instrumentScreen.getNoteSize() * .8;
    }


    private double dSize;

    @Override
    protected void animFrame(final double targetTime, final double deltaValue) {
        ring.size = (int)(dSize += deltaValue * ringSizeMultiplier);

        if (getAnimTime() < targetTime / 1.75f)
            ring.alpha += (float)(deltaValue * 1.5f);
        else
            ring.alpha -= (float)(deltaValue);
    }


    @Override
    protected void resetAnimVars() {
        super.resetAnimVars();

        ring.size = (int)(dSize = initSize);
        ring.alpha = INIT_ALPHA;
    }

    public void play(final float initAlpha) {
        this.play();
        ring.alpha = initAlpha;
    }
    
}