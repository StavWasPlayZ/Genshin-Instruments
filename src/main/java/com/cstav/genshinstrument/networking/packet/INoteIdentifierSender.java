package com.cstav.genshinstrument.networking.packet;

import com.cstav.genshinstrument.networking.GIPacketHandler;
import com.cstav.genshinstrument.networking.IModPacket;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifier;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

public interface INoteIdentifierSender extends IModPacket {
    
    default List<Class<? extends NoteButtonIdentifier>> acceptableIdentifiers() {
        return GIPacketHandler.ACCEPTABLE_IDENTIFIERS;
    }

    default NoteButtonIdentifier readNoteIdentifierFromNetwork(final FriendlyByteBuf buf) {
        return NoteButtonIdentifier.readFromNetwork(buf, acceptableIdentifiers());
    }

}
