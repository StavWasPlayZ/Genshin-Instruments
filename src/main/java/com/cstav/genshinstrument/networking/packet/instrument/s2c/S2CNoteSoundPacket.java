package com.cstav.genshinstrument.networking.packet.instrument.s2c;

import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.Optional;
import java.util.UUID;

/**
 * A S2C packet notifying the client to play
 * a specific {@link NoteSound}.
 */
public class S2CNoteSoundPacket extends S2CNotePacket<NoteSound> {
    /**
     * Constructs a new {@link S2CNoteSoundPacket}.
     * @param initiatorUUID The UUID of the player initiating the sound.
     *                      May be empty for a non-player trigger.
     */
    public S2CNoteSoundPacket(Optional<UUID> initiatorUUID, NoteSound sound, NoteSoundMetadata meta) {
        super(initiatorUUID, sound, meta);
    }
    public S2CNoteSoundPacket(FriendlyByteBuf buf) {
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

    @Override
    public void handle(final Context context) {
        sound.play(initiatorUUID, meta);
    }
}