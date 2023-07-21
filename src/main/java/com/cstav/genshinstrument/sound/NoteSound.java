package com.cstav.genshinstrument.sound;

import java.util.Optional;
import java.util.UUID;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.config.enumType.InstrumentChannelType;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.util.LabelUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

/**
 * A class holding sound information for an instrument's note
 */
public class NoteSound {
    /**
     * The range at which playuers with Mixed instrument sound type will start to hear Mono.
    */
    public static final double STEREO_RANGE = 5.5;
    /**
     * The range from which players will stop hearing Minecraft's background music on playing
     */
    public static final double STOP_SOUND_DISTANCE = 10;
    /**
     * The range from which players will hear instruments from their local sound output rather than the level's
     */
    public static final double LOCAL_RANGE = STEREO_RANGE;

    public static final int
        MIN_PITCH = -LabelUtil.NOTES_PER_SCALE,
        MAX_PITCH = LabelUtil.NOTES_PER_SCALE
    ;



    SoundEvent mono;
    Optional<SoundEvent> stereo;
    
    public NoteSound(SoundEvent mono, Optional<SoundEvent> stereo) {
        this.mono = mono;
        this.stereo = stereo;
    }
    NoteSound() {}
    

    public SoundEvent getMono() {
        return mono;
    }

    public boolean hasStereo() {
        return stereo.isPresent();
    }
    public Optional<SoundEvent> getStereo() {
        return stereo;
    }


    /**
     * Determines which sound type should play based on this player's distance from the instrument player.
     * <p>This method is fired from the server.</p>
     * @param distanceFromPlayer The distance between this player and the position of the note's sound
     * @return Either the Mono or Stereo sound, based on the client's preference.
     */
    @OnlyIn(Dist.CLIENT)
    public SoundEvent getByPreference(final double distanceFromPlayer) {
        if (!hasStereo())
            return mono;
        
        final InstrumentChannelType preference = ModClientConfigs.CHANNEL_TYPE.get();

        return switch(preference) {
            case MIXED -> (metInstrumentVolume() && (distanceFromPlayer <= STEREO_RANGE)) ? stereo.get() : mono;

            case STEREO -> stereo.get();
            case MONO -> mono;
        };
    }
    /**
     * Returns the literal preference of the client. Defaults to Stereo.
     * <p>This method is fired from the client.</p>
     * @return Either the Mono or Stereo sound, based on the client's preference
     */
    @OnlyIn(Dist.CLIENT)
    public SoundEvent getByPreference() {
        if (!hasStereo())
            return mono;
        
        final InstrumentChannelType preference = ModClientConfigs.CHANNEL_TYPE.get();

        return switch (preference) {
            case MIXED -> metInstrumentVolume() ? stereo.get() : mono;

            case STEREO -> stereo.get();
            case MONO -> mono;
        };
    }

    /**
     * @return True if the instrument volume is set to 100%
     */
    @SuppressWarnings("resource")
    private static boolean metInstrumentVolume() {
        return Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.RECORDS) == 1;
    }


    /**
     * A method for packets to use for playing this note on the client's end.
     * Will also stop the client's background music per preference.
     * @param playerUUID The UUID of the player who initiated the sound. Null for when it wasn't a player.
     * @param hand The hand of the player who initiated the sound. Null for when it wasn't a player.
     * @param pos The position at which the sound was fired from
     */
    @OnlyIn(Dist.CLIENT)
    public void playAtPos(int pitch, UUID playerUUID, InteractionHand hand,
            ResourceLocation instrumentId, NoteButtonIdentifier buttonIdentifier, BlockPos pos) {
        final Minecraft minecraft = Minecraft.getInstance();
        final Player player = minecraft.player;

        final double distanceFromPlayer = Math.sqrt(pos.distToCenterSqr((Position)player.position()));
        
        if (ModClientConfigs.STOP_MUSIC_ON_PLAY.get() && (distanceFromPlayer < NoteSound.STOP_SOUND_DISTANCE))
            minecraft.getMusicManager().stopPlaying();

        final Level level = minecraft.level;

        
        MinecraftForge.EVENT_BUS.post((playerUUID == null)
            ? new InstrumentPlayedEvent(
                this, level, pos, instrumentId, buttonIdentifier, true
            )
            : new InstrumentPlayedEvent.ByPlayer(
                this, level.getPlayerByUUID(playerUUID), hand,
                instrumentId, buttonIdentifier, true
            )
        );
        

        if (player.getUUID().equals(playerUUID))
            return;

        
        final float mcPitch = getPitchByNoteOffset(clampPitch(pitch));
            
        if (distanceFromPlayer > LOCAL_RANGE)
            level.playLocalSound(pos,
                getByPreference(distanceFromPlayer), SoundSource.RECORDS,
                1, mcPitch
            , false);
        else
            playLocally(mcPitch);
    }

    /**
     * Plays this sound locally. Treats the given {@code pitch} as a Minecraft pitch.
     */
    @OnlyIn(Dist.CLIENT)
    public void playLocally(final float pitch) {
        Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(
            getByPreference().getLocation(), SoundSource.RECORDS,
            1, pitch, SoundInstance.createUnseededRandom(),
            false, 0, SoundInstance.Attenuation.NONE,
            0, 0, 0, true
        ));
    }
    /**
     * <p>Plays this note locally.</p>
     * Treats the given {@code pitch} as a note offset pitch,
     * thus performs a conversion from note offset pitch to Minecraft pitch.
     * @see NoteSound#getPitchByNoteOffset
     */
    @OnlyIn(Dist.CLIENT)
    public void playLocally(final int pitch) {
        playLocally(getPitchByNoteOffset(clampPitch(pitch)));
    }


    /**
     * Clams the given {@code pitch} between the set range
     */
    public static int clampPitch(final int pitch) {
        return (int)Mth.clamp(pitch, MIN_PITCH, MAX_PITCH);
    }
    /**
     * Converts the given note offset to Minecraft pitch.
     * @apiNote Formula taken from
     * <a href="https://github.com/Specy/genshin-music/blob/bb8229a279c7e5885ad3ab270b7afbe41f00d1c2/src/lib/Utilities.ts#L207C4-L207C4">
     * Specy's Genshin music app
     * </a>
     */
    public static float getPitchByNoteOffset(final int pitch) {
        return (float)Math.pow(2, (double)pitch/LabelUtil.NOTES_PER_SCALE);
    }



    public void writeToNetwork(final FriendlyByteBuf buf) {
        mono.writeToNetwork(buf);
        buf.writeOptional(stereo, (fbb, sound) -> sound.writeToNetwork(fbb));
    }
    public static NoteSound readFromNetwork(final FriendlyByteBuf buf) {
        return new NoteSound(
            SoundEvent.readFromNetwork(buf),
            buf.readOptional(SoundEvent::readFromNetwork)
        );
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NoteSound))
            return false;

        final NoteSound other = (NoteSound) obj;
        // Mono is enough to determine if the sounds are are the same
        return mono.getLocation().equals(other.mono.getLocation());
    }
}
