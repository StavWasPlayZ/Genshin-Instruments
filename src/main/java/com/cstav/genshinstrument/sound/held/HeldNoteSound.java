package com.cstav.genshinstrument.sound.held;

import com.cstav.genshinstrument.event.HeldNoteSoundPlayedEvent;
import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.networking.packet.instrument.util.HeldSoundPhase;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.registrar.HeldNoteSoundRegistrar;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

/**
 * A container for {@link NoteSound}s that act together
 * as a holdable note sound.
 * <p>The attack sound plays once at the beginning, and the hold
 * sound is held and repeated until released.</p>
 */
public record HeldNoteSound(
    ResourceLocation baseSoundLocation, int index,
    NoteSound attack,
    NoteSound hold,
    @Nullable NoteSound release,

    float holdDuration,
    float holdDelay,
    float chainedHoldDelay,
    float decay,
    float releaseFadeOut,
    float fullHoldFadeoutTime
) {

    public NoteSound getSound(final Phase phase) {
        return switch (phase) {
            case HOLD -> hold;
            case ATTACK -> attack;
            case RELEASE -> release;
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
    public void startPlaying(int notePitch, float volume, Entity initiator, BlockPos pos,
                             InitiatorID initiatorId, ResourceLocation instrumentId) {
        new HeldNoteSoundInstance(
            this, Phase.ATTACK,
            notePitch, volume,
            initiator, pos,
            initiatorId, instrumentId
        ).queueAndAddInstance();
    }
    /**
     * A held note sound instance for 3rd party trigger
     */
    @OnlyIn(Dist.CLIENT)
    public void startPlaying(int notePitch, float volume, Entity initiator,
                             InitiatorID initiatorId, ResourceLocation instrumentId) {
        startPlaying(notePitch, volume, initiator, null, initiatorId, instrumentId);
    }
    /**
     * A held note sound instance for 3rd party trigger
     */
    @OnlyIn(Dist.CLIENT)
    public void startPlaying(int notePitch, float volume, Entity initiator, ResourceLocation instrumentId) {
        startPlaying(notePitch, volume, initiator, InitiatorID.fromEntity(initiator), instrumentId);
    }
    /**
     * A held note sound instance for 3rd party trigger
     */
    @OnlyIn(Dist.CLIENT)
    public void startPlaying(int notePitch, float volume, BlockPos pos,
                             InitiatorID initiatorId, ResourceLocation instrumentId) {
        startPlaying(notePitch, volume, null, pos, initiatorId, instrumentId);
    }
    /**
     * A held note sound instance for local playing
     */
    @OnlyIn(Dist.CLIENT)
    public void startPlaying(int notePitch, float volume, ResourceLocation instrumentId) {
        startPlaying(notePitch, volume, Minecraft.getInstance().player, instrumentId);
    }


    //#region Play From Server

    /**
     * A method for packets to use for playing this note on the client's end.
     * Will also stop the client's background music per preference.
     * @param initiatorId The ID of the entity who initiated the sound. Empty for when it wasn't an entity.
     * @param oInitiatorId The initiator ID of the non-player initiator.
     * @param meta Additional metadata of the Note Sound being played
     */
    @OnlyIn(Dist.CLIENT)
    public void playFromServer(Optional<Integer> initiatorId, Optional<InitiatorID> oInitiatorId,
                               NoteSoundMetadata meta, HeldSoundPhase phase) {
        final Player localPlayer = Minecraft.getInstance().player;
        final Level level = localPlayer.getLevel();

        final InitiatorID _initiatorID = InitiatorID.getEither(initiatorId, oInitiatorId);

        if (initiatorId.isPresent()) {
            final Entity initiator = level.getEntity(initiatorId.get());

            MinecraftForge.EVENT_BUS.post(
                new HeldNoteSoundPlayedEvent(initiator, this, meta, phase, _initiatorID)
            );

            // Don't play sound for ourselves
            if (localPlayer.equals(initiator))
                return;
        }

        MinecraftForge.EVENT_BUS.post(
            new HeldNoteSoundPlayedEvent(level, this, meta, phase, _initiatorID)
        );


        switch (phase) {
            case ATTACK -> attackFromServer(_initiatorID, meta);
            case RELEASE -> releaseFromServer(_initiatorID, meta);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void attackFromServer(InitiatorID initiatorID, NoteSoundMetadata meta) {
        if (initiatorID.type().equals("entity")) {
            // Play as an entity
            startPlaying(
                meta.pitch(), meta.volume() / 100f,
                Minecraft.getInstance().level.getEntity(
                    Integer.parseInt(initiatorID.identifier())
                ),
                meta.instrumentId()
            );
        } else {
            // Play as other
            startPlaying(
                meta.pitch(), meta.volume() / 100f,
                meta.pos(),
                initiatorID, meta.instrumentId()
            );
        }
    }
    @OnlyIn(Dist.CLIENT)
    private void releaseFromServer(InitiatorID initiatorID, NoteSoundMetadata meta) {
        HeldNoteSounds.release(initiatorID, this, meta.pitch());
    }

    //#endregion

    public void writeToNetwork(final FriendlyByteBuf buf) {
        buf.writeResourceLocation(baseSoundLocation);
        buf.writeInt(index);
    }
    public static HeldNoteSound readFromNetwork(final FriendlyByteBuf buf) {
        return HeldNoteSoundRegistrar.getSounds(buf.readResourceLocation())[buf.readInt()];
    }


    public enum Phase {
        ATTACK, HOLD, RELEASE
    }

}
