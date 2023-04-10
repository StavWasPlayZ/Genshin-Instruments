package com.cstav.genshinstrument.client.gui.screens.instrument.windsongLyre;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.sounds.NoteSound;
import com.cstav.genshinstrument.sounds.ModSounds;
import com.cstav.genshinstrument.util.RGBColor;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
//NOTE: There just to make it load on mod startup
@EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT)
public class WindsongLyreScreen extends AbstractInstrumentScreen {
    @Override
    protected ResourceLocation getInstrumentResourcesLocation() {
        return new ResourceLocation(Main.MODID, "textures/gui/instrument/lyre");
    }
    
    private static final InstrumentThemeLoader THEME_LOADER = new InstrumentThemeLoader(
        new ResourceLocation(Main.MODID, "textures/gui/instrument/lyre/instrument_style.json"),
        new RGBColor(154, 228, 212), new RGBColor(255, 249, 239)
    );
    @Override
    protected InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }

    @Override
    public NoteSound[] getSounds() {
        return ModSounds.LYRE_NOTE_SOUNDS;
    }



    public static void open() {
        Minecraft.getInstance().setScreen(new WindsongLyreScreen());
    }
    
}
