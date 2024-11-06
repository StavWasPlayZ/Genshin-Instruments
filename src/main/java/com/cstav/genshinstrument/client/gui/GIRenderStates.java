package com.cstav.genshinstrument.client.gui;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.block.partial.AbstractInstrumentBlock;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.ModArmPose;
import com.cstav.genshinstrument.forgeimpl.CustomRenderStateField;
import com.cstav.genshinstrument.forgeimpl.GIForgeRegistries;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class GIRenderStates {

    public static final DeferredRegister<CustomRenderStateField<?>> STATES = DeferredRegister.create(GIForgeRegistries.KEY_CUSTOM_RENDER_STATES, GInstrumentMod.MODID);
    public static void register(final IEventBus bus) {
        STATES.register(bus);
    }

    public static final RegistryObject<CustomRenderStateField<Optional<ArmPose>>> INSTRUMENT_BLOCK_PLAYED = STATES.register("instrument_block_played", () ->
        new CustomRenderStateField<>(Player.class, GIRenderStates::extractInstrumentBlockPlayingState)
    );

    private static Optional<ArmPose> extractInstrumentBlockPlayingState(EntityRenderer<?, ?> entityRenderer,
                                                                        Entity entity,
                                                                        EntityRenderState state,
                                                                        float tickDelta) {
//        final Player player = (Player)entity;
//
//        if (!(InstrumentOpenProvider.isOpen(player) && !InstrumentOpenProvider.isItem(player)))
//            return Optional.empty();
//
//        final Block block = player.level().getBlockState(InstrumentOpenProvider.getBlockPos(player)).getBlock();
//        if (!(block instanceof AbstractInstrumentBlock instrumentBlock))
//            return Optional.empty();
//
//        return Optional.ofNullable(instrumentBlock.getClientBlockArmPose());
        return Optional.of(ModArmPose.PLAYING_BLOCK_INSTRUMENT);
    }
}
