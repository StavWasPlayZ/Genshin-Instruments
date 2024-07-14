package com.cstav.genshinstrument.networking.packet.instrument.util;

import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.S2CNoteSoundPacket;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

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
        InstrumentPacketUtil.sendPlayerPlayNotePackets(
            initiator, sound, instrumentId, pitch, volume,
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
        InstrumentPacketUtil.sendPlayerPlayNotePackets(initiator, sound, soundMeta, notePacketDelegate);

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
        InstrumentPacketUtil.sendPlayNotePackets(
            level, pos, sound, instrumentId, pitch, volume,
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
        InstrumentPacketUtil.sendPlayNotePackets(level, sound, soundMeta, notePacketDelegate);

        MinecraftForge.EVENT_BUS.post(
            new InstrumentPlayedEvent(level, sound, soundMeta)
        );
    }

}
