package com.cstav.genshinstrument.networking.packet.instrument.s2c;

import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.INoteIdentifierSender;
import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.Optional;
import java.util.UUID;

/**
 * A S2C packet notifying the client to play
 * a specific {@link NoteSound}.
 */
public class PlayNotePacket implements INoteIdentifierSender {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;

    private final NoteSound sound;
    private final NoteSoundMetadata meta;

    public PlayNotePacket(Optional<BlockPos> pos, NoteSound sound, int pitch, int volume, ResourceLocation instrumentId,
                          Optional<NoteButtonIdentifier> noteIdentifier, Optional<UUID> playerUUID) {
        this.sound = sound;
        meta = new NoteSoundMetadata(
            playerUUID,
            pos,

            pitch,
            volume,
            instrumentId,
            noteIdentifier
        );
    }
    public PlayNotePacket(FriendlyByteBuf buf) {
        sound = NoteSound.readFromNetwork(buf);
        meta = NoteSoundMetadata.read(buf, this);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        sound.writeToNetwork(buf);
        meta.write(buf);
    }


    @Override
    public void handle(final Context context) {
        sound.play(
            meta.pitch(), meta.volume(), meta.playerUUID(),
            meta.instrumentId(), meta.noteIdentifier(), meta.pos()
        );
    }
}