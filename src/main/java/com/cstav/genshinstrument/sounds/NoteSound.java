package com.cstav.genshinstrument.sounds;

import javax.annotation.Nullable;

import com.cstav.genshinstrument.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.InstrumentChannelType;
import com.cstav.genshinstrument.networking.packets.instrument.InstrumentPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
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
    

    /**
     * Used in {@link InstrumentPacket} to determine which instrument should trigger a criteria
     */
    public ItemLike instrument;

    public SoundEvent mono;
    @Nullable public SoundEvent stereo;
    private float pitch;
    public NoteSound(SoundEvent mono, @Nullable SoundEvent stereo, float pitch, ItemLike instrument) {
        this.instrument = instrument;

        this.mono = mono;
        this.stereo = stereo;
        
        setPitch(pitch);
    }
    public NoteSound() {}
    

    public boolean hasStereo() {
        return stereo != null;
    }
    public ItemLike getInstrument() {
        return instrument;
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
     * @apiNote This method assumes that the request was sent by a server.
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


    public void writeToNetwork(final FriendlyByteBuf buf) {
        mono.writeToNetwork(buf);

        buf.writeBoolean(hasStereo());
        if (hasStereo())
            stereo.writeToNetwork(buf);

        buf.writeFloat(pitch);


        buf.writeItem(new ItemStack(instrument.asItem()));
    }
    public static NoteSound readFromNetwork(final FriendlyByteBuf buf) {
        return new NoteSound(
            SoundEvent.readFromNetwork(buf),
            buf.readBoolean() ? SoundEvent.readFromNetwork(buf) : null,
            buf.readFloat(),
            buf.readItem().getItem()
        );
    }

}
