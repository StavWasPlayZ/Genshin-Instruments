package com.cstav.genshinstrument.block.partial;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.ModArmPose;
import com.cstav.genshinstrument.event.PosePlayerArmEvent;
import com.cstav.genshinstrument.networking.GIPacketHandler;
import com.cstav.genshinstrument.networking.packet.instrument.NotifyInstrumentOpenPacket;
import com.cstav.genshinstrument.util.ServerUtil;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.NotifyInstrumentOpenPacket;
import com.cstav.genshinstrument.networking.packet.instrument.util.InstrumentPacketUtil;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractInstrumentBlock extends BaseEntityBlock {

    public AbstractInstrumentBlock(Properties pProperties) {
        super(pProperties);
    }

    
    // Abstract implementations

    /**
     * A server-side event fired when the player has interacted with the instrument.
     * It should send a packet to the given player for opening this instrument's screen.
     */
    protected abstract void onInstrumentOpen(final ServerPlayer player);
    @Override
    public abstract InstrumentBlockEntity newBlockEntity(BlockPos pPos, BlockState pState);


    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
            BlockHitResult pHit) {        
        if (pLevel.isClientSide)
                return InteractionResult.CONSUME;


        final BlockEntity be = pLevel.getBlockEntity(pPos);
        if (!(be instanceof InstrumentBlockEntity))
            return InteractionResult.FAIL;

        if (InstrumentPacketUtil.sendOpenPacket((ServerPlayer)pPlayer, this::onInstrumentOpen, pPos)) {
            ((InstrumentBlockEntity)be).users.add(pPlayer.getUUID());
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        final BlockEntity be = pLevel.getBlockEntity(pPos);
        if (!(be instanceof InstrumentBlockEntity))
            return;


        final InstrumentBlockEntity ibe = (InstrumentBlockEntity)be;
        
        for (final Player player : pLevel.players()) {
            ibe.users.forEach((user) -> {
                InstrumentOpenProvider.setClosed(pLevel.getPlayerByUUID(user));
                GIPacketHandler.sendToClient(new NotifyInstrumentOpenPacket(user), (ServerPlayer)player);
            });
        }
    }


    @OnlyIn(Dist.CLIENT)
    public void onPosePlayerArm(PosePlayerArmEvent args) {
        ModArmPose.poseForBlockInstrument(args);
    }

}
