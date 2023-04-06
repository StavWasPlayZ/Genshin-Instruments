package com.cstav.genshinstrument.capability.lyreOpen;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class LyreOpenProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<LyreOpen> LYRE_OPEN = CapabilityManager.get(new CapabilityToken<>() {});

    private LyreOpen lyreOpen;
    private final LazyOptional<LyreOpen> optional = LazyOptional.of(this::getLyreOpenInstance);
    private @NotNull LyreOpen getLyreOpenInstance() {
        return (lyreOpen == null) ?
            (lyreOpen = new LyreOpen()) :
            lyreOpen;
    }


    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return LYRE_OPEN.orEmpty(cap, optional);
    }

    @Override
    public CompoundTag serializeNBT() {
        final CompoundTag nbt = new CompoundTag();
        getLyreOpenInstance().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(final CompoundTag nbt) {
        getLyreOpenInstance().loadNBTData(nbt);
    }
    
}
