package com.cstav.genshinstrument.sound;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.config.enumType.InstrumentChannelType;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.sound.registrar.NoteSoundRegistrar;
import com.cstav.genshinstrument.util.LabelUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.ApiStatus.Internal;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * A class holding sound information for an instrument's note
 */
public class NoteSound {
    public static final SoundSource INSTRUMENT_SOUND_SOURCE = SoundSource.RECORDS;

    /**
     * The range at which players with Mixed instrument sound type will start to hear Mono.
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



    public final int index;
    public final ResourceLocation baseSoundLocation;

    public SoundEvent mono;
    public SoundEvent stereo;

    /**
     * Constructor for assigning mono & stereo lazily
     * @apiNote Please use {@link NoteSoundRegistrar}!
     */
    @Internal
    public NoteSound(int index, ResourceLocation baseSoundLocation) {
        this.index = index;
        this.baseSoundLocation = baseSoundLocation;
    }
    

    public SoundEvent getMono() {
        return mono;
    }

    public boolean hasStereo() {
        return stereo != null;
    }
    @Nullable
    public SoundEvent getStereo() {
        return stereo;
    }

    public NoteSound[] getSoundsArr() {
        return NoteSoundRegistrar.getSounds(baseSoundLocation);
    }

    public NoteSoundReuslt transpose(final int amount) {
        final NoteSound[] sounds = getSoundsArr();
        int newIndex = amount + index;

        final int delta = newIndex / sounds.length;

        if (delta != 0) {
            // We can only go up/down 1 octave
            if ((delta < -1) || (delta > 1))
                return new NoteSoundReuslt(null, delta);

            newIndex += sounds.length * delta;
        }

        return new NoteSoundReuslt(sounds[newIndex], delta);
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
            case MIXED -> (metInstrumentVolume() && (distanceFromPlayer <= STEREO_RANGE)) ? getStereo() : mono;

            case STEREO -> getStereo();
            case MONO -> mono;
        };
    }
    /**
     * Returns the literal preference of the client. Defaults to Stereo.
     * <p>This method is usually fired from the client.</p>
     * <p>Shorthand for {@code getByPreference(0)}</p>
     * @return Either the Mono or Stereo sound, based on the client's preference
     */
    @OnlyIn(Dist.CLIENT)
    public SoundEvent getByPreference() {
        return getByPreference(0);
    }

    /**
     * @return True if the instrument volume is set to 100%
     */
    @SuppressWarnings("resource")
    private static boolean metInstrumentVolume() {
        return Minecraft.getInstance().options.getSoundSourceVolume(INSTRUMENT_SOUND_SOURCE) == 1;
    }


    /**
     * A method for packets to use for playing this note on the client's end.
     * Will also stop the client's background music per preference.
     * @param initiatorUUID The UUID of the player who initiated the sound. Empty for when it wasn't a player.
     * @param meta Additional metadata of the Note Sound being played
     */
    @OnlyIn(Dist.CLIENT)
    public void play(Optional<UUID> initiatorUUID, NoteSoundMetadata meta) {
        final Minecraft minecraft = Minecraft.getInstance();
        final Player player = minecraft.player;

        final Level level = minecraft.level;
        final Player initiator = initiatorUUID.map(level::getPlayerByUUID).orElse(null);

        final double distanceFromPlayer = meta.pos().getCenter().distanceTo(player.position());
        
        if (ModClientConfigs.STOP_MUSIC_ON_PLAY.get() && (distanceFromPlayer < NoteSound.STOP_SOUND_DISTANCE))
            minecraft.getMusicManager().stopPlaying();


        
        MinecraftForge.EVENT_BUS.post(initiator == null
            ? new InstrumentPlayedEvent(level, this, meta)
            : new InstrumentPlayedEvent.ByPlayer(initiator, this, meta)
        );
        

        if (player.equals(initiator))
            return;

        
        final float mcPitch = getPitchByNoteOffset(clampPitch(meta.pitch()));
            
        if (distanceFromPlayer > LOCAL_RANGE)
            level.playLocalSound(meta.pos(),
                getByPreference(distanceFromPlayer), INSTRUMENT_SOUND_SOURCE,
                1, mcPitch
            , false);
        else
            playLocally(mcPitch, meta.volume() / 100f);
    }

    /**
     * Plays this sound locally. Treats the given {@code pitch} as a Minecraft pitch.
     */
    @OnlyIn(Dist.CLIENT)
    public void playLocally(final float pitch, final float volume) {
        Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(
            getByPreference().getLocation(), INSTRUMENT_SOUND_SOURCE,
            volume, pitch, SoundInstance.createUnseededRandom(),
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
    public void playLocally(final int pitch, final float volume) {
        playLocally(getPitchByNoteOffset(clampPitch(pitch)), volume);
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
        return (float)Math.pow(2, (double)pitch / LabelUtil.NOTES_PER_SCALE);
    }



    public void writeToNetwork(final FriendlyByteBuf buf) {
        buf.writeResourceLocation(baseSoundLocation);
        buf.writeInt(index);
    }
    public static NoteSound readFromNetwork(final FriendlyByteBuf buf) {
        return NoteSoundRegistrar.getSounds(buf.readResourceLocation())[buf.readInt()];
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NoteSound other))
            return false;

        // Mono is enough to determine if the sounds are the same
        return baseSoundLocation.equals(other.baseSoundLocation) && (index == other.index);
    }
}
