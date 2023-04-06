package com.cstav.genshinstrument.capability.lyreOpen;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public class LyreOpen {
    public static final String NBT_KEY = "lyreOpen";
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
