package com.cstav.genshinstrument.client.gui.screens.instrument.drum;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButtonIdentifier;

import net.minecraft.network.FriendlyByteBuf;

public class DrumNoteIdentifier extends NoteButtonIdentifier {

    private DrumButtonType noteType;
    public DrumNoteIdentifier(final DrumNoteButton note) {
        super(note.sound);
        noteType = note.btnType;
    }
    public DrumNoteIdentifier(FriendlyByteBuf buf) {
        super(buf);
        noteType = buf.readEnum(DrumButtonType.class);
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        super.writeToNetwork(buf);
        buf.writeEnum(noteType);
    }

    @Override
    public boolean matches(NoteButtonIdentifier other) {
        return (other instanceof DrumNoteIdentifier)
            && (noteType == ((DrumNoteIdentifier)other).noteType);
    }
    
}
