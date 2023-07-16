package com.cstav.genshinstrument.networking.buttonidentifiers;

import com.cstav.genshinstrument.client.gui.screens.instrument.drum.DrumButtonType;
import com.cstav.genshinstrument.client.gui.screens.instrument.drum.DrumNoteButton;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DrumNoteIdentifier extends NoteButtonIdentifier {

    private DrumButtonType noteType;

    @OnlyIn(Dist.CLIENT)
    public DrumNoteIdentifier(final DrumNoteButton note) {
        super(note);
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
