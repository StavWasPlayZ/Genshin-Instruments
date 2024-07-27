package com.cstav.genshinstrument.networking.packet.instrument.s2c;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.event.InstrumentOpenStateEvent;
import com.cstav.genshinstrument.networking.IModPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.Optional;
import java.util.UUID;

/**
 * A S2C packet to update the {@code instrument open} state
 * of a certain player
 */
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

    /**
     * Constructs packet notifying of an open block instrument
     * at the provided position
     */
    public NotifyInstrumentOpenPacket(UUID playerUUID, BlockPos pos) {
        this.playerUUID = playerUUID;

        this.isOpen = true;
        this.pos = Optional.of(pos);
        this.hand = Optional.empty();
    }
    /**
     * Constructs packet notifying of an open item instrument
     * at the specified hand
     */
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

        } else {
            InstrumentOpenProvider.setClosed(player);
        }

        MinecraftForge.EVENT_BUS.post(new InstrumentOpenStateEvent(isOpen, player, pos, hand));
    }
    
}
