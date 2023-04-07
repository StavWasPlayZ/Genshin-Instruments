package com.cstav.genshinstrument.capability.instrumentOpen;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public class InstrumentOpen {
    public static final String NBT_KEY = "instrumentOpen";
    private boolean isOpen = false;

    public boolean isOpen() {
        return isOpen;
    }
    public void setOpen(final boolean isOpen) {
        this.isOpen = isOpen;
    }

    public void saveNBTData(final CompoundTag nbt) {
        nbt.putBoolean(NBT_KEY, isOpen);
    }
    public void loadNBTData(final CompoundTag nbt) {
        isOpen = nbt.getBoolean(NBT_KEY);
    }
}
