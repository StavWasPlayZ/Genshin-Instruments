package com.cstav.genshinstrument.client.gui.screens.instrument.drum;

import java.awt.Color;
import java.util.HashMap;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.AbstractInstrumentOptionsScreen;
import com.cstav.genshinstrument.sound.ModSounds;
import com.cstav.genshinstrument.sound.NoteSound;
import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.LinearLayout.Orientation;
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
    
    /**
     * Maps keycodes to their respected note button
     */
    private final HashMap<Integer, NoteButton> notes = new HashMap<>();
    @Override
    public Iterable<NoteButton> noteIterable() {
        return notes.values();
    }

    @Override
    protected AbstractInstrumentOptionsScreen initInstrumentOptionsScreen() {
        return new DrumOptionsScren(this);
    }


    @Override
    protected void init() {
        initOptionsButton(height/2 + 25);

        final LinearLayout layout1 = createRow(DrumButtonType.DON, 2.25f),
            layout2 = createRow(DrumButtonType.KA, 1.5f);

        // Make layout magic
        layout1.arrangeElements();
        layout2.arrangeElements();

        layout1.setPosition((width - layout1.getWidth()) / 2, (int)(height * .8f));
        layout2.setPosition((width - layout2.getWidth()) / 2, layout1.getY() - layout1.getHeight());

        layout1.visitWidgets(this::addRenderableWidget);
        layout2.visitWidgets(this::addRenderableWidget);

        // Initialize all the notes
        noteIterable().forEach((note) -> note.init());

        super.init();
    }

    private LinearLayout createRow(DrumButtonType type, float widthPercent) {
        final LinearLayout layout = new LinearLayout(
            0, 0,
            (int)(width/widthPercent), NoteButton.getSize(),
            Orientation.HORIZONTAL
        );

        createButton(type, layout, type.getKeys().left, false);
        createButton(type, layout, type.getKeys().right, true);

        return layout;
    }
    private NoteButton createButton(DrumButtonType btnType, LinearLayout container, Key key, boolean isRight) {
        final NoteButton btn = new DrumNoteButton(btnType, isRight, this);

        container.addChild(btn);
        notes.put(key.getValue(), btn);

        return btn;
    }


    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (notes.containsKey(pKeyCode)) {
            notes.get(pKeyCode).play(true);
            return true;
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (notes.containsKey(pKeyCode)) {
            notes.get(pKeyCode).locked = false;
            return true;
        }

        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }


    @Override
    protected ResourceLocation getInstrumentResourcesLocation() {
        return new ResourceLocation(Main.MODID, genPath(INSTRUMENT_ID));
    }
    
    private static final InstrumentThemeLoader THEME_LOADER = new InstrumentThemeLoader(
        new ResourceLocation(Main.MODID, genStylerPath(INSTRUMENT_ID)),
        new Color(197, 213, 172), new Color(232, 127, 74)
    );
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }

    @Override
    public NoteSound[] getSounds() {
        return ModSounds.GLORIOUS_DRUM;
    }
    
}