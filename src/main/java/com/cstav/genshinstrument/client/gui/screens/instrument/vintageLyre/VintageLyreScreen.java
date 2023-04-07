package com.cstav.genshinstrument.client.gui.screens.instrument.vintageLyre;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.NoteGrid;
import com.cstav.genshinstrument.sounds.ModSounds;
import com.cstav.genshinstrument.util.RGBColor;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
//NOTE: There just to make it load on mod startup
@EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT)
public class VintageLyreScreen extends AbstractInstrumentScreen {
    @Override
    protected ResourceLocation getInstrumentResourcesLocation() {
        return new ResourceLocation(Main.MODID, "textures/gui/instrument/vintage_lyre");
    }
    
    private static final InstrumentThemeLoader THEME_LOADER = new InstrumentThemeLoader(
        new ResourceLocation(Main.MODID, "textures/gui/instrument/vintage_lyre/instrument_style.json"),
        new RGBColor(127, 179, 99), new RGBColor(255, 249, 239)
    );
    @Override
    protected InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }

    @Override
    public SoundEvent[] getSounds() {
        return ModSounds.getSoundsFromArr(ModSounds.VINTAGE_LYRE_NOTE_SOUNDS);
    }


    
    @Override
    public NoteGrid initNoteGrid() {
        return new VintageNoteGrid(
            ROWS, COLUMNS, getSounds(),
            getResourceFromRoot(NOTE_DIR),
            () -> getThemeLoader().getNoteTheme().getNumeric(),
            () -> getThemeLoader().getPressedNoteTheme().getNumeric()
        );
    }



    public static void open() {
        Minecraft.getInstance().setScreen(new VintageLyreScreen());
    }
    
}
