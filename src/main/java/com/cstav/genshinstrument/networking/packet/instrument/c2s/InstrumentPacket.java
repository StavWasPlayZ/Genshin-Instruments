package com.cstav.genshinstrument.networking.packet.instrument.c2s;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.INoteIdentifierSender;
import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.PlayNotePacket;
import com.cstav.genshinstrument.networking.packet.instrument.util.NoteSoundPacketUtil;
import com.cstav.genshinstrument.sound.NoteSound;
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

    private final NoteSound sound;
    private final NoteSoundMetadata meta;

    public InstrumentPacket(Optional<BlockPos> pos, NoteSound sound, int pitch, int volume,
            ResourceLocation instrumentId, Optional<NoteButtonIdentifier> noteIdentifier) {
        this.sound = sound;

        meta = new NoteSoundMetadata(
            Optional.empty(),
            pos,

            pitch,
            volume,
            instrumentId,
            noteIdentifier
        );
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
        sound = NoteSound.readFromNetwork(buf);
        meta = NoteSoundMetadata.read(buf, this);
    }

    @Override
    public void write(final FriendlyByteBuf buf) {
        sound.writeToNetwork(buf);
        meta.write(buf);
    }



    @Override
    public void handle(final Context context) {
        final ServerPlayer player = context.getSender();
        sendPlayNotePackets(player);
    }

    protected void sendPlayNotePackets(final ServerPlayer player) {

        NoteSoundPacketUtil.sendPlayNotePackets(player, meta.pos(),
            sound, meta.instrumentId(), meta.noteIdentifier().orElse(null),
            meta.pitch(), meta.volume(),
            PlayNotePacket::new
        );
        
    }
    
}