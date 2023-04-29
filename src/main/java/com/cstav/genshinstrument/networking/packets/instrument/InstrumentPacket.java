package com.cstav.genshinstrument.networking.packets.instrument;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpen;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.networking.ModPacket;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.util.CommonUtil;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class InstrumentPacket implements ModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;
    
    public static final int PLAY_DISTANCE = 16;


    private final NoteSound sound;
    public InstrumentPacket(final NoteSound sound) {
        this.sound = sound;
    }
    public InstrumentPacket(FriendlyByteBuf buf) {
        sound = NoteSound.readFromNetwork(buf);
    }

    @Override
    public void toBytes(final FriendlyByteBuf buf) {
        sound.writeToNetwork(buf);
    }


    @Override
    public boolean handle(final Supplier<Context> supplier) {
        final Context context = supplier.get();
        
        context.enqueueWork(() -> {
            final ServerPlayer player = context.getSender();

            // The player could forcibly be trying to play a sound.
            // Dunno how but ig it could happen, but we handle it here
            final Optional<InstrumentOpen> lyreOpen = player.getCapability(InstrumentOpenProvider.INSTRUMENT_OPEN).resolve();
            if (!lyreOpen.isPresent())
                return;
            if (!lyreOpen.get().isOpen())
                return;

            final Level level = player.getLevel();

            // Send a play packet to everyone in the met distance
            final List<Player> listeners = CommonUtil.getPlayersInArea(level,
                player.getBoundingBox().inflate(PLAY_DISTANCE)
            );
            for (final Player listener : listeners)
                ModPacketHandler.sendToClient(
                    new PlayNotePacket(player.blockPosition(), sound, player.getUUID()), (ServerPlayer)listener
                );

            
            // Fire the Forge instrument event
            MinecraftForge.EVENT_BUS.post(new InstrumentPlayedEvent(player, sound));
            
            // Trigger an instrument game event
            // This is done so that sculk sensors can pick up the instrument's sound
            level.gameEvent(GameEvent.INSTRUMENT_PLAY, player.blockPosition(), GameEvent.Context.of(player));
        });

        return true;
    }
    
}