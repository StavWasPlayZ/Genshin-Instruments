package com.cstav.genshinstrument.item;

import com.cstav.genshinstrument.client.ModArmPose;
import com.cstav.genshinstrument.event.PosePlayerArmEvent;
import com.cstav.genshinstrument.networking.OpenInstrumentPacketSender;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WindInstrumentItem extends InstrumentItem {

    public WindInstrumentItem(OpenInstrumentPacketSender onOpenRequest) {
        super(onOpenRequest);
    }
    public WindInstrumentItem(OpenInstrumentPacketSender onOpenRequest, Properties properties) {
        super(onOpenRequest, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onPosePlayerArm(final PosePlayerArmEvent args) {
        ModArmPose.poseForWindInstrument(args);
    }

}
