package com.cstav.genshinstrument.sounds;

import java.util.UUID;

import javax.annotation.Nullable;

import com.cstav.genshinstrument.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.InstrumentChannelType;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NoteSound {
    /**
     * The range at which playuers with Mixed instrument sound type will start to hear Mono.
    */
    public static final double STEREO_RANGE = 5.5;
    /**
     * The range from which players will stop hearing Minecraft's background music on playing
     */
    public static final int STOP_SOUND_DISTANCE = 10;

    public static final float MIN_PITCH = .5f, MAX_PITCH = 1.9f;
    

    public SoundEvent mono;
    @Nullable public SoundEvent stereo;
    private float pitch;
    public NoteSound(final SoundEvent mono, final @Nullable SoundEvent stereo, final float pitch) {
        this.mono = mono;
        this.stereo = stereo;
        
        setPitch(pitch);
    }
    public NoteSound() {}
    

    public boolean hasStereo() {
        return stereo != null;
    }

    public float getPitch() {
        return pitch;
    }
    public void setPitch(float pitch) {
        this.pitch = Math.min(Math.max(pitch, MIN_PITCH), MAX_PITCH);
    }


    /**
     * @param distanceFromPlayer The distance between this player and the position of the note's sound
     * @return Either the Mono or Stereo sound, based on the client's preference.
     * This method assumes that the request was made by a server.
     */
    @SuppressWarnings("resource")
    @OnlyIn(Dist.CLIENT)
    public SoundEvent getByPreference(final double distanceFromPlayer) {
        if (!hasStereo())
            return mono;
        
        final InstrumentChannelType preference = ModClientConfigs.CHANNEL_TYPE.get();
        final float insrtumentVol = Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.RECORDS);

        return switch(preference) {
            case MIXED -> ((insrtumentVol < 1) || (distanceFromPlayer > STEREO_RANGE)) ? mono : stereo;

            case STEREO -> stereo;
            case MONO -> mono;
        };
    }
    /**
     * @return Either the Mono or Stereo sound, based on the client's preference
     * This method assumes that the request was made by a client.
     */
    @OnlyIn(Dist.CLIENT)
    public SoundEvent getByPreference() {
        if (!hasStereo())
            return mono;
        
        final InstrumentChannelType preference = ModClientConfigs.CHANNEL_TYPE.get();

        return switch(preference) {
            case MIXED, STEREO -> stereo;
            case MONO -> mono;
        };
    }

    /**
     * A method for packets to use for playing this note on the client's end.
     * If {@link Minecraft#player this player} is the same as the gives player,
     * the method will only stop the client's background music per preference.
     * @param playerUUID The UUID of the player who initiated the sound
     * @param pos The position at which the sound was fired from
     */
    public void playNoteAtPos(final UUID playerUUID, final BlockPos pos) {
        final Minecraft minecraft = Minecraft.getInstance();
        final Player player = minecraft.player;

        final double distanceFromPlayer = Math.sqrt(pos.distToCenterSqr((Position)player.position()));
        
        if (ModClientConfigs.STOP_MUSIC_ON_PLAY.get() && (distanceFromPlayer < STOP_SOUND_DISTANCE))
            minecraft.getMusicManager().stopPlaying();
        

        if (player.getUUID().equals(playerUUID))
            return;
            
        final Level level = minecraft.level;
        level.playLocalSound(pos,
            getByPreference(distanceFromPlayer), SoundSource.RECORDS,
            1, getPitch()
        , false);
    }


    public void writeToNetwork(final FriendlyByteBuf buf) {
        mono.writeToNetwork(buf);

        buf.writeBoolean(hasStereo());
        if (hasStereo())
            stereo.writeToNetwork(buf);

        buf.writeFloat(pitch);
    }
    public static NoteSound readFromNetwork(final FriendlyByteBuf buf) {
        return new NoteSound(
            SoundEvent.readFromNetwork(buf),
            buf.readBoolean() ? SoundEvent.readFromNetwork(buf) : null,
            buf.readFloat()
        );
    }

}
