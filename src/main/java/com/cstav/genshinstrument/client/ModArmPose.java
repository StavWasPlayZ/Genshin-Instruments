package com.cstav.genshinstrument.client;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.event.PosePlayerArmEvent;
import com.cstav.genshinstrument.event.PosePlayerArmEvent.HandType;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ModArmPose {
    public static final float HAND_HEIGHT_ROT = .9f;

    public static void load() {}


    public static void poseForItemInstrument(final PosePlayerArmEvent event) {
        if (!InstrumentOpenProvider.isOpen(event.player) || !InstrumentOpenProvider.isItem(event.player))
            return;

        final ModelPart arm = event.arm;
        if (event.hand == HandType.LEFT) {
            arm.xRot = -HAND_HEIGHT_ROT;
            arm.zRot = 0.85f;
        } else {
            arm.xRot = -HAND_HEIGHT_ROT;
            arm.zRot = -0.35f;
        }

        event.setCanceled(true);
    }

    public static void poseForWindInstrument(final PosePlayerArmEvent args) {
        public static void poseForBlockInstrument(final PosePlayerArmEvent args) {
            if (args.hand == HandType.RIGHT) {
                args.arm.xRot = -1.5f;
                args.arm.zRot = -0.35f;
                args.arm.yRot = -0.5f;
            } else {
                args.arm.xRot = -1.5f;
                args.arm.zRot = 0.55f;
                args.arm.yRot = 0.5f;
            }

            args.setCanceled(true);
        }

        args.setCanceled(true);
    }


    public static void poseForBlockInstrument(final PosePlayerArmEvent args) {
        args.arm.xRot = -HAND_HEIGHT_ROT;

        args.setCanceled(true);
    }

}
