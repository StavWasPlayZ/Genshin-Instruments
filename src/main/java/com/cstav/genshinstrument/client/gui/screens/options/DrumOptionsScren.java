package com.cstav.genshinstrument.client.gui.screens.options;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.config.enumType.label.DrumNoteLabel;
import com.cstav.genshinstrument.client.gui.screens.instrument.drum.AratakisGreatAndGloriousDrumScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.INoteLabel;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.BaseInstrumentOptionsScreen;

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
    protected void openMidiOptions() {
        minecraft.popGuiLayer();
        minecraft.pushGuiLayer(new DrumMidiOptionsScreen(MIDI_OPTIONS, this, instrumentScreen));
    }
    
}
