package com.cstav.genshinstrument.networking.packet.instrument.s2c;

import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.sound.held.HeldNoteSound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.Optional;
import java.util.UUID;

/**
 * A S2C packet notifying the client to play
 * a specific {@link HeldNoteSound}.
 */
public class S2CHeldNoteSoundAttackPacket extends S2CNotePacket<HeldNoteSound> {
    /**
     * Constructs a new {@link S2CHeldNoteSoundAttackPacket}.
     * @param initiatorUUID The UUID of the player initiating the sound.
     *                      May be empty for a non-player trigger.
     */
    public S2CHeldNoteSoundAttackPacket(Optional<UUID> initiatorUUID, HeldNoteSound sound, NoteSoundMetadata meta) {
        super(initiatorUUID, sound, meta);
    }
    public S2CHeldNoteSoundAttackPacket(FriendlyByteBuf buf) {
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

    @Override
    public void handle(final Context context) {
        sound.playFromServer(initiatorUUID, meta);
    }
}