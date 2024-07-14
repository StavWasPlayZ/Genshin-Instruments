package com.cstav.genshinstrument.sound.held;

import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.held.cached.HeldNoteSoundKey;
import com.cstav.genshinstrument.sound.registrar.HeldNoteSoundRegistrar;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

/**
 * A container for {@link NoteSound}s that act together
 * as a holdable note sound.
 * <p>The attack sound plays once at the beginning, and the hold
 * sound is held and repeated until released.</p>
 */
public record HeldNoteSound(
    ResourceLocation baseSoundLocation, int index,
    NoteSound attack, NoteSound hold, float holdDuration,
    float holdDelay,
    float chainedHoldDelay, float decay,
    float releaseFadeOut,
    float fullHoldFadeoutTime
) {

    public NoteSound getSound(final Phase phase) {
        return switch (phase) {
            case HOLD -> hold;
            case ATTACK -> attack;
        };
    }

    public static NoteSound[] getSounds(final HeldNoteSound[] sounds, final Phase phase) {
        return Arrays.stream(sounds)
            .map((sound) -> sound.getSound(phase))
            .toArray(NoteSound[]::new);
    }


    /**
     * A held note sound instance for 3rd party trigger
     */
    @OnlyIn(Dist.CLIENT)
    public void startPlaying(final int notePitch, final float volume, final Player initiator) {
        new HeldNoteSoundInstance(
            this, Phase.ATTACK,
            notePitch, volume,
            initiator, null
        ).queueAndAddInstance();
    }
    /**
     * A held note sound instance for local playing
     */
    @OnlyIn(Dist.CLIENT)
    public void startPlaying(final int notePitch, final float volume) {
        new HeldNoteSoundInstance(
            this, Phase.ATTACK,
            notePitch, volume
        ).queueAndAddInstance();
    }

    /**
     * A method for packets to use for playing this note on the client's end.
     * Will also stop the client's background music per preference.
     * @param initiatorUUID The UUID of the player who initiated the sound. Empty for when it wasn't a player.
     * @param meta Additional metadata of the Note Sound being played
     */
    public void playFromServer(Optional<UUID> initiatorUUID, NoteSoundMetadata meta) {
        initiatorUUID.ifPresentOrElse(
            // Sound was played by player
            (uuid) -> startPlaying(
                meta.pitch(), meta.volume(),
                Minecraft.getInstance().level.getPlayerByUUID(uuid)
            ),
            // Sound is by some other thing
            () -> startPlaying(meta.pitch(), meta.volume())
        );
    }


    public HeldNoteSoundKey getKey(final String initiatorId) {
        return new HeldNoteSoundKey(initiatorId, baseSoundLocation, index);
    }

    /**
     * @return The sound key by the player's UUID
     */
    public HeldNoteSoundKey getKey(Player initiator) {
        return new HeldNoteSoundKey(initiator.getStringUUID(), baseSoundLocation, index);
    }
    /**
     * @return The sound key by the position's toString method
     */
    public HeldNoteSoundKey getKey(BlockPos initiator) {
        return new HeldNoteSoundKey(initiator.toString(), baseSoundLocation, index);
    }


    public void writeToNetwork(final FriendlyByteBuf buf) {
        buf.writeResourceLocation(baseSoundLocation);
        buf.writeInt(index);
    }
    public static HeldNoteSound readFromNetwork(final FriendlyByteBuf buf) {
        return HeldNoteSoundRegistrar.getSounds(buf.readResourceLocation())[buf.readInt()];
    }


    public static enum Phase {
        ATTACK, HOLD
    }

}
