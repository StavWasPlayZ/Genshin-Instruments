package com.cstav.genshinstrument.client.midi;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record PressedMIDINote(
    int notePitch,
    NoteButton pressedNote,
    NoteSound sound
) {}
