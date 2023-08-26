package com.cstav.genshinstrument.networking.buttonidentifier;

import com.cstav.genshinstrument.client.gui.screen.instrument.drum.DrumButtonType;
import com.cstav.genshinstrument.client.gui.screen.instrument.drum.DrumNoteButton;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DrumNoteIdentifier extends DefaultNoteButtonIdentifier {

    public final DrumButtonType noteType;
    public final boolean isRight;

    @OnlyIn(Dist.CLIENT)
    public DrumNoteIdentifier(final DrumNoteButton note) {
        super(note, false);
        noteType = note.btnType;
        isRight = note.isRight;
    }

    public DrumNoteIdentifier(FriendlyByteBuf buf) {
        super(buf);
        noteType = buf.readEnum(DrumButtonType.class);
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
        return MatchType.perferMatch(other, this::drumMatch, super::matches);
    }
    private boolean drumMatch(final DrumNoteIdentifier other) {
        return (noteType == other.noteType) && (isRight == other.isRight);
    }

    
}
