package com.cstav.genshinstrument.networking.packet.instrument.s2c;

import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.networking.packet.instrument.util.HeldSoundPhase;
import com.cstav.genshinstrument.sound.held.HeldNoteSound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.Optional;

/**
 * A S2C packet notifying the client to play
 * a specific {@link HeldNoteSound}.
 */
public class S2CHeldNoteSoundPacket extends S2CNotePacket<HeldNoteSound> {

    protected final HeldSoundPhase phase;

    /**
     * Constructs a new {@link S2CHeldNoteSoundPacket}.
     * @param initiatorID The UUID of the player initiating the sound.
     *                      May be empty for a non-player trigger.
     */
    public S2CHeldNoteSoundPacket(Optional<Integer> initiatorID, HeldNoteSound sound, NoteSoundMetadata meta,
                                  HeldSoundPhase phase) {
        super(initiatorID, sound, meta);
        this.phase = phase;
    }

    public S2CHeldNoteSoundPacket(FriendlyByteBuf buf) {
        super(buf);
        this.phase = buf.readEnum(HeldSoundPhase.class);
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


    @Override
    public void handle(final Context context) {
        sound.playFromServer(initiatorID, meta, phase);
    }
}