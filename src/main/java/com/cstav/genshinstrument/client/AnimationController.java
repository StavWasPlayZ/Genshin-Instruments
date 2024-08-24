package com.cstav.genshinstrument.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AnimationController {
    protected final Minecraft minecraft = Minecraft.getInstance();

    /**
     * The time the animation ran for.
     * Generally advised to not be modified.
     */
    protected int animTime;
    private boolean isPlaying;

    public final double initDuration, initTargetValue;
    protected double duration, targetValue;

    /**
     * @param duration The duration of the animation in seconds
     */
    public AnimationController(final double duration, final double targetValue) {
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
        final double targetTime = fps * duration;
            
        if (animTime++ >= targetTime) {
            stop();
            return;
        }
        
        animFrame(targetTime, targetValue / targetTime);
    }
    
    protected abstract void animFrame(final double targetTime, final double deltaValue);


    public void stop() {
        resetAnimVars();
        isPlaying = false;
    }

    public void play() {
        resetAnimVars();
        isPlaying = true;
    }
    public void play(final double duration, final float targetValue) {
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
