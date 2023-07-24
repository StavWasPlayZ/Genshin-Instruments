package com.cstav.genshinstrument.block.partial.client;

import com.cstav.genshinstrument.item.clientExtensions.ClientInstrumentItem;

import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InstrumentClientBlockArmPose implements IClientBlockArmPose {

    @Override
    public ArmPose getArmPose() {
        return ClientInstrumentItem.PLAYING_BLOCK_INSTRUMENT;
    }
    
}
