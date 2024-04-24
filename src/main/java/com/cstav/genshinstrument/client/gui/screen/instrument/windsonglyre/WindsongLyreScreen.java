package com.cstav.genshinstrument.client.gui.screen.instrument.windsonglyre;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.GridInstrumentScreen;
import com.cstav.genshinstrument.sound.GISounds;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
//NOTE: There just to make it load on mod setup
@EventBusSubscriber(Dist.CLIENT)
public class WindsongLyreScreen extends GridInstrumentScreen {
    public static final ResourceLocation INSTRUMENT_ID = new ResourceLocation(GInstrumentMod.MODID, "windsong_lyre");

    @Override
    public ResourceLocation getInstrumentId() {
        return INSTRUMENT_ID;
    }
    

    @Override
    public NoteSound[] getInitSounds() {
        return GISounds.WINDSONG_LYRE_NOTE_SOUNDS;
    }


    private static final InstrumentThemeLoader THEME_LOADER = new InstrumentThemeLoader(INSTRUMENT_ID);
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
    
}
