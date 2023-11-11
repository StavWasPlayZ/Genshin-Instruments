package com.cstav.genshinstrument.util;

import java.util.Optional;
import java.util.UUID;

import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.instrument.PlayNotePacket;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

@FunctionalInterface
public interface PlayNotePacketDelegate {

    PlayNotePacket create(Optional<BlockPos> pos, NoteSound sound, int pitch, int volume, ResourceLocation instrumentId,
        NoteButtonIdentifier noteIdentifier, Optional<UUID> playerUUID, Optional<InteractionHand> hand);
        
}
