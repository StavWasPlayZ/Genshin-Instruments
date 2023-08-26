package com.cstav.genshinstrument.client.gui.screen.options.instrument;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.config.enumType.label.DrumNoteLabel;
import com.cstav.genshinstrument.client.gui.screen.instrument.drum.AratakisGreatAndGloriousDrumScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.label.INoteLabel;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.midi.DrumMidiOptionsScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.BaseInstrumentOptionsScreen;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DrumOptionsScren extends BaseInstrumentOptionsScreen {

    public DrumOptionsScren(AratakisGreatAndGloriousDrumScreen screen) {
        super(screen);
    }
    

    @Override
    protected void saveLabel(INoteLabel newLabel) {
        if (newLabel instanceof DrumNoteLabel)
            ModClientConfigs.DRUM_LABEL_TYPE.set((DrumNoteLabel)newLabel);
    }

    @Override
    public DrumNoteLabel[] getLabels() {
        return DrumNoteLabel.values();
    }
    @Override
    public DrumNoteLabel getCurrentLabel() {
        return ModClientConfigs.DRUM_LABEL_TYPE.get();
    }

    @Override
    protected DrumMidiOptionsScreen midiOptionsScreen() {
        return new DrumMidiOptionsScreen(MIDI_OPTIONS, this, instrumentScreen);
    }
    
}
