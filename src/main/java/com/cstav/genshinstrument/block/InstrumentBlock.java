package com.cstav.genshinstrument.block;

import com.cstav.genshinstrument.networking.OpenInstrumentPacketSender;
import com.cstav.genshinstrument.util.ServerUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class InstrumentBlock extends Block {

    private final OpenInstrumentPacketSender onOpenRequest;
    /**
     * @param onOpenRequest A server-side event fired when the player has requested to interact
     * with the instrument.
     * It should should send a packet to the given player for opening this instrument's screen.
     */
    public InstrumentBlock(Properties pProperties, OpenInstrumentPacketSender onOpenRequest) {
        super(pProperties);
        this.onOpenRequest = onOpenRequest;
    }


    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
            BlockHitResult pHit) {
        
        return pLevel.isClientSide ? InteractionResult.CONSUME

            : ServerUtil.sendOpenRequest((ServerPlayer)pPlayer, pHand, onOpenRequest)
                ? InteractionResult.SUCCESS
                : InteractionResult.FAIL;
    }
}
