package com.cstav.genshinstrument.client.gui.screens.instrument.test.banjo;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.AbstractGridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.NoteGrid;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.AbstractInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.GridInstrumentOptionsScreen;
import com.cstav.genshinstrument.sound.ModSounds;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

//TODO remove after tests
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public class BanjoInstrumentScreen extends AbstractGridInstrumentScreen {
    public static final String INSTRUMENT_ID = "banjo";
    public static final String[] NOTES_LAYOUT = {"F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F"};


    public BanjoInstrumentScreen(InteractionHand hand) {
        super(hand);
    }
    @Override
    public ResourceLocation getInstrumentId() {
        return new ResourceLocation(GInstrumentMod.MODID, INSTRUMENT_ID);
    }
    

    @Override
    public NoteSound[] getSounds() {
        return ModSounds.BANJO;
    }

    @Override
    public String[] noteLayout() {
        return NOTES_LAYOUT;
    }


    @Override
    public NoteGrid initNoteGrid() {
        return new NoteGrid(rows(), columns(), getSounds(), this, NoteSound.MIN_PITCH);
    }

    @Override
    protected AbstractInstrumentOptionsScreen initInstrumentOptionsScreen() {
        return new GridInstrumentOptionsScreen(this) {
            
            @Override
            public boolean isPitchSliderEnabled() {
                return false;
            }

        };
    }


    private static final InstrumentThemeLoader THEME_LOADER = initThemeLoader(GInstrumentMod.MODID, INSTRUMENT_ID);
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
}
