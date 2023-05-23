package com.cstav.genshinstrument.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class GuiAnimationController {

    private AnimationState animState = AnimationState.IDLE;
    private int animTime;

    protected final Minecraft minecraft = Minecraft.getInstance();
    protected final float duration, targetValue;
    /**
     * @param duration The duration of the animation in seconds
     */
    public GuiAnimationController(final float duration, final float targetValue) {
        this.duration = duration;
        this.targetValue = targetValue;
    }

    public AnimationState getAnimState() {
        return animState;
    }


    public void update() {
        if (!isPlaying())
            return;

        final int fps = minecraft.getFps();
        final float targetTime = fps * duration;
            
        if (animTime++ >= targetTime/2) {
            animTime = 0;

            if (animState == AnimationState.END) {
                stop();
                return;
            }
            animState = AnimationState.END;
        }
        
        animFrame(targetValue / targetTime);
    }
    
    protected abstract void animFrame(final float deltaValue);

    public void stop() {
        resetAnimVars();
        animState = AnimationState.IDLE;
    }
    public void start() {
        resetAnimVars();
        animState = AnimationState.START;
    }
    protected void resetAnimVars() {
        animTime = 0;
    }
    

    public boolean isPlaying() {
        return animState != AnimationState.IDLE;
    }


    @OnlyIn(Dist.CLIENT)
    public static enum AnimationState {
        START, END, IDLE
    }

}
