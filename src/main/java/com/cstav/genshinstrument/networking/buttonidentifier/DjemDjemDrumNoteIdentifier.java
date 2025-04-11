package com.cstav.genshinstrument.networking.buttonidentifier;

import com.cstav.genshinstrument.client.gui.screen.instrument.djemdjemdrum.DjemDjemDrumNoteButton;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DjemDjemDrumNoteIdentifier extends NoteButtonIdentifier {

    public final int index;

    @OnlyIn(Dist.CLIENT)
    public DjemDjemDrumNoteIdentifier(final DjemDjemDrumNoteButton note) {
        index = note.index;
    }

    public DjemDjemDrumNoteIdentifier(FriendlyByteBuf buf) {
        index = buf.readInt();
    }
    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        super.writeToNetwork(buf);
        buf.writeInt(index);
    }

    @Override
    public boolean matches(NoteButtonIdentifier other) {
        return MatchType.forceMatch(other, this::drumMatch);
    }
    private boolean drumMatch(final DjemDjemDrumNoteIdentifier other) {
        return index == other.index;
    }

    
}
