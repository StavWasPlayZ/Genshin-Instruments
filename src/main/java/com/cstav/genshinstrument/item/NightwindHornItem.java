package com.cstav.genshinstrument.item;

import com.cstav.genshinstrument.client.ModArmPose;
import com.cstav.genshinstrument.event.PosePlayerArmEvent;
import com.cstav.genshinstrument.networking.OpenInstrumentPacketSender;

public class NightwindHornItem extends WindInstrumentItem {
    public NightwindHornItem(OpenInstrumentPacketSender onOpenRequest) {
        super(onOpenRequest);
    }
    public NightwindHornItem(OpenInstrumentPacketSender onOpenRequest, Properties properties) {
        super(onOpenRequest, properties);
    }

    @Override
    public void onPosePlayerArm(PosePlayerArmEvent args) {
        ModArmPose.poseForNightwindHornInstrument(args);
    }
}
