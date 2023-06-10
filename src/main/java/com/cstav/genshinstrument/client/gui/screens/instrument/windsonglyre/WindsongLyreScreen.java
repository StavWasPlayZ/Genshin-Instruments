package com.cstav.genshinstrument.client.gui.screens.instrument.windsonglyre;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractGridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.sound.ModSounds;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
//NOTE: There just to make it load on mod setup
@EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT)
public class WindsongLyreScreen extends AbstractGridInstrumentScreen {
    public static final String INSTRUMENT_ID = "windsong_lyre";

    public WindsongLyreScreen(InteractionHand hand) {
        super(hand);
    }
    @Override
    public ResourceLocation getInstrumentId() {
        return new ResourceLocation(Main.MODID, INSTRUMENT_ID);
    }
    

    @Override
    public NoteSound[] getSounds() {
        return ModSounds.WINDSONG_LYRE_NOTE_SOUNDS;
    }


    private static final InstrumentThemeLoader THEME_LOADER = initThemeLoader(Main.MODID, INSTRUMENT_ID);
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
    
}
