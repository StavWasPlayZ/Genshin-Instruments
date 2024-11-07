package com.cstav.genshinstrument.client.gui;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.block.partial.AbstractInstrumentBlock;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.forgeimpl.CustomRenderStateField;
import com.cstav.genshinstrument.forgeimpl.GICustomRenderStateFieldRegistry;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

/**
 * Custom render fields for the Genshin Instruments mod.
 * Later found a different way to approach the issue, so this is just for demonstration purposes now.
 */
@OnlyIn(Dist.CLIENT)
public class GIRenderStates {

    public static void load() {}

    public static final CustomRenderStateField<Optional<ArmPose>> INSTRUMENT_BLOCK_PLAYED = GICustomRenderStateFieldRegistry.register(
        GInstrumentMod.loc("instrument_block_played"),
        new CustomRenderStateField<>(
            Player.class,
            GIRenderStates::extractInstrumentBlockPlayingState
        )
    );

    private static Optional<ArmPose> extractInstrumentBlockPlayingState(EntityRenderer<?, ?> entityRenderer,
                                                                        Entity entity,
                                                                        EntityRenderState state,
                                                                        float packedLight) {
        final Player player = (Player)entity;

        if (!(InstrumentOpenProvider.isOpen(player) && !InstrumentOpenProvider.isItem(player)))
            return Optional.empty();

        final Block block = player.level().getBlockState(InstrumentOpenProvider.getBlockPos(player)).getBlock();
        if (!(block instanceof AbstractInstrumentBlock instrumentBlock))
            return Optional.empty();

        return Optional.ofNullable(instrumentBlock.getClientBlockArmPose());
    }
}
