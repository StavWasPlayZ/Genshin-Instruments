package com.cstav.genshinstrument.networking.packet.instrument.c2s;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.event.HeldNoteSoundPlayedEvent;
import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.networking.packet.instrument.util.HeldNoteSoundPacketUtil;
import com.cstav.genshinstrument.networking.packet.instrument.util.HeldSoundPhase;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSounds;
import com.cstav.genshinstrument.sound.held.InitiatorID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A C2S packet notifying the server that a
 * specific {@link NoteSound} should be played in the level.
 *
 * @implNote <p>(Note for self if I ever think to myself "why tf did i do this")</p>
 * While it is possible to implement a secondary
 * release packet that specializes in providing parameters to
 * the {@link HeldNoteSounds} map in potentially less packets - sending each individual
 * packet for a sound for the {@link HeldNoteSounds#release(InitiatorID, HeldNoteSound, int) most specific function}
 * is imperative for notifying the {@link HeldNoteSoundPlayedEvent}.
 * Only the client knows which sounds are playing at the moment - not the server.
 */
public class C2SHeldNoteSoundPacket extends C2SNotePacket<HeldNoteSound> {

    public final HeldSoundPhase phase;

    public C2SHeldNoteSoundPacket(HeldNoteSound sound, NoteSoundMetadata meta, HeldSoundPhase phase) {
        super(sound, meta);
        this.phase = phase;
    }
    @OnlyIn(Dist.CLIENT)
    public C2SHeldNoteSoundPacket(NoteButton noteButton, HeldNoteSound sound, int pitch, HeldSoundPhase phase) {
        super(noteButton, sound, pitch);
        this.phase = phase;
    }

    public C2SHeldNoteSoundPacket(FriendlyByteBuf buf) {
        super(buf);
        phase = buf.readEnum(HeldSoundPhase.class);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        super.write(buf);
        buf.writeEnum(phase);
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
        HeldNoteSoundPacketUtil.sendPlayerPlayNotePackets(player, sound, meta, phase);
    }
}