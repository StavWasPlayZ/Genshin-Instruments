package com.cstav.genshinstrument.networking.packet.instrument.util;

import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.networking.GIPacketHandler;
import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.S2CNotePacket;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.S2CNoteSoundPacket;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.Optional;

/**
 * A helper class for dealing with {@link NoteSound} packets.
 */
public class NoteSoundPacketUtil {

    /**
     * Sends {@link S2CNoteSoundPacket}s in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
     * @param initiator The player producing the sounds
     * @param sound The sound to initiate
     * @param instrumentId The ID of the instrument initiating the sound
     * @param pitch The pitch of the sound to initiate
     * @param volume The volume of the sound to initiate
     */
    public static void sendPlayerPlayNotePackets(ServerPlayer initiator,
                                           NoteSound sound, ResourceLocation instrumentId, int pitch, int volume) {
        sendPlayerPlayNotePackets(
            initiator, sound, new NoteSoundMetadata(
                initiator.blockPosition(),
                pitch, volume,
                instrumentId,
                Optional.empty()
            ),
            S2CNoteSoundPacket::new
        );
    }
    /**
     * Sends {@link S2CNoteSoundPacket}s in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
     * @param initiator The player producing the sounds
     * @param sound The sound to initiate
     * @param soundMeta Additional metadata of the used sound
     * @param notePacketDelegate The initiator of the {@link S2CNoteSoundPacket} to be sent
     */
    public static void sendPlayerPlayNotePackets(ServerPlayer initiator, NoteSound sound, NoteSoundMetadata soundMeta,
                                           S2CNotePacketDelegate<NoteSound> notePacketDelegate) {

        final S2CNotePacket<NoteSound> packet = notePacketDelegate.create(
            Optional.of(initiator.getUUID()), sound, soundMeta
        );

        for (final Player listener : InstrumentPacketUtil.noteListeners(initiator.level(), soundMeta.pos()))
            GIPacketHandler.sendToClient(packet, (ServerPlayer)listener);


        // Trigger an instrument game event
        // This is done so that sculk sensors can pick up the instrument's sound
        initiator.level().gameEvent(
            GameEvent.INSTRUMENT_PLAY, soundMeta.pos(),
            GameEvent.Context.of(initiator)
        );

        MinecraftForge.EVENT_BUS.post(
            new InstrumentPlayedEvent.ByPlayer(initiator, sound, soundMeta)
        );
    }

    /**
     * Sends {@link S2CNoteSoundPacket}s in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
     * This method treats the sound as it was NOT produced by a player.
     * @param level The world that the sound should initiate in
     * @param pos The position of the sound to initiate
     * @param sound The sound to initiate
     * @param instrumentId The ID of the instrument initiating the sound
     * @param pitch The pitch of the sound to initiate
     */
    public static void sendPlayNotePackets(Level level, BlockPos pos, NoteSound sound, ResourceLocation instrumentId,
                                           int pitch, int volume) {
        sendPlayNotePackets(
            level, sound, new NoteSoundMetadata(
                pos,
                pitch, volume,
                instrumentId,
                Optional.empty()
            ),
            S2CNoteSoundPacket::new
        );
    }
    /**
     * Sends {@link S2CNoteSoundPacket}s in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
     * This method treats the sound as it was NOT produced by a player.
     * @param level The world that the sound should initiate in
     * @param sound The sound to initiate
     * @param soundMeta Additional metadata of the used sound
     * @param notePacketDelegate The initiator of the {@link S2CNoteSoundPacket} to be sent
     */
    public static void sendPlayNotePackets(Level level, NoteSound sound, NoteSoundMetadata soundMeta,
                                           S2CNotePacketDelegate<NoteSound> notePacketDelegate) {

        final S2CNotePacket<NoteSound> packet = notePacketDelegate.create(
            Optional.empty(), sound, soundMeta
        );

        for (final Player listener : InstrumentPacketUtil.noteListeners(level, soundMeta.pos()))
            GIPacketHandler.sendToClient(packet, (ServerPlayer)listener);


        final BlockState bs = level.getBlockState(soundMeta.pos());
        // The sound may have been coming from a block
        if (bs != Blocks.AIR.defaultBlockState())
            level.gameEvent(
                GameEvent.INSTRUMENT_PLAY, soundMeta.pos(),
                GameEvent.Context.of(bs)
            );
            // idk what else
        else
            level.gameEvent(null, GameEvent.INSTRUMENT_PLAY, soundMeta.pos());


        MinecraftForge.EVENT_BUS.post(
            new InstrumentPlayedEvent(level, sound, soundMeta)
        );
    }

}
