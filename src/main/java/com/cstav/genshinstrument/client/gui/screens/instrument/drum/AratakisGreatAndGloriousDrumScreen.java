package com.cstav.genshinstrument.client.gui.screens.instrument.drum;

import java.util.HashMap;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.AbstractInstrumentOptionsScreen;
import com.cstav.genshinstrument.sounds.ModSounds;
import com.cstav.genshinstrument.sounds.NoteSound;
import com.cstav.genshinstrument.util.RGBColor;
import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.LinearLayoutWidget;
import net.minecraft.client.gui.components.LinearLayoutWidget.Orientation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
//NOTE: There just to make it load on mod startup
@EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT)
// ikik im funny, long name, thank you
public class AratakisGreatAndGloriousDrumScreen extends AbstractInstrumentScreen {
    public static final String INSTRUMENT_ID = "glorious_drum";

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

        final LinearLayoutWidget layout1 = createRow(DrumButtonType.DON, 2.25f),
            layout2 = createRow(DrumButtonType.KA, 1.5f);

        // Make layout magic
        layout1.pack();
        layout2.pack();

        layout1.setPosition((width - layout1.getWidth()) / 2, (int)(height * .8f));
        layout2.setPosition((width - layout2.getWidth()) / 2, layout1.getY() - layout1.getHeight());

        addRenderableWidget(layout1);
        addRenderableWidget(layout2);

        // Initialize all the notes
        noteIterable().forEach((note) -> note.init());

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
        new RGBColor(197, 213, 172), new RGBColor(232, 127, 74)
    );
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }

    @Override
    public NoteSound[] getSounds() {
        return ModSounds.GLORIOUS_DRUM;
    }


    
    public static void open() {
        Minecraft.getInstance().setScreen(new AratakisGreatAndGloriousDrumScreen());
    }
    
}