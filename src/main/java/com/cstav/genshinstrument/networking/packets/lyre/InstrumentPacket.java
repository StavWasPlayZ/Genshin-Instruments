package com.cstav.genshinstrument.networking.packets.lyre;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpen;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.networking.ModPacket;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.packets.StopMusicPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class InstrumentPacket implements ModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;
    /**
     * The range at which playuers with Mixed instrument sound type will start to hear Mono.
     */
    public static final int MIXED_RANGE = 5;

    private static final int STOP_SOUND_DISTANCE = 10, SOUND_DISTANCE = 16;
    public static final float MIN_PITCH = .5f, MAX_PITCH = 1.9f;


    private final SoundEvent sound;
    private final float pitch;
    public InstrumentPacket(final SoundEvent sound, final float pitch) {
        this.sound = sound;
        this.pitch = pitch;
    }
    public InstrumentPacket(FriendlyByteBuf buf) {
        sound = SoundEvent.readFromNetwork(buf);
        pitch = Math.min(Math.max(buf.readFloat(), MIN_PITCH), MAX_PITCH);
    }


    @Override
    public void toBytes(final FriendlyByteBuf buf) {
        buf.writeResourceLocation(sound.getLocation());
        buf.writeBoolean(true);
        buf.writeFloat(SOUND_DISTANCE);
        buf.writeFloat(pitch);
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

            // Play the sound to all nearby players
            level.playSound(player.self(), new BlockPos(player.position()),
                sound, SoundSource.RECORDS, 1f, pitch
            );

            // Stop all nearby players' background music
            final List<Player> listeners = level.getNearbyPlayers(
                TargetingConditions.forNonCombat().range(STOP_SOUND_DISTANCE),
                null, player.getBoundingBox().inflate(STOP_SOUND_DISTANCE)
            );
            for (final Player listener : listeners)
                ModPacketHandler.sendToClient(new StopMusicPacket(), (ServerPlayer)listener);
        });

        return true;
    }
    
}