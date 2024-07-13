package com.cstav.genshinstrument.util;

import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.PlayNotePacket;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.UUID;

@FunctionalInterface
public interface PlayNotePacketDelegate {

    PlayNotePacket create(Optional<BlockPos> pos, NoteSound sound, int pitch, int volume, ResourceLocation instrumentId,
        Optional<NoteButtonIdentifier> noteIdentifier, Optional<UUID> playerUUID);
        
}
