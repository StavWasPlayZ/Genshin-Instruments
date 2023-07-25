package com.cstav.genshinstrument.block;

import com.cstav.genshinstrument.block.partial.AbstractInstrumentBlock;
import com.cstav.genshinstrument.block.partial.InstrumentBlockEntity;
import com.cstav.genshinstrument.networking.OpenInstrumentPacketSender;
import com.cstav.genshinstrument.util.ServerUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;


//TODO remove after tests
public class LyreInstrumentBlock extends AbstractInstrumentBlock {

    public LyreInstrumentBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InstrumentBlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new InstrumentBlockEntity(pPos, pState);
    }

    @Override
    protected OpenInstrumentPacketSender instrumentPacketSender() {
        return (player, hand) -> ServerUtil.sendInternalOpenPacket(player, hand, "windsong_lyre");
    }
    
}
