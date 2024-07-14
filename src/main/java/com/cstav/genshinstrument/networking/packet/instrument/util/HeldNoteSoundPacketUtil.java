package com.cstav.genshinstrument.networking.packet.instrument.util;

import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.S2CHeldNoteSoundAttackPacket;
import com.cstav.genshinstrument.sound.held.HeldNoteSound;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * A helper class for dealing with {@link HeldNoteSound} packets.
 */
public class HeldNoteSoundPacketUtil {

    /**
     * Sends {@link S2CHeldNoteSoundAttackPacket}s in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
     * @param initiator The player producing the sounds
     * @param sound The sound to initiate
     * @param instrumentId The ID of the instrument initiating the sound
     * @param pitch The pitch of the sound to initiate
     * @param volume The volume of the sound to initiate
     */
    public static void sendPlayerPlayNotePackets(ServerPlayer initiator, HeldNoteSound sound,
                                                 ResourceLocation instrumentId, int pitch, int volume) {
        sendPlayerPlayNotePackets(
            initiator, sound, new NoteSoundMetadata(
                initiator.blockPosition(),
                pitch, volume,
                instrumentId,
                Optional.empty()
            ),
            S2CHeldNoteSoundAttackPacket::new
        );
    }
    /**
     * Sends {@link S2CHeldNoteSoundAttackPacket}s in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
     * @param initiator The player producing the sounds
     * @param sound The sound to initiate
     * @param soundMeta Additional metadata of the used sound
     * @param notePacketDelegate The initiator of the {@link S2CHeldNoteSoundAttackPacket} to be sent
     */
    public static void sendPlayerPlayNotePackets(ServerPlayer initiator, HeldNoteSound sound, NoteSoundMetadata soundMeta,
                                                 S2CNotePacketDelegate<HeldNoteSound> notePacketDelegate) {
        InstrumentPacketUtil.sendPlayerPlayNotePackets(initiator, sound, soundMeta, notePacketDelegate);

//        MinecraftForge.EVENT_BUS.post(
//            new InstrumentPlayedEvent.ByPlayer(initiator, sound, soundMeta)
//        );
    }

    /**
     * Sends {@link S2CHeldNoteSoundAttackPacket}s in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
     * This method treats the sound as it was NOT produced by a player.
     * @param level The world that the sound should initiate in
     * @param pos The position of the sound to initiate
     * @param sound The sound to initiate
     * @param instrumentId The ID of the instrument initiating the sound
     * @param pitch The pitch of the sound to initiate
     */
    public static void sendPlayNotePackets(Level level, BlockPos pos, HeldNoteSound sound, ResourceLocation instrumentId,
                                           int pitch, int volume) {
        InstrumentPacketUtil.sendPlayNotePackets(
            level, pos, sound, instrumentId, pitch, volume,
            S2CHeldNoteSoundAttackPacket::new
        );
    }
    /**
     * Sends {@link S2CHeldNoteSoundAttackPacket}s in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
     * This method treats the sound as it was NOT produced by a player.
     * @param level The world that the sound should initiate in
     * @param sound The sound to initiate
     * @param soundMeta Additional metadata of the used sound
     * @param notePacketDelegate The initiator of the {@link S2CHeldNoteSoundAttackPacket} to be sent
     */
    public static void sendPlayNotePackets(Level level, HeldNoteSound sound, NoteSoundMetadata soundMeta,
                                           S2CNotePacketDelegate<HeldNoteSound> notePacketDelegate) {
        InstrumentPacketUtil.sendPlayNotePackets(level, sound, soundMeta, notePacketDelegate);

//        MinecraftForge.EVENT_BUS.post(
//            new InstrumentPlayedEvent(level, sound, soundMeta)
//        );
    }

}
