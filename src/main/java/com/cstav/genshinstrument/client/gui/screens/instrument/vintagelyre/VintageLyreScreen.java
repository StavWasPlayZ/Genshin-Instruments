package com.cstav.genshinstrument.client.gui.screens.instrument.vintagelyre;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.AbstractGridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.NoteGrid;
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
public class VintageLyreScreen extends AbstractGridInstrumentScreen {
    public static final String INSTRUMENT_ID = "vintage_lyre";
    
    public VintageLyreScreen(InteractionHand hand) {
        super(hand);
    }
    @Override
    public ResourceLocation getInstrumentId() {
        return new ResourceLocation(Main.MODID, INSTRUMENT_ID);
    }


    @Override
    public NoteSound[] getSounds() {
        return ModSounds.VINTAGE_LYRE_NOTE_SOUNDS;
    }
    
    @Override
    public NoteGrid initNoteGrid() {
        return new VintageNoteGrid(
            rows(), columns(), getSounds(), this
        );
    }
    
    
    private static final InstrumentThemeLoader THEME_LOADER = initThemeLoader(Main.MODID, INSTRUMENT_ID);
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
}
