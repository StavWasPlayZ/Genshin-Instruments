package com.cstav.genshinstrument.client.gui.screen.instrument.djemdjemdrum;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.InstrumentOptionsScreen;
import com.cstav.genshinstrument.client.gui.widget.copied.LinearLayoutWidget;
import com.cstav.genshinstrument.client.gui.widget.copied.LinearLayoutWidget.Orientation;
import com.mojang.blaze3d.platform.InputConstants.Key;
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

        final LinearLayoutWidget layout1 = createRow(0),
            layout2 = createRow(1);

        // Make layout magic
        layout1.pack();
        layout2.pack();

        layout1.x = (width - layout1.getWidth()) / 2;
        layout1.y = (int)(height * .8f);
        layout2.x = (width - layout2.getWidth()) / 2;
        layout2.y = layout1.y - layout1.getHeight() - 10;

        layout1.pack();
        layout2.pack();


        addRenderableWidget(layout1);
        addRenderableWidget(layout2);

        // Initialize all the notes
        notesIterable().forEach(NoteButton::init);

        super.init();
    }

    private LinearLayoutWidget createRow(int row) {
        final LinearLayoutWidget generalLayout = new LinearLayoutWidget(
            0, 0,
            (int)(width/2f), getNoteSize(),
            Orientation.HORIZONTAL
        );

        final LinearLayoutWidget leftLayout = new LinearLayoutWidget(
            0, 0,
            -40, getNoteSize(),
            Orientation.HORIZONTAL
        );

        final LinearLayoutWidget rightLayout = new LinearLayoutWidget(
            0, 0,
            -40, getNoteSize(),
            Orientation.HORIZONTAL
        );

        generalLayout.addChild(leftLayout);
        generalLayout.addChild(rightLayout);
        generalLayout.pack();

        createButton(leftLayout, row, 1);
        createButton(leftLayout, row, 0);
        leftLayout.pack();

        createButton(rightLayout, row, 3);
        createButton(rightLayout, row, 2);
        rightLayout.pack();

        return generalLayout;
    }
    private DjemDjemDrumNoteButton createButton(LinearLayoutWidget container, int row, int column) {
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