package com.cstav.genshinstrument.networking.packet.instrument.c2s;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.INoteIdentifierSender;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.PlayNotePacket;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.util.ServerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.Optional;

/**
 * A C2S packet notifying the server that a
 * specific note should be played in the level
 */
public class InstrumentPacket implements INoteIdentifierSender {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;


    /** Optionally pass a position that defers from the player's */
    private final Optional<BlockPos> pos;
    private final NoteSound sound;

    private final int pitch, volume;

    private final ResourceLocation instrumentId;
    private final Optional<NoteButtonIdentifier> noteIdentifier;

    public InstrumentPacket(Optional<BlockPos> pos, NoteSound sound, int pitch, int volume,
            ResourceLocation instrumentId, Optional<NoteButtonIdentifier> noteIdentifier) {
        this.pos = pos;
        this.sound = sound;

        this.pitch = pitch;
        this.volume = volume;

        this.instrumentId = instrumentId;
        this.noteIdentifier = noteIdentifier;
    }
    @OnlyIn(Dist.CLIENT)
    public InstrumentPacket(NoteButton noteButton, NoteSound sound, int pitch) {
        this(Optional.empty(), sound, pitch,
            noteButton.instrumentScreen.volume,
            noteButton.instrumentScreen.getInstrumentId(),
            Optional.ofNullable(noteButton.getIdentifier())
        );
    }

    public InstrumentPacket(FriendlyByteBuf buf) {
        pos = buf.readOptional(FriendlyByteBuf::readBlockPos);
        sound = NoteSound.readFromNetwork(buf);

        pitch = buf.readInt();
        volume = buf.readInt();

        instrumentId = buf.readResourceLocation();
        noteIdentifier = buf.readOptional(this::readNoteIdentifierFromNetwork);
    }

    @Override
    public void write(final FriendlyByteBuf buf) {
        buf.writeOptional(pos, FriendlyByteBuf::writeBlockPos);
        sound.writeToNetwork(buf);

        buf.writeInt(pitch);
        buf.writeInt(volume);

        buf.writeResourceLocation(instrumentId);
        buf.writeOptional(noteIdentifier, (fbb, identifier) -> identifier.writeToNetwork(fbb));
    }



    @Override
    public void handle(final Context context) {
        final ServerPlayer player = context.getSender();
        sendPlayNotePackets(player);
    }

    protected void sendPlayNotePackets(final ServerPlayer player) {

        ServerUtil.sendPlayNotePackets(player, pos,
            sound, instrumentId, noteIdentifier.orElse(null),
            pitch, volume,
            PlayNotePacket::new
        );
        
    }
    
}