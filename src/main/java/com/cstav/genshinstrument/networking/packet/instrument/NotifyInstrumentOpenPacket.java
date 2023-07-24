package com.cstav.genshinstrument.networking.packet.instrument;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.networking.ModPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class NotifyInstrumentOpenPacket implements ModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;


    private final UUID playerUUID;
    private final boolean isOpen;
    private final Optional<BlockPos> pos;
    
    public NotifyInstrumentOpenPacket(UUID playerUUID, boolean isOpen, Optional<BlockPos> pos) {
        this.playerUUID = playerUUID;
        this.isOpen = isOpen;
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
    public boolean handle(Supplier<Context> supplier) {
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

        return true;
    }
    
}
