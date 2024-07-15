package com.cstav.genshinstrument.networking.packet.instrument.c2s;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.S2CNoteSoundPacket;
import com.cstav.genshinstrument.networking.packet.instrument.util.NoteSoundPacketUtil;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A C2S packet notifying the server that a
 * specific {@link NoteSound} should be played in the level
 */
public class C2SNoteSoundPacket extends C2SNotePacket<NoteSound> {

    public C2SNoteSoundPacket(NoteSound sound, NoteSoundMetadata meta) {
        super(sound, meta);
    }
    @OnlyIn(Dist.CLIENT)
    public C2SNoteSoundPacket(NoteButton noteButton, NoteSound sound, int pitch) {
        super(noteButton, sound, pitch);
    }

    public C2SNoteSoundPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    protected void writeSound(FriendlyByteBuf buf) {
        sound.writeToNetwork(buf);
    }
    @Override
    protected NoteSound readSound(FriendlyByteBuf buf) {
        return NoteSound.readFromNetwork(buf);
    }

    protected void sendPlayNotePackets(final ServerPlayer player) {
        NoteSoundPacketUtil.sendPlayerPlayNotePackets(player, sound, meta, S2CNoteSoundPacket::new);
    }
}