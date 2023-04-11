package com.cstav.genshinstrument.networking.packets.instrument;

import java.util.UUID;
import java.util.function.Supplier;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.NoteButton;
import com.cstav.genshinstrument.networking.ModPacket;
import com.cstav.genshinstrument.sounds.NoteSound;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class PlayNotePacket implements ModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;


    private final BlockPos blockPos;
    private final NoteSound sound;
    private final UUID playerUUID;
    public PlayNotePacket(final BlockPos pos, final NoteSound sound, final UUID playerUUID) {
        this.blockPos = pos;
        this.sound = sound;
        this.playerUUID = playerUUID;
    }
    public PlayNotePacket(FriendlyByteBuf buf) {
        blockPos = buf.readBlockPos();
        sound = NoteSound.readFromNetwork(buf);
        playerUUID = buf.readUUID();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        sound.writeToNetwork(buf);
        buf.writeUUID(playerUUID);
    }


    @Override
    public boolean handle(final Supplier<Context> supplier) {
        supplier.get().enqueueWork(() ->
            NoteButton.playNoteAtPos(sound, playerUUID, blockPos)
        );

        return true;
    }
}