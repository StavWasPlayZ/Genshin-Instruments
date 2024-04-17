package com.cstav.genshinstrument.client;

import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ModArmPose {
    public static final float HAND_HEIGHT_ROT = .9f;

    public static void load() {}


    public static final ArmPose PLAYING_ITEM_INSTRUMENT = ArmPose.create("playing_item_instrument", true,
        (model, entity, arm) -> {
            model.rightArm.xRot = -HAND_HEIGHT_ROT;
            model.rightArm.zRot = -0.35f;

            model.leftArm.xRot = -HAND_HEIGHT_ROT;
            model.leftArm.zRot = 0.85f;
        }
    );

    public static final ArmPose PLAYING_WIND_INSTRUMENT = ArmPose.create("playing_trombone_instrument", true,
        (model, entity, arm) -> {
            model.rightArm.xRot = -1.5f;
            model.rightArm.zRot = -0.35f;
            model.rightArm.yRot = -0.5f;

            model.leftArm.xRot = -1.5f;
            model.leftArm.zRot = 0.55f;
            model.leftArm.yRot = 0.5f;
        }
    );


    public static final ArmPose PLAYING_BLOCK_INSTRUMENT = ArmPose.create("playing_block_instrument", true,
        (model, entity, arm) -> {
            model.rightArm.xRot = -HAND_HEIGHT_ROT;

            model.leftArm.xRot = -HAND_HEIGHT_ROT;
        }
    );

}
