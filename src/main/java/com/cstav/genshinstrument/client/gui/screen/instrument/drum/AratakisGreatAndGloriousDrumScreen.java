package com.cstav.genshinstrument.client.gui.screen.instrument.drum;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.DrumOptionsScren;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.InstrumentOptionsScreen;
import com.cstav.genshinstrument.client.gui.widget.copied.LinearLayoutWidget;
import com.cstav.genshinstrument.client.gui.widget.copied.LinearLayoutWidget.Orientation;
import com.cstav.genshinstrument.client.midi.InstrumentMidiReceiver;
import com.mojang.blaze3d.platform.InputConstants.Key;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
//NOTE: There just to make it load on mod setup
@EventBusSubscriber(Dist.CLIENT)
public class AratakisGreatAndGloriousDrumScreen extends InstrumentScreen {
    public static final ResourceLocation INSTRUMENT_ID = new ResourceLocation(GInstrumentMod.MODID, "glorious_drum");
    public static final String[] NOTE_LAYOUT = {"D", "G"};

    @Override
    public ResourceLocation getInstrumentId() {
        return INSTRUMENT_ID;
    }

    
    /**
     * Maps keycodes to their respected note button
     */
    private final HashMap<Key, NoteButton> notes = new HashMap<>();
    @Override
    public Map<Key, NoteButton> noteMap() {
        return notes;
    }

    @Override
    protected InstrumentOptionsScreen initInstrumentOptionsScreen() {
        return new DrumOptionsScren(this);
    }


    @Override
    protected void init() {
        initOptionsButton(height/2 + 25);

        final LinearLayoutWidget layout1 = createRow(DrumButtonType.DON, 2),
            layout2 = createRow(DrumButtonType.KA, 1.3f);

        // Make layout magic
        layout1.pack();
        layout2.pack();

        layout1.x = (width - layout1.getWidth()) / 2;
        layout1.y = (int)(height * .8f);
        layout2.x = (width - layout2.getWidth()) / 2;
        layout2.y = layout1.y - layout1.getHeight()/2;

        layout1.pack();
        layout2.pack();

        
        addRenderableWidget(layout1);
        addRenderableWidget(layout2);

        // Initialize all the notes
        notesIterable().forEach(NoteButton::init);

        super.init();
    }

    private LinearLayoutWidget createRow(DrumButtonType type, float widthPercent) {
        final LinearLayoutWidget layout = new LinearLayoutWidget(
            0, 0,
            (int)(width/widthPercent), getNoteSize(),
            Orientation.HORIZONTAL
        );

        createButton(type, layout, false);
        createButton(type, layout, true);

        return layout;
    }
    private DrumNoteButton createButton(DrumButtonType btnType, LinearLayoutWidget container, boolean isRight) {
        final DrumNoteButton btn = new DrumNoteButton(btnType, isRight, this);

        container.addChild(btn);
        notes.put(btn.getKey(), btn);

        return btn;
    }
    

    @Override
    public String[] noteLayout() {
        return NOTE_LAYOUT;
    }

    private static final InstrumentThemeLoader THEME_LOADER = new InstrumentThemeLoader(INSTRUMENT_ID);
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
    

    @Override
    public InstrumentMidiReceiver initMidiReceiver() {
        return new InstrumentMidiReceiver(this) {

            private static boolean donRight = false, kaRight = false;

            @Override
            protected @Nullable NoteButton handleMidiPress(int note, int key) {
                final boolean isKa = (ddt() == DominantDrumType.KA) || ((ddt() == DominantDrumType.BOTH) && (note >= 12));

                setPitch(note - (isKa ? 19 : 2));

                for (final NoteButton noteButton : notesIterable()) {
                    final DrumNoteButton dnb = (DrumNoteButton) noteButton;
                    if (dnb.btnType != (isKa ? DrumButtonType.KA : DrumButtonType.DON))
                        continue;

                    // Toggle between left/right keys
                    // just visually fun stuff
                    if (isKa) {
                        if (dnb.isRight == kaRight) {
                            kaRight = !kaRight;
                            return dnb;
                        }
                    } else {
                        if (dnb.isRight == donRight) {
                            donRight = !donRight;
                            return dnb;
                        }
                    }
                }

                return null;
            }

            @Override
            protected int minMidiNote() {
                return ((ddt() == DominantDrumType.BOTH) || ddt() == DominantDrumType.DON) ? -10 : 7;
            }
            @Override
            protected int maxMidiNote() {
                return ((ddt() == DominantDrumType.BOTH) || ddt() == DominantDrumType.KA) ? 32 : 15;
            }
        };
    }



    /**
     * Shorthand for {@code ModClientConfigs.DOMINANT_DRUM_TYPE.get()}
     */
    private final static DominantDrumType ddt() {
        return ModClientConfigs.DOMINANT_DRUM_TYPE.get();
    }

}