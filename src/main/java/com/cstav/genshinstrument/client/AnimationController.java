package com.cstav.genshinstrument.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AnimationController {

    private int animTime;
    private boolean isPlaying;

    protected final Minecraft minecraft = Minecraft.getInstance();
    protected final float duration, targetValue;
    /**
     * @param duration The duration of the animation in seconds
     */
    public AnimationController(final float duration, final float targetValue) {
        this.duration = duration;
        this.targetValue = targetValue;
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
    protected void resetAnimVars() {
        animTime = 0;
    }
    

    public boolean isPlaying() {
        return isPlaying;
    }

}
