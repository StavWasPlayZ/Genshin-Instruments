package com.cstav.genshinstrument.sounds;

import javax.annotation.Nullable;

import com.cstav.genshinstrument.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.InstrumentChannelType;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NoteSound {
    /**
     * The range at which playuers with Mixed instrument sound type will start to hear Mono.
    */
    public static final int STEREO_RANGE = 3;
    

    public SoundEvent mono;
    @Nullable public SoundEvent stereo;
    public NoteSound(final SoundEvent mono, final @Nullable SoundEvent stereo) {
        this.mono = mono;
        this.stereo = stereo;
    }
    public NoteSound() {}
    

    public boolean hasStereo() {
        return stereo != null;
    }


    /**
     * @param distanceFromPlayer The distance between this player and the position of the note's sound
     * @return Either the Mono or Stereo sound, based on the client's preference.
     * This method assumes that the request was made by a server.
     */
    @OnlyIn(Dist.CLIENT)
    public SoundEvent getByPreference(final double distanceFromPlayer) {
        if (!hasStereo())
            return mono;
        
        final InstrumentChannelType preference = ModClientConfigs.CHANNEL_TYPE.get();

        return switch(preference) {
            case MIXED -> (distanceFromPlayer > STEREO_RANGE) ? mono : stereo;

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
