package com.cstav.genshinstrument.capability.instrumentOpen;

import java.util.function.Function;

import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
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
        getInstance().loadNBTData(nbt);
    }
    

    public static void setOpen(final Player player, final BlockPos pos) {
        player.getCapability(InstrumentOpenProvider.INSTRUMENT_OPEN).ifPresent((instrumentOpen) ->
            instrumentOpen.setOpen(pos)
        );
    }
    public static void setOpen(Player player, InteractionHand hand) {
        player.getCapability(InstrumentOpenProvider.INSTRUMENT_OPEN).ifPresent((instrumentOpen) ->
            instrumentOpen.setOpen(hand)
        );
    }
    public static void setClosed(Player player) {
        player.getCapability(InstrumentOpenProvider.INSTRUMENT_OPEN).ifPresent(InstrumentOpen::setClosed);
    }

    public static void setBlockPos(Player player, BlockPos blockPos) {
        player.getCapability(InstrumentOpenProvider.INSTRUMENT_OPEN).ifPresent((instrumentOpen) ->
            instrumentOpen.setBlockPos(blockPos)
        );
    }

    public static boolean isOpen(final Player player) {
        return getInstrumentOpen(player, InstrumentOpen::isOpen, false);
    }
    public static boolean isItem(final Player player) {
        return getInstrumentOpen(player, InstrumentOpen::isItem, false);
    }

    public static BlockPos getBlockPos(final Player player) {
        return getInstrumentOpen(player, InstrumentOpen::getBlockPos, null);
    }
    public static InteractionHand getHand(final Player player) {
        return getInstrumentOpen(player, InstrumentOpen::getHand, null);
    }

    private static <T> T getInstrumentOpen(Player player, Function<InstrumentOpen, T> ifExists, T elseVal) {
        final LazyOptional<InstrumentOpen> lazyOpen = player.getCapability(InstrumentOpenProvider.INSTRUMENT_OPEN);

        return lazyOpen.isPresent()
            ? ifExists.apply(lazyOpen.resolve().get())
            : elseVal;
    }
}
