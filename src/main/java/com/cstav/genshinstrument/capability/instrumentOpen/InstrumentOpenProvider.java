package com.cstav.genshinstrument.capability.instrumentOpen;

import java.util.function.Function;

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

    private InstrumentOpen instrumentOpen;
    private final LazyOptional<InstrumentOpen> optional = LazyOptional.of(this::getInstance);
    private @NotNull InstrumentOpen getInstance() {
        return (instrumentOpen == null) ?
            (instrumentOpen = new InstrumentOpen()) :
            instrumentOpen;
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
        System.out.println("nbt is here: "+nbt);
        getInstance().loadNBTData(nbt);
    }
    

    public static void setOpen(Player player, boolean isOpen, boolean isItem) {
        player.getCapability(InstrumentOpenProvider.INSTRUMENT_OPEN).ifPresent((instrumentOpen) ->
            instrumentOpen.setOpen(isOpen, isItem)
        );
    }
    public static void setClosed(Player player) {
        player.getCapability(InstrumentOpenProvider.INSTRUMENT_OPEN).ifPresent((instrumentOpen) ->
            instrumentOpen.setClosed()
        );
    }


    public static boolean isOpen(final Player player) {
        return getInstrumentOpen(player, InstrumentOpen::isOpen);
    }
    public static boolean isItem(final Player player) {
        return getInstrumentOpen(player, InstrumentOpen::isItem);
    }

    private static final boolean getInstrumentOpen(Player player, Function<InstrumentOpen, Boolean> ifExists) {
        final LazyOptional<InstrumentOpen> lazyOpen = player.getCapability(InstrumentOpenProvider.INSTRUMENT_OPEN);
        return lazyOpen.isPresent() && ifExists.apply(lazyOpen.resolve().get());
    }
}
