package com.cstav.genshinstrument.networking.packet.instrument.util;

import com.cstav.genshinstrument.event.HeldNoteSoundPlayedEvent;
import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.S2CHeldNoteSoundPacket;
import com.cstav.genshinstrument.sound.held.HeldNoteSound;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

/**
 * A helper class for dealing with {@link HeldNoteSound} packets.
 */
public class HeldNoteSoundPacketUtil {

    /**
     * Sends {@link S2CHeldNoteSoundPacket}s in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
     * @param initiator The player producing the sounds
     * @param sound The sound to initiate
     * @param instrumentId The ID of the instrument initiating the sound
     * @param pitch The pitch of the sound to initiate
     * @param volume The volume of the sound to initiate
     * @param phase The phase for the packet to report
     */
    public static void sendPlayerPlayNotePackets(ServerPlayer initiator, HeldNoteSound sound,
                                                 ResourceLocation instrumentId, int pitch, int volume,
                                                 HeldSoundPhase phase) {
        final NoteSoundMetadata meta = InstrumentPacketUtil.sendPlayerPlayNotePackets(
            initiator, sound, instrumentId, pitch, volume,
            S2CHeldNotePacketDelegate.toReg(S2CHeldNoteSoundPacket::new, phase)
        );

        MinecraftForge.EVENT_BUS.post(
            new HeldNoteSoundPlayedEvent(initiator, sound, meta, phase)
        );
    }
    /**
     * Sends {@link S2CHeldNoteSoundPacket}s in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
     * @param initiator The player producing the sounds
     * @param sound The sound to initiate
     * @param soundMeta Additional metadata of the used sound
     * @param notePacketDelegate The initiator of the {@link S2CHeldNoteSoundPacket} to be sent
     * @param phase The phase for the packet to report
     */
    public static void sendPlayerPlayNotePackets(ServerPlayer initiator, HeldNoteSound sound, NoteSoundMetadata soundMeta,
                                                 S2CHeldNotePacketDelegate notePacketDelegate,
                                                 HeldSoundPhase phase) {
        InstrumentPacketUtil.sendPlayerPlayNotePackets(initiator, sound, soundMeta, notePacketDelegate.toReg(phase));

        MinecraftForge.EVENT_BUS.post(
            new HeldNoteSoundPlayedEvent(initiator, sound, soundMeta, phase)
        );
    }

    /**
     * Sends {@link S2CHeldNoteSoundPacket}s in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
     * This method treats the sound as it was NOT produced by a player.
     * @param level The world that the sound should initiate in
     * @param pos The position of the sound to initiate
     * @param sound The sound to initiate
     * @param instrumentId The ID of the instrument initiating the sound
     * @param pitch The pitch of the sound to initiate
     * @param phase The phase for the packet to report
     */
    public static void sendPlayNotePackets(Level level, BlockPos pos, HeldNoteSound sound, ResourceLocation instrumentId,
                                           int pitch, int volume,
                                           HeldSoundPhase phase) {
        final NoteSoundMetadata meta = InstrumentPacketUtil.sendPlayNotePackets(
            level, pos, sound, instrumentId, pitch, volume,
            S2CHeldNotePacketDelegate.toReg(S2CHeldNoteSoundPacket::new, phase)
        );

        MinecraftForge.EVENT_BUS.post(
            new HeldNoteSoundPlayedEvent(level, sound, meta, phase)
        );
    }
    /**
     * Sends {@link S2CHeldNoteSoundPacket}s in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
     * This method treats the sound as it was NOT produced by a player.
     * @param level The world that the sound should initiate in
     * @param sound The sound to initiate
     * @param soundMeta Additional metadata of the used sound
     * @param notePacketDelegate The initiator of the {@link S2CHeldNoteSoundPacket} to be sent
     * @param phase The phase for the packet to report
     */
    public static void sendPlayNotePackets(Level level, HeldNoteSound sound, NoteSoundMetadata soundMeta,
                                           S2CHeldNotePacketDelegate notePacketDelegate,
                                           HeldSoundPhase phase) {
        InstrumentPacketUtil.sendPlayNotePackets(level, sound, soundMeta, notePacketDelegate.toReg(phase));

        MinecraftForge.EVENT_BUS.post(
            new HeldNoteSoundPlayedEvent(level, sound, soundMeta, phase)
        );
    }

}
