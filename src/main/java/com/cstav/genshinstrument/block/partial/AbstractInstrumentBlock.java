package com.cstav.genshinstrument.block.partial;

import java.util.function.Consumer;

import com.cstav.genshinstrument.block.partial.client.IClientBlockArmPose;
import com.cstav.genshinstrument.block.partial.client.InstrumentClientBlockArmPose;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.OpenInstrumentPacketSender;
import com.cstav.genshinstrument.networking.packet.instrument.NotifyInstrumentClosedPacket;
import com.cstav.genshinstrument.util.ServerUtil;

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
import net.minecraftforge.fml.loading.FMLEnvironment;

public abstract class AbstractInstrumentBlock extends BaseEntityBlock {

    /**
     * @param onOpenRequest A server-side event fired when the player has requested to interact
     * with the instrument.
     * It should should send a packet to the given player for opening this instrument's screen.
     */
    public AbstractInstrumentBlock(Properties pProperties) {
        super(pProperties);

        if (!FMLEnvironment.dist.isDedicatedServer())
            initClientBlockUseAnim((pose) -> clientBlockArmPose = pose);
    }

    
    // Abstract implementations
    protected abstract OpenInstrumentPacketSender instrumentPacketSender();
    @Override
    public abstract InstrumentBlockEntity newBlockEntity(BlockPos pPos, BlockState pState);
    
    /**
     * An instance of {@link IClientBlockArmPose} as defined in {@link AbstractInstrumentBlock#initClientBlockUseAnim}
     */
    private Object clientBlockArmPose;
    protected void initClientBlockUseAnim(final Consumer<IClientBlockArmPose> consumer) {
        consumer.accept(new InstrumentClientBlockArmPose());
    }
    @OnlyIn(Dist.CLIENT)
    public IClientBlockArmPose getClientBlockArmPose() {
        return (IClientBlockArmPose)clientBlockArmPose;
    }


    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
            BlockHitResult pHit) {        
        if (pLevel.isClientSide)
                return InteractionResult.CONSUME;


        final BlockEntity be = pLevel.getBlockEntity(pPos);
        if (!(be instanceof InstrumentBlockEntity))
            return InteractionResult.FAIL;

        if (ServerUtil.sendOpenPacket((ServerPlayer)pPlayer, instrumentPacketSender(), pPos)) {
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
                ModPacketHandler.sendToClient(new NotifyInstrumentClosedPacket(user), (ServerPlayer)player);
            });
        }
    }

}
