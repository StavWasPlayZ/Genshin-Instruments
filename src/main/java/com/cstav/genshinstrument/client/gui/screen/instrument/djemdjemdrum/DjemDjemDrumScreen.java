package com.cstav.genshinstrument.client.gui.screen.instrument.djemdjemdrum;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.InstrumentOptionsScreen;
import com.mojang.blaze3d.platform.InputConstants.Key;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.LinearLayout.Orientation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class DjemDjemDrumScreen extends InstrumentScreen {
    public static final ResourceLocation INSTRUMENT_ID = new ResourceLocation(GInstrumentMod.MODID, "djem_djem_drum");

    public static final String[] NOTE_LAYOUT = {
        "C#", "C#", "F", "Bb",
        "C#", "C", "F", "F",
    };

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
        return new DjemDjemDrumOptionsScreen(this);
    }

    @Override
    protected void init() {
        initOptionsButton(height/2 + 25);

        final LinearLayout layout1 = createRow(0),
            layout2 = createRow(1);

        // Make layout magic
        layout1.arrangeElements();
        layout2.arrangeElements();

        layout1.setPosition((width - layout1.getWidth()) / 2, (int)(height * .8f));
        layout2.setPosition((width - layout2.getWidth()) / 2, layout1.getY() - layout1.getHeight() - 10);

        layout1.arrangeElements();
        layout2.arrangeElements();


        layout1.visitWidgets(this::addRenderableWidget);
        layout2.visitWidgets(this::addRenderableWidget);

        // Initialize all the notes
        notesIterable().forEach(NoteButton::init);

        super.init();
    }

    private LinearLayout createRow(int row) {
        final LinearLayout generalLayout = new LinearLayout(
            0, 0,
            (int)(width/2f), getNoteSize(),
            Orientation.HORIZONTAL
        );

        final LinearLayout leftLayout = new LinearLayout(
            0, 0,
            -40, getNoteSize(),
            Orientation.HORIZONTAL
        );

        final LinearLayout rightLayout = new LinearLayout(
            0, 0,
            -40, getNoteSize(),
            Orientation.HORIZONTAL
        );

        createButton(leftLayout, row, 1);
        createButton(leftLayout, row, 0);
        createButton(rightLayout, row, 3);
        createButton(rightLayout, row, 2);

        generalLayout.addChild(leftLayout);
        generalLayout.addChild(rightLayout);

        return generalLayout;
    }
    private DjemDjemDrumNoteButton createButton(LinearLayout container, int row, int column) {
        final DjemDjemDrumNoteButton btn = new DjemDjemDrumNoteButton(this, row, column);

        container.addChild(btn);
        notes.put(btn.getKey(), btn);

        return btn;
    }


    @Override
    public String[] noteLayout() {
        return NOTE_LAYOUT;
    }

    public static final InstrumentThemeLoader THEME_LOADER = new InstrumentThemeLoader(INSTRUMENT_ID);
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }

}