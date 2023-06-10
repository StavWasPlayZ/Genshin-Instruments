package com.cstav.genshinstrument.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AnimationController {
    protected final Minecraft minecraft = Minecraft.getInstance();

    private int animTime;
    private boolean isPlaying;

    public final float initDuration, initTargetValue;
    protected float duration, targetValue;

    /**
     * @param duration The duration of the animation in seconds
     */
    public AnimationController(final float duration, final float targetValue) {
        this.duration = initDuration = duration;
        this.targetValue = initTargetValue = targetValue;
    }

    public int getAnimTime() {
        return animTime;
    }


    public void update() {
        if (!isPlaying())
            return;

        final int fps = minecraft.getFps();
        final float targetTime = fps * duration;
            
        if (animTime++ >= targetTime) {
            stop();
            return;
        }
        
        animFrame(targetTime, targetValue / targetTime);
    }
    
    protected abstract void animFrame(final float targetTime, final float deltaValue);


    public void stop() {
        resetAnimVars();
        isPlaying = false;
    }

    public void play() {
        resetAnimVars();
        isPlaying = true;
    }
    public void play(final float duration, final float targetValue) {
        this.duration = duration;
        this.targetValue = targetValue;
    }



    protected void resetAnimVars() {
        duration = initDuration;
        targetValue = initTargetValue;

        animTime = 0;
    }
    

    public boolean isPlaying() {
        return isPlaying;
    }

}
