package com.cstav.genshinstrument.item.clientExtensions;

import javax.annotation.Nullable;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpen;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IArmPoseTransformer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.util.LazyOptional;

public class ClientInstrumentItem implements IClientItemExtensions {
    
    public static final float HAND_HEIGHT_ROT = .9f;
    public static final ArmPose PLAYING_INSTRUMENT = ArmPose.create("playing_instrument", true, new IArmPoseTransformer() {

        @Override
        public void applyTransform(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm) {
            model.rightArm.xRot = -HAND_HEIGHT_ROT;
            model.rightArm.zRot = -0.35f;

            model.leftArm.xRot = -HAND_HEIGHT_ROT;
            model.leftArm.zRot = 0.85f;
        }
        
    });


    @Override
    public @Nullable ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
        if (!(entityLiving instanceof Player))
            return null;
        final Player player = (Player)entityLiving;
        
        final LazyOptional<InstrumentOpen> lazyOpen = player.getCapability(InstrumentOpenProvider.INSTRUMENT_OPEN);
        if (!lazyOpen.isPresent() || !lazyOpen.resolve().get().isOpen())
            return null;


        return PLAYING_INSTRUMENT;
    }
}
