package com.cstav.genshinstrument.mixins.optional;

import com.cstav.genshinstrument.event.PosePlayerArmEvent;
import com.cstav.genshinstrument.event.PosePlayerArmEvent.HandType;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class InstrumentAnimMixin {

    @Final
    @Shadow
    public ModelPart rightArm;
    @Final
    @Shadow
    public ModelPart leftArm;

    @Unique
    @SuppressWarnings("unchecked")
    public HumanoidModel<Player> self() {
        return(HumanoidModel<Player>) ((Object)this);
    }


    @Inject(at = @At("HEAD"), method = "poseLeftArm", cancellable = true)
    private void injectLeftArmPose(LivingEntity entity, CallbackInfo info) {
        if (!(entity instanceof Player player))
            return;

        final PosePlayerArmEvent event = new PosePlayerArmEvent(player, self(), HandType.LEFT, leftArm);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled())
            info.cancel();
    }

    @Inject(at = @At("HEAD"), method = "poseRightArm", cancellable = true)
    private void injectRightArmPose(LivingEntity entity, CallbackInfo info) {
        if (!(entity instanceof Player player))
            return;

        final PosePlayerArmEvent event = new PosePlayerArmEvent(player, self(), HandType.RIGHT, rightArm);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled())
            info.cancel();
    }

}