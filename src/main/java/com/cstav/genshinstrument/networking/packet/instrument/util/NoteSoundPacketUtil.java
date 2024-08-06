package com.cstav.genshinstrument.networking.packet.instrument.util;

import com.cstav.genshinstrument.event.NoteSoundPlayedEvent;
import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.S2CNoteSoundPacket;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

/**
 * A helper class for dealing with {@link NoteSound} packets.
 */
public class NoteSoundPacketUtil {

    /**
     * Sends {@link S2CNoteSoundPacket}s in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
     * @param initiator The entity producing the sounds
     * @param sound The sound to initiate
     * @param instrumentId The ID of the instrument initiating the sound
     * @param pitch The pitch of the sound to initiate
     * @param volume The volume of the sound to initiate
     */
    public static void sendPlayerPlayNotePackets(Entity initiator,
                                           NoteSound sound, ResourceLocation instrumentId, int pitch, int volume) {
        firePlayerEvent(initiator,
            InstrumentPacketUtil.sendPlayerPlayNotePackets(
                initiator, sound, instrumentId, pitch, volume,
                S2CNoteSoundPacket::new
            )
        );
    }
    /**
     * Sends {@link S2CNoteSoundPacket}s in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
     * @param initiator The entity producing the sounds
     * @param sound The sound to initiate
     * @param soundMeta Additional metadata of the used sound
     */
    public static void sendPlayerPlayNotePackets(Entity initiator, NoteSound sound, NoteSoundMetadata soundMeta) {
        firePlayerEvent(initiator,
            InstrumentPacketUtil.sendPlayerPlayNotePackets(
                initiator, sound, soundMeta, S2CNoteSoundPacket::new
            )
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
        fireGenericEvent(level,
            InstrumentPacketUtil.sendPlayNotePackets(
                level, pos, sound, instrumentId, pitch, volume,
                S2CNoteSoundPacket::new
            )
        );
    }
    /**
     * Sends {@link S2CNoteSoundPacket}s in the specified {@link InstrumentPacketUtil#PLAY_DISTANCE}.
     * This method treats the sound as it was NOT produced by a player.
     * @param level The world that the sound should initiate in
     * @param sound The sound to initiate
     * @param soundMeta Additional metadata of the used sound
     */
    public static void sendPlayNotePackets(Level level, NoteSound sound, NoteSoundMetadata soundMeta) {
        fireGenericEvent(level,
            InstrumentPacketUtil.sendPlayNotePackets(
                level, sound, soundMeta, S2CNoteSoundPacket::new
            )
        );
    }


    private static void firePlayerEvent(Entity initiator, S2CNoteSoundPacket packet) {
        MinecraftForge.EVENT_BUS.post(
            new NoteSoundPlayedEvent(initiator, packet.sound, packet.meta)
        );
    }
    private static void fireGenericEvent(Level level, S2CNoteSoundPacket packet) {
        MinecraftForge.EVENT_BUS.post(
            new NoteSoundPlayedEvent(level, packet.sound, packet.meta)
        );
    }

}
