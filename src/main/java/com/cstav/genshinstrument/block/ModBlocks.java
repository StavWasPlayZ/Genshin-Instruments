package com.cstav.genshinstrument.block;

import com.cstav.genshinstrument.GInstrumentMod;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GInstrumentMod.MODID);

    public static void register(final IEventBus bus) {
        BLOCKS.register(bus);
    }

    //NOTE: For testing purposes
    public static final RegistryObject<Block> LYRE_BLOCK = BLOCKS.register("lyre_block", () ->
        new LyreInstrumentBlock(Properties.copy(Blocks.OAK_WOOD))
    );
}
