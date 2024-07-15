package com.cstav.genshinstrument.networking.packet.instrument.c2s;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.networking.packet.instrument.s2c.S2CHeldNoteSoundAttackPacket;
import com.cstav.genshinstrument.networking.packet.instrument.util.HeldNoteSoundPacketUtil;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A C2S packet notifying the server that a
 * specific {@link NoteSound} should be played in the level
 */
public class C2SHeldNoteSoundAttackPacket extends C2SNotePacket<HeldNoteSound> {

    public C2SHeldNoteSoundAttackPacket(HeldNoteSound sound, NoteSoundMetadata meta) {
        super(sound, meta);
    }
    @OnlyIn(Dist.CLIENT)
    public C2SHeldNoteSoundAttackPacket(NoteButton noteButton, HeldNoteSound sound, int pitch) {
        super(noteButton, sound, pitch);
    }

    public C2SHeldNoteSoundAttackPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    protected void writeSound(FriendlyByteBuf buf) {
        sound.writeToNetwork(buf);
    }
    @Override
    protected HeldNoteSound readSound(FriendlyByteBuf buf) {
        return HeldNoteSound.readFromNetwork(buf);
    }

    protected void sendPlayNotePackets(final ServerPlayer player) {
        HeldNoteSoundPacketUtil.sendPlayerPlayNotePackets(player, sound, meta, S2CHeldNoteSoundAttackPacket::new);
    }
}