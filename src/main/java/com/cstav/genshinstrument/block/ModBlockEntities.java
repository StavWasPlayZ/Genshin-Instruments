package com.cstav.genshinstrument.block;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.block.partial.InstrumentBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

//NOTE: For testing purposes
public abstract class ModBlockEntities {
    
    public static final DeferredRegister<BlockEntityType<?>> BETS = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GInstrumentMod.MODID);
    public static void register(final IEventBus bus) {
        BETS.register(bus);
    }

    //NOTE: For testing purposes
    public static final RegistryObject<BlockEntityType<InstrumentBlockEntity>> INSTRUMENT_BE = BETS.register("instrument_be", () -> 
        BlockEntityType.Builder.of((pos, state) -> new InstrumentBlockEntity(pos, state), ModBlocks.LYRE_BLOCK.get())
            .build(null)
    );
}
