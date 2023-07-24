package com.cstav.genshinstrument.capability.instrumentOpen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.LazyOptional;

@AutoRegisterCapability
public class InstrumentOpen {
    public static final String OPEN_TAG = "instrumentOpen";
    public static final String IS_ITEM_TAG = "isItem";
    private boolean isOpen = false, isItem = false;

    public static boolean isOpen(final Player player) {
        final LazyOptional<InstrumentOpen> oIsOpen = player.getCapability(InstrumentOpenProvider.INSTRUMENT_OPEN);
        return oIsOpen.isPresent() && oIsOpen.resolve().get().isOpen;
    }


    public boolean isOpen() {
        return isOpen;
    }
    
    public void setOpen(final boolean isOpen, final boolean isItem) {
        this.isOpen = isOpen;
        this.isItem = isItem;
    }
    public void setClosed() {
        isOpen = false;
    }

    public boolean isItem() {
        return isItem;
    }


    public void saveNBTData(final CompoundTag nbt) {
        nbt.putBoolean(OPEN_TAG, isOpen);
        nbt.putBoolean(IS_ITEM_TAG, isOpen);
    }
    public void loadNBTData(final CompoundTag nbt) {
        isOpen = nbt.getBoolean(OPEN_TAG);
        isItem = nbt.getBoolean(IS_ITEM_TAG);
    }
}
