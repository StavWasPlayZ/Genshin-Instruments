package com.cstav.genshinstrument.client.gui.screens.instrument.drum;

import java.util.HashMap;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.AbstractNoteLabels;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.sounds.ModSounds;
import com.cstav.genshinstrument.sounds.NoteSound;
import com.cstav.genshinstrument.util.RGBColor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.LinearLayoutWidget;
import net.minecraft.client.gui.components.LinearLayoutWidget.Orientation;
import net.minecraft.network.chat.Component;
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

    final HashMap<Integer, NoteButton> notes = new HashMap<>();
    @Override
    public Iterable<NoteButton> noteIterable() {
        return notes.values();
    }


    @Override
    protected void init() {
        initOptionsButton(height/2 + 25);

        final LinearLayoutWidget layout1 = createRow(DrumButtonType.DON, 2.25f, 83, 75),
            layout2 = createRow(DrumButtonType.KA, 1.5f, 65, 76);

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

    private LinearLayoutWidget createRow(DrumButtonType type, float widthPercent, int leftKeycode, int rightKeycode) {
        final LinearLayoutWidget layout = new LinearLayoutWidget(
            0, 0,
            (int)(width/widthPercent), NoteButton.getSize(),
            Orientation.HORIZONTAL
        );

        createButton(type, layout, leftKeycode);
        createButton(type, layout, rightKeycode);

        return layout;
    }
    private NoteButton createButton(final DrumButtonType btnType, final LinearLayoutWidget container, final int keycode) {
        final NoteButton btn = new NoteButton(
            btnType.getSound(), btnType.getLabelSupplier(),
            btnType.getIndex(), 2,
            this, 13, .3335f
        );

        container.addChild(btn);
        notes.put(keycode, btn);

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

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
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
    

    @OnlyIn(Dist.CLIENT)
    private static enum DrumButtonType {
        DON(0, ModSounds.GLORIOUS_DRUM[0], (note) ->
            Component.translatable(AbstractNoteLabels.TRANSLATABLE_PATH + "glorious_drum.don")
        ),
        KA(1, ModSounds.GLORIOUS_DRUM[1], (note) ->
            Component.translatable(AbstractNoteLabels.TRANSLATABLE_PATH + "glorious_drum.ka")
        );

        private final NoteSound sound;
        private final NoteLabelSupplier labelSupplier;
        private final int index;
        private DrumButtonType(final int index, final NoteSound sound, final NoteLabelSupplier labelSupplier) {
            this.sound = sound;
            this.labelSupplier = labelSupplier;
            this.index = index;
        }

        public NoteSound getSound() {
            return sound;
        }
        public NoteLabelSupplier getLabelSupplier() {
            return labelSupplier;
        }
        public int getIndex() {
            return index;
        }
    }
}