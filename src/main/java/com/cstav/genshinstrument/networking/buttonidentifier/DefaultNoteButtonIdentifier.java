package com.cstav.genshinstrument.networking.buttonidentifier;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The default note button identifier. Uses a button's {@link NoteSound} as an identifier.
 */
public class DefaultNoteButtonIdentifier extends NoteButtonIdentifier {

    private NoteSound sound;

    public DefaultNoteButtonIdentifier(final NoteSound sound) {
        this.sound = sound;
    }
    @OnlyIn(Dist.CLIENT)
    public DefaultNoteButtonIdentifier(final NoteButton note) {
        this(note.getSound());
    }

    public void setSound(NoteSound sound) {
        this.sound = sound;
    }


    public boolean matches(NoteButtonIdentifier other) {
        return MatchType.forceMatch(other, this::matchSound);
    }
    private boolean matchSound(final DefaultNoteButtonIdentifier other) {
        return other.sound.equals(sound);
    }
    
}
