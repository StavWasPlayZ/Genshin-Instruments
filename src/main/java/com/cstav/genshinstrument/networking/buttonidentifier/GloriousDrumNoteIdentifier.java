package com.cstav.genshinstrument.networking.buttonidentifier;

import com.cstav.genshinstrument.client.gui.screen.instrument.gloriousdrum.GloriousDrumButtonType;
import com.cstav.genshinstrument.client.gui.screen.instrument.gloriousdrum.GloriousDrumNoteButton;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GloriousDrumNoteIdentifier extends NoteButtonIdentifier {

    public final GloriousDrumButtonType noteType;
    public final boolean isRight;

    @OnlyIn(Dist.CLIENT)
    public GloriousDrumNoteIdentifier(final GloriousDrumNoteButton note) {
        noteType = note.btnType;
        isRight = note.isRight;
    }

    public GloriousDrumNoteIdentifier(FriendlyByteBuf buf) {
        noteType = buf.readEnum(GloriousDrumButtonType.class);
        isRight = buf.readBoolean();
    }
    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        super.writeToNetwork(buf);
        buf.writeEnum(noteType);
        buf.writeBoolean(isRight);
    }

    @Override
    public boolean matches(NoteButtonIdentifier other) {
        return MatchType.forceMatch(other, this::drumMatch);
    }
    private boolean drumMatch(final GloriousDrumNoteIdentifier other) {
        return (noteType == other.noteType) && (isRight == other.isRight);
    }

    
}
