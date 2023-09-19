package com.cstav.genshinstrument.networking.packet.instrument;

import java.util.Optional;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import com.cstav.genshinstrument.networking.packet.INoteIdentifierSender;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.util.ServerUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class InstrumentPacket implements INoteIdentifierSender {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;


    private final BlockPos pos;
    private final NoteSound sound;
    private final Optional<InteractionHand> hand;

    private final int pitch;
    private final float volume;

    private final ResourceLocation instrumentId;
    private final NoteButtonIdentifier noteIdentifier;

    public InstrumentPacket(BlockPos pos, NoteSound sound, int pitch, float volume, Optional<InteractionHand> hand,
            ResourceLocation instrumentId, NoteButtonIdentifier noteIdentifier) {
        this.pos = pos;
        this.sound = sound;
        this.hand = hand;

        this.pitch = pitch;
        this.volume = volume;

        this.instrumentId = instrumentId;
        this.noteIdentifier = noteIdentifier;
    }
    @OnlyIn(Dist.CLIENT)
    public InstrumentPacket(final NoteButton noteButton, final BlockPos pos) {
        this(pos, noteButton.getSound(),
            noteButton.getPitch(), noteButton.instrumentScreen.volume(),
            noteButton.instrumentScreen.interactionHand,
            noteButton.instrumentScreen.getInstrumentId(), noteButton.getIdentifier()
        );
    }

    public InstrumentPacket(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
        sound = NoteSound.readFromNetwork(buf);
        hand = buf.readOptional((fbb) -> buf.readEnum(InteractionHand.class));

        pitch = buf.readInt();
        volume = buf.readFloat();

        instrumentId = buf.readResourceLocation();
        noteIdentifier = readNoteIdentifierFromNetwork(buf);
    }

    @Override
    public void write(final FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        sound.writeToNetwork(buf);
        buf.writeOptional(hand, FriendlyByteBuf::writeEnum);

        buf.writeInt(pitch);
        buf.writeFloat(volume);

        buf.writeResourceLocation(instrumentId);
        noteIdentifier.writeToNetwork(buf);
    }



    @Override
    public void handle(final Context context) {
        final ServerPlayer player = context.getSender();
        if (!InstrumentOpen.isOpen(player))
            return;

        sendPlayNotePackets(player);
    }

    protected void sendPlayNotePackets(final ServerPlayer player) {

        ServerUtil.sendPlayNotePackets(player, pos, hand,
            sound, instrumentId, noteIdentifier,
            pitch, volume,
            PlayNotePacket::new
        );
        
    }
    
}