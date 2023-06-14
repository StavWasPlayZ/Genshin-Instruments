package com.cstav.genshinstrument.client.gui.screens.options.instrument;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.config.enumType.label.NoteGridLabel;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractGridInstrumentScreen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = Main.MODID, bus = Bus.FORGE)
public class GridInstrumentOptionsScreen extends AbstractInstrumentOptionsScreen {


    public GridInstrumentOptionsScreen(final AbstractGridInstrumentScreen screen) {
        super(screen);
    }
    public GridInstrumentOptionsScreen(final Screen lastScreen) {
        super(lastScreen);
    }


    @Override
    public NoteGridLabel[] getLabels() {
        return NoteGridLabel.values();
    }
    @Override
    public NoteGridLabel getCurrentLabel() {
        return ModClientConfigs.GRID_LABEL_TYPE.get();
    }
    
}
