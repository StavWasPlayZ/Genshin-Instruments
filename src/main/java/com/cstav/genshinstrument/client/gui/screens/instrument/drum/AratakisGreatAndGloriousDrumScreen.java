package com.cstav.genshinstrument.client.gui.screens.instrument.drum;

import java.util.HashMap;
import java.util.Map;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.BaseInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.DrumOptionsScren;
import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.LinearLayout.Orientation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
//NOTE: There just to make it load on mod setup
@EventBusSubscriber(Dist.CLIENT)
public class AratakisGreatAndGloriousDrumScreen extends AbstractInstrumentScreen {
    public static final ResourceLocation INSTRUMENT_ID = new ResourceLocation(GInstrumentMod.MODID, "glorious_drum");
    public static final String[] NOTE_LAYOUT = {"D", "G"};

    public AratakisGreatAndGloriousDrumScreen(InteractionHand hand) {
        super(hand);
    }
    @Override
    public ResourceLocation getInstrumentId() {
        return INSTRUMENT_ID;
    }

    @Override
    public ResourceLocation getNoteSymbolsLocation() {
        return getResourceFromRoot("note/notes.png", false);
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
    protected BaseInstrumentOptionsScreen initInstrumentOptionsScreen() {
        return new DrumOptionsScren(this);
    }


    @Override
    protected void init() {
        initOptionsButton(height/2 + 25);

        final LinearLayout layout1 = createRow(DrumButtonType.DON, 2f),
            layout2 = createRow(DrumButtonType.KA, 1.3f);

        // Make layout magic
        layout1.arrangeElements();
        layout2.arrangeElements();

        layout1.setPosition((width - layout1.getWidth()) / 2, (int)(height * .8f));
        layout2.setPosition((width - layout2.getWidth()) / 2, layout1.getY() - layout1.getHeight()/2);

        layout1.arrangeElements();
        layout2.arrangeElements();

        
        layout1.visitWidgets(this::addRenderableWidget);
        layout2.visitWidgets(this::addRenderableWidget);

        // Initialize all the notes
        notesIterable().forEach(NoteButton::init);

        super.init();
    }

    private LinearLayout createRow(DrumButtonType type, float widthPercent) {
        final LinearLayout layout = new LinearLayout(
            0, 0,
            (int)(width/widthPercent), getNoteSize(),
            Orientation.HORIZONTAL
        );

        createButton(type, layout, type.getKeys().left, false);
        createButton(type, layout, type.getKeys().right, true);

        return layout;
    }
    private NoteButton createButton(DrumButtonType btnType, LinearLayout container, Key key, boolean isRight) {
        final NoteButton btn = new DrumNoteButton(btnType, isRight, this);

        container.addChild(btn);
        notes.put(key, btn);

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
    
}