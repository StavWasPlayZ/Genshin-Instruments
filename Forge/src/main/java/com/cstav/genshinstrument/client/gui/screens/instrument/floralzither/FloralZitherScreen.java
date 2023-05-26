package com.cstav.genshinstrument.client.gui.screens.instrument.floralzither;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractGridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.AbstractInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.FloralZitherOptionsScreen;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
//NOTE: There just to make it load on mod setup
@EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT)
public class FloralZitherScreen extends AbstractGridInstrumentScreen {
    public static final String INSTRUMENT_ID = "floral_zither";
    
    public FloralZitherScreen(InteractionHand hand) {
        super(hand);
    }
    @Override
    public String getInstrumentId() {
        return INSTRUMENT_ID;
    }

    
    @Override
    public NoteSound[] getSounds() {
        return ((FloralZitherOptionsScreen)optionsScreen).getPerferredSoundType().soundArr().get();
    }

    @Override
    protected AbstractInstrumentOptionsScreen initInstrumentOptionsScreen() {
        return new FloralZitherOptionsScreen(this);
    }

    
    private static final InstrumentThemeLoader THEME_LOADER = initThemeLoader(Main.MODID, INSTRUMENT_ID);
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
    
}
