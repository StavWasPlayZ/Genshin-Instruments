package com.cstav.genshinstrument.capability.instrumentOpen;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.LazyOptional;

@AutoRegisterCapability
public class InstrumentOpen {
    public static final String OPEN_TAG = "instrumentOpen";
    public static final String IS_ITEM_TAG = "isItem";
    public static final String BLOCK_POS_TAG = "blockPos";

    private boolean isOpen = false, isItem = false;
    private BlockPos blockPos;

    public static boolean isOpen(final Player player) {
        final LazyOptional<InstrumentOpen> oIsOpen = player.getCapability(InstrumentOpenProvider.INSTRUMENT_OPEN);
        return oIsOpen.isPresent() && oIsOpen.resolve().get().isOpen;
    }


    public boolean isOpen() {
        return isOpen;
    }
    public boolean isItem() {
        return isItem;
    }
    public BlockPos getBlockPos() {
        return blockPos;
    }
    
    public void setOpen() {
        isOpen = true;
        isItem = true;
    }
    public void setOpen(final BlockPos blockPos) {
        isOpen = true;
        this.blockPos = blockPos;

        isItem = false;
    }

    public void setClosed() {
        isOpen = false;
    }
    public void setBlockPos(final BlockPos blockPos) {
        this.blockPos = blockPos;
    }


    public void saveNBTData(final CompoundTag nbt) {
        nbt.putBoolean(OPEN_TAG, isOpen);
        nbt.putBoolean(IS_ITEM_TAG, isOpen);

        if (blockPos != null)
            nbt.put(BLOCK_POS_TAG, NbtUtils.writeBlockPos(blockPos));
    }
    public void loadNBTData(final CompoundTag nbt) {
        isOpen = nbt.getBoolean(OPEN_TAG);
        isItem = nbt.getBoolean(IS_ITEM_TAG);

        if (nbt.contains(BLOCK_POS_TAG, CompoundTag.TAG_COMPOUND))
            blockPos = NbtUtils.readBlockPos(nbt.getCompound(BLOCK_POS_TAG));
    }
}
