package com.cstav.genshinstrument.capability.instrumentOpen;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.LazyOptional;

@AutoRegisterCapability
public class InstrumentOpen {
    public static final String
        OPEN_TAG = "instrumentOpen",
        IS_ITEM_TAG = "isItem",
        BLOCK_POS_TAG = "blockPos",
        HAND_TAG = "inOffhand"
    ;

    private boolean isOpen = false, isItem = false;
    private BlockPos blockPos;
    private InteractionHand hand;

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

    /**
     * The position of the instrument block.
     * Present only for when {@link InstrumentOpen#isItem() not an item}.
     */
    public BlockPos getBlockPos() {
        return blockPos;
    }
    /**
     * The hand holding the instrument.
     * Present only for when {@link InstrumentOpen#isItem() not an item}.
     */
    public InteractionHand getHand() {
        return hand;
    }
    
    public void setOpen(final InteractionHand hand) {
        isOpen = true;

        this.hand = hand;
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
        nbt.putBoolean(IS_ITEM_TAG, isItem);

        if (blockPos != null)
            nbt.put(BLOCK_POS_TAG, NbtUtils.writeBlockPos(blockPos));
        if (hand != null)
            nbt.putBoolean(HAND_TAG, hand == InteractionHand.OFF_HAND);
    }
    public void loadNBTData(final CompoundTag nbt) {
        isOpen = nbt.getBoolean(OPEN_TAG);
        isItem = nbt.getBoolean(IS_ITEM_TAG);

        if (nbt.contains(BLOCK_POS_TAG, Tag.TAG_COMPOUND))
            blockPos = NbtUtils.readBlockPos(nbt.getCompound(BLOCK_POS_TAG));
        if (nbt.contains(HAND_TAG, Tag.TAG_BYTE))
            hand = nbt.getBoolean(HAND_TAG) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }
}
