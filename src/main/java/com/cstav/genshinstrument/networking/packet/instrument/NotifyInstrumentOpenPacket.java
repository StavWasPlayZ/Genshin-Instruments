package com.cstav.genshinstrument.networking.packet.instrument;

import java.util.Optional;
import java.util.UUID;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.networking.IModPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class NotifyInstrumentOpenPacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;


    private final UUID playerUUID;
    private final boolean isOpen;
    private final Optional<BlockPos> pos;
    private final Optional<InteractionHand> hand;

    /**
     * Constructs packet notifying of a closed instrument
     */
    public NotifyInstrumentOpenPacket(UUID playerUUID) {
        this.playerUUID = playerUUID;

        this.isOpen = false;
        this.pos = Optional.empty();
        this.hand = Optional.empty();
    }
    public NotifyInstrumentOpenPacket(UUID playerUUID, BlockPos pos) {
        this.playerUUID = playerUUID;

        this.isOpen = true;
        this.pos = Optional.of(pos);
        this.hand = Optional.empty();
    }
    public NotifyInstrumentOpenPacket(UUID playerUUID, InteractionHand hand) {
        this.playerUUID = playerUUID;

        this.isOpen = true;
        this.pos = Optional.empty();
        this.hand = Optional.of(hand);
    }
    
    public NotifyInstrumentOpenPacket(final FriendlyByteBuf buf) {
        playerUUID = buf.readUUID();
        isOpen = buf.readBoolean();
        pos = buf.readOptional(FriendlyByteBuf::readBlockPos);
        hand = buf.readOptional((fbb) -> fbb.readEnum(InteractionHand.class));
    }
    
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(playerUUID);
        buf.writeBoolean(isOpen);
        buf.writeOptional(pos, FriendlyByteBuf::writeBlockPos);
        buf.writeOptional(hand, FriendlyByteBuf::writeEnum);
    }


    @SuppressWarnings("resource")
    @Override
    public void handle(final Context context) {
        final Player player = Minecraft.getInstance().level.getPlayerByUUID(playerUUID);

        if (isOpen) {

            if (pos.isPresent()) // is block instrument
                InstrumentOpenProvider.setOpen(player, pos.get());
            else
                InstrumentOpenProvider.setOpen(player, hand.get());

        } else
            InstrumentOpenProvider.setClosed(player);
    }
    
}
