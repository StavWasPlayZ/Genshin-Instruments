package com.cstav.genshinstrument.block.partial;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class InstrumentBlockEntity extends BlockEntity {

    public Set<UUID> users = new HashSet<UUID>();

    public InstrumentBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    // //NOTE: For testing purposes
    // public InstrumentBlockEntity(BlockPos pPos, BlockState pBlockState) {
    //     super(ModBlockEntities.INSTRUMENT_BE.get(), pPos, pBlockState);
    // }
    
}
