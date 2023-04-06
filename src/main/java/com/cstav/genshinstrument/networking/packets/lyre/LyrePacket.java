package com.cstav.genshinstrument.networking.packets.lyre;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.cstav.genshinstrument.capability.lyreOpen.LyreOpen;
import com.cstav.genshinstrument.capability.lyreOpen.LyreOpenProvider;
import com.cstav.genshinstrument.networking.ModPacket;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.packets.StopMusicPacket;
import com.cstav.genshinstrument.sounds.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class LyrePacket implements ModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;
    private static final int STOP_SOUND_DISTANCE = 5;
    public static final float MIN_PITCH = .5f, MAX_PITCH = 1.9f;


    private final int note;
    private final float pitch;
    public LyrePacket(final int note, final float pitch) {
        this.note = note;
        this.pitch = pitch;
    }
    public LyrePacket(FriendlyByteBuf buf) {
        note = buf.readInt();
        pitch = Math.min(Math.max(buf.readFloat(), MIN_PITCH), MAX_PITCH);
    }


    @Override
    public void toBytes(final FriendlyByteBuf buf) {
        buf.writeInt(note);
        buf.writeFloat(pitch);
    }


    @Override
    public boolean handle(final Supplier<Context> supplier) {
        final Context context = supplier.get();
        
        context.enqueueWork(() -> {
            final ServerPlayer player = context.getSender();

            // The player could forcibly be trying to play a note.
            // Dunno how but ig it could happen, but we handle it here
            final Optional<LyreOpen> lyreOpen = player.getCapability(LyreOpenProvider.LYRE_OPEN).resolve();
            if (!lyreOpen.isPresent())
                return;
            if (!lyreOpen.get().isOpen())
                return;


            final Level level = player.getLevel();

            // Play the note to all nearby players
            level.playSound(player.self(), new BlockPos(player.position()),
                ModSounds.LYRE_NOTE_SOUNDS[note].get(), SoundSource.RECORDS, 3, pitch
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