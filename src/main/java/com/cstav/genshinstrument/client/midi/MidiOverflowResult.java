package com.cstav.genshinstrument.client.midi;

import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record MidiOverflowResult(NoteSound newNoteSound, int pitchOffset, int fixedOctaveNote) {}
