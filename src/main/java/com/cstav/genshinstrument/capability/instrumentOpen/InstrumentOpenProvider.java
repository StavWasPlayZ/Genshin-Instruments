package com.cstav.genshinstrument.capability.instrumentOpen;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class InstrumentOpenProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<InstrumentOpen> INSTRUMENT_OPEN = CapabilityManager.get(new CapabilityToken<>() {});

    private InstrumentOpen lyreOpen;
    private final LazyOptional<InstrumentOpen> optional = LazyOptional.of(this::getInstance);
    private @NotNull InstrumentOpen getInstance() {
        return (lyreOpen == null) ?
            (lyreOpen = new InstrumentOpen()) :
            lyreOpen;
    }


    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return INSTRUMENT_OPEN.orEmpty(cap, optional);
    }

    @Override
    public CompoundTag serializeNBT() {
        final CompoundTag nbt = new CompoundTag();
        getInstance().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(final CompoundTag nbt) {
        getInstance().loadNBTData(nbt);
    }
    

    public static void setOpen(final Player player, final boolean isOpen) {
        player.getCapability(InstrumentOpenProvider.INSTRUMENT_OPEN).ifPresent((lyreOpen) ->
            lyreOpen.setOpen(isOpen)
        );
    }
}
