package com.cstav.genshinstrument.networking.packet.instrument;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.networking.IModPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class NotifyInstrumentOpenPacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;


    private final UUID playerUUID;
    private final boolean isOpen;
    private final Optional<BlockPos> pos;
    
    public NotifyInstrumentOpenPacket(UUID playerUUID, final boolean isOpen) {
        this.playerUUID = playerUUID;

        this.isOpen = isOpen;
        this.pos = Optional.empty();
    }
    /**
     * Constructs a {@link NotifyInstrumentOpenPacket} that notifies of an open state
     * with an optional instrument block position.
     */
    public NotifyInstrumentOpenPacket(UUID playerUUID, Optional<BlockPos> pos) {
        this.playerUUID = playerUUID;

        this.isOpen = true;
        this.pos = pos;
    }
    
    public NotifyInstrumentOpenPacket(final FriendlyByteBuf buf) {
        playerUUID = buf.readUUID();
        isOpen = buf.readBoolean();
        pos = buf.readOptional(FriendlyByteBuf::readBlockPos);
    }
    
    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(playerUUID);
        buf.writeBoolean(isOpen);
        buf.writeOptional(pos, FriendlyByteBuf::writeBlockPos);
    }


    @SuppressWarnings("resource")
    @Override
    public void handle(Supplier<Context> supplier) {
        final Context context = supplier.get();

        context.enqueueWork(() -> {
            final Player player = Minecraft.getInstance().level.getPlayerByUUID(playerUUID);

            if (isOpen) {

                if (pos.isPresent())
                    InstrumentOpenProvider.setOpen(player, pos.get());
                else
                    InstrumentOpenProvider.setOpen(player);

            } else
                InstrumentOpenProvider.setClosed(player);

        });

        context.setPacketHandled(true);
    }
    
}
