package com.cstav.genshinstrument.client;

import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
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

    public static final ArmPose PLAYING_BLOCK_INSTRUMENT = ArmPose.create("playing_block_instrument", true,
        (model, entity, arm) -> {
            model.rightArm.xRot = -HAND_HEIGHT_ROT;

            model.leftArm.xRot = -HAND_HEIGHT_ROT;
        }
    );


    /**Applies the default*/
    private static void defRightWind(ModelPart arm) {
        arm.xRot = -1.5f;
        arm.zRot = -0.35f;
        arm.yRot = -0.5f;
    }
    private static void defLeftWind(ModelPart arm) {
        arm.xRot = -1.5f;
        arm.zRot = 0.55f;
        arm.yRot = 0.5f;
    }

    public static final ArmPose PLAYING_WIND_INSTRUMENT = ArmPose.create("playing_wind_instrument", true,
        (model, entity, arm) -> {
            defRightWind(model.rightArm);
            defLeftWind(model.leftArm);
        }
    );
    public static final ArmPose PLAYING_NIGHTWIND_HORN_INSTRUMENT = ArmPose.create("playing_nightwind_horn_instrument", true,
        (model, entity, arm) -> {
            if (arm == HumanoidArm.RIGHT) {
                defRightWind(model.rightArm);

                model.leftArm.xRot = -1.65f;
                model.leftArm.zRot = -0.1f;
                model.leftArm.yRot = -0.1f;
            } else {
                defLeftWind(model.leftArm);

                model.rightArm.xRot = -1.65f;
                model.rightArm.zRot = 0.1f;
                model.rightArm.yRot = 0;
            }
        }
    );

}
