package com.cstav.genshinstrument.client.gui.screen.instrument.vintagelyre;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid.GridInstrumentMidiReceiver;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid.GridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.InstrumentOptionsScreen;
import com.cstav.genshinstrument.client.midi.InstrumentMidiReceiver;
import com.cstav.genshinstrument.client.midi.MidiOverflowResult;
import com.cstav.genshinstrument.client.midi.MidiOverflowResult.OverflowType;
import com.cstav.genshinstrument.client.midi.PressedMIDINote;
import com.cstav.genshinstrument.sound.GISounds;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class VintageLyreScreen extends GridInstrumentScreen {
    public static final ResourceLocation INSTRUMENT_ID = GInstrumentMod.loc("vintage_lyre");
    public static final String[] NOTE_LAYOUT = {
        "C", "Db", "Eb", "F", "G", "Ab", "Bb",
        "C", "D", "Eb", "F", "G", "A", "Bb",
        "C", "D", "Eb", "F", "G", "A", "Bb"
    };

    @Override
    public ResourceLocation getInstrumentId() {
        return INSTRUMENT_ID;
    }


    @Override
    public NoteSound[] getInitSounds() {
        return GISounds.VINTAGE_LYRE_NOTE_SOUNDS;
    }

    @Override
    public String[] noteLayout() {
        return shouldSoundNormalize()
            ? GridInstrumentScreen.NOTE_LAYOUT
            : NOTE_LAYOUT;
    }

    @Override
    protected InstrumentOptionsScreen initInstrumentOptionsScreen() {
        return new VintageLyreOptionsScreen(this);
    }

    public boolean shouldSoundNormalize() {
        return ModClientConfigs.NORMALIZE_VINTAGE_LYRE.get()
            // To normalize a flattened note, one must go up a note.
            // ...But we're maxed.
            && (getPitch() != NoteSound.MAX_PITCH);
    }


    @Override
    public VintageNoteButton createNote(int row, int column) {
        return new VintageNoteButton(row, column, this);
    }


    // Fix MIDI overflow for Vintage Lyre (pitch being offset by 1).
    // IMPORTANT: The last note of the lyre is KNOWN to be
    // flattened. The below fix is based off that assumption.
    @Override
    public InstrumentMidiReceiver initMidiReceiver() {
        return new GridInstrumentMidiReceiver(this) {
            @Override
            protected PressedMIDINote playNote(NoteButton noteBtn, @Nullable MidiOverflowResult midiOverflow, int basePitch) {
                if ((midiOverflow == null) || (midiOverflow.type() != OverflowType.TOP))
                    return super.playNote(noteBtn, midiOverflow, basePitch);

                final int offsetFix = shouldSoundNormalize()
                    ? 1
                    : (((VintageNoteButton)(noteBtn)).isDefaultFlat() ? 0 : 1);

                // Account for Minecraft pitch overflow
                final int newPitch = basePitch + offsetFix;
                if (newPitch + midiOverflow.pitchOffset() > NoteSound.MAX_PITCH)
                    return null;

                return super.playNote(noteBtn, midiOverflow, newPitch);
            }
        };
    }


    public static final InstrumentThemeLoader THEME_LOADER = new InstrumentThemeLoader(INSTRUMENT_ID);
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
}
