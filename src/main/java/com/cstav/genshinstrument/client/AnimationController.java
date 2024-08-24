package com.cstav.genshinstrument.client;

import java.lang.reflect.Field;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

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

        final int fps = getFps();
        final double targetTime = fps * duration;
            
        if (animTime++ >= targetTime) {
            stop();
            return;
        }
        
        animFrame(targetTime, targetValue / targetTime);
    }
    
    private static int getFps() {
        try {
            final Field fps = ObfuscationReflectionHelper.findField(Minecraft.class, "f_91021_");
            fps.setAccessible(true);
            return fps.getInt(Minecraft.getInstance());
        } catch (Exception e) {
            LogUtils.getLogger().error("Exception occured during the proccess of getting FPS! Defaulting to 90", e);
            return 90;
        }
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
