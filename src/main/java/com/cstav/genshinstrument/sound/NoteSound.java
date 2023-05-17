package com.cstav.genshinstrument.sound;

import javax.annotation.Nullable;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.config.enumType.InstrumentChannelType;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    public static final int STOP_SOUND_DISTANCE = 10;

    public static final float MIN_PITCH = .5f, MAX_PITCH = 1.9f;



    public SoundEvent mono;
    @Nullable public SoundEvent stereo;
    
    public NoteSound(SoundEvent mono, @Nullable SoundEvent stereo) {
        this.mono = mono;
        this.stereo = stereo;
    }
    public NoteSound() {}
    

    public boolean hasStereo() {
        return stereo != null;
    }


    /**
     * Determines which sound type should play based on this player's distance from the instrument player.
     * <p>This method is used by the server.</p>
     * @param distanceFromPlayer The distance between this player and the position of the note's sound
     * @return Either the Mono or Stereo sound, based on the client's preference.
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
     * Returns the literal preference of the client. Defaults to Stereo.
     * <p>This method is used by the client.</p>
     * @return Either the Mono or Stereo sound, based on the client's preference
     */
    @OnlyIn(Dist.CLIENT)
    public SoundEvent getByPreference() {
        if (!hasStereo())
            return mono;
        
        final InstrumentChannelType preference = ModClientConfigs.CHANNEL_TYPE.get();

        return switch (preference) {
            case MIXED, STEREO -> stereo;
            case MONO -> mono;
        };
    }


    public static float clampPitch(final float pitch) {
        return Mth.clamp(pitch, MIN_PITCH, MAX_PITCH);
    }


    public void writeToNetwork(final FriendlyByteBuf buf) {
        mono.writeToNetwork(buf);

        buf.writeBoolean(hasStereo());
        if (hasStereo())
            stereo.writeToNetwork(buf);
    }
    public static NoteSound readFromNetwork(final FriendlyByteBuf buf) {
        return new NoteSound(
            SoundEvent.readFromNetwork(buf),
            buf.readBoolean() ? SoundEvent.readFromNetwork(buf) : null
        );
    }

}
