package com.cstav.genshinstrument.networking.buttonidentifier;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The default note button identifier. Uses a button's {@link NoteSound} as an identifier.
 */
public class DefaultNoteButtonIdentifier extends NoteButtonIdentifier {
    
    private final NoteSound sound;
    private final int pitch;
    private final boolean identifyByPitch;

    public DefaultNoteButtonIdentifier(final NoteSound sound, final int pitch, final boolean identifyByPitch) {
        this.sound = sound;
        this.pitch = pitch;
        this.identifyByPitch = identifyByPitch;
    }
    public DefaultNoteButtonIdentifier(final NoteSound sound, final int pitch) {
        this.sound = sound;
        this.pitch = pitch;
        this.identifyByPitch = true;
    }
    
    @OnlyIn(Dist.CLIENT)
    public DefaultNoteButtonIdentifier(final NoteButton note, final boolean identifyByPitch) {
        this(note.getSound(), note.getPitch(), identifyByPitch);
    }


    public DefaultNoteButtonIdentifier(final FriendlyByteBuf buf) {
        sound = NoteSound.readFromNetwork(buf);
        pitch = buf.readInt();
        identifyByPitch = buf.readBoolean();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        super.writeToNetwork(buf);

        sound.writeToNetwork(buf);
        buf.writeInt(pitch);
        buf.writeBoolean(identifyByPitch);
    }

    
    public boolean matches(NoteButtonIdentifier other) {
        return MatchType.forceMatch(other, this::matchSound);
    }
    private boolean matchSound(final DefaultNoteButtonIdentifier other) {
        return other.sound.equals(sound)
            && ((identifyByPitch && other.identifyByPitch) ? (pitch == other.pitch) : true);
    }
    
}
