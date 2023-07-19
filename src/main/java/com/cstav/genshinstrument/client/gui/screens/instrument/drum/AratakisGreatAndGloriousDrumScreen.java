package com.cstav.genshinstrument.client.gui.screens.instrument.drum;

import java.util.HashMap;
import java.util.Map;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.AbstractInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.DrumOptionsScren;
import com.cstav.genshinstrument.client.gui.widget.copied.LinearLayoutWidget;
import com.cstav.genshinstrument.client.gui.widget.copied.LinearLayoutWidget.Orientation;
import com.cstav.genshinstrument.sound.ModSounds;
import com.cstav.genshinstrument.sound.NoteSound;
import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
//NOTE: There just to make it load on mod setup
@EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT)
// ikik im funny, long name, thank you
public class AratakisGreatAndGloriousDrumScreen extends AbstractInstrumentScreen {
    public static final String INSTRUMENT_ID = "glorious_drum";

    public AratakisGreatAndGloriousDrumScreen(InteractionHand hand) {
        super(hand);
    }
    @Override
    public ResourceLocation getInstrumentId() {
        return new ResourceLocation(Main.MODID, INSTRUMENT_ID);
    }

    @Override
    public ResourceLocation getNotesLocation() {
        return getResourceFromRoot("note/notes.png");
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
    protected AbstractInstrumentOptionsScreen initInstrumentOptionsScreen() {
        return new DrumOptionsScren(this);
    }


    @Override
    protected void init() {
        initOptionsButton(height/2 + 25);

        final LinearLayoutWidget layout1 = createRow(DrumButtonType.DON, 2.25f),
            layout2 = createRow(DrumButtonType.KA, 1.5f);

        // Make layout magic
        layout1.pack();
        layout2.pack();

        layout1.x = (width - layout1.getWidth()) / 2;
        layout1.y = (int)(height * .8f);
        layout2.x = (width - layout2.getWidth()) / 2;
        layout2.y = layout1.y - layout1.getHeight();

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
            (int)(width/widthPercent), NoteButton.getSize(),
            Orientation.HORIZONTAL
        );

        createButton(type, layout, type.getKeys().left, false);
        createButton(type, layout, type.getKeys().right, true);

        return layout;
    }
    private NoteButton createButton(DrumButtonType btnType, LinearLayoutWidget container, Key key, boolean isRight) {
        final NoteButton btn = new DrumNoteButton(btnType, isRight, this);

        container.addChild(btn);
        notes.put(key, btn);

        return btn;
    }


    @Override
    public NoteSound[] getSounds() {
        return ModSounds.GLORIOUS_DRUM;
    }

    private static final InstrumentThemeLoader THEME_LOADER = initThemeLoader(Main.MODID, INSTRUMENT_ID);
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
    
}