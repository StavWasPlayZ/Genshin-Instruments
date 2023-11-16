package com.cstav.genshinstrument.item;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.event.PosePlayerArmEvent;
import com.cstav.genshinstrument.util.CommonUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.FORGE, modid = GInstrumentMod.MODID, value = Dist.CLIENT)
public interface ItemPoseModifier {

    @OnlyIn(Dist.CLIENT)
    void onPosePlayerArm(final PosePlayerArmEvent args);

}
