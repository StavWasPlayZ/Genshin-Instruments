package com.cstav.genshinstrument.client.gui.screens.instrument.partial;

import java.io.IOException;
import java.util.ArrayList;
import java.awt.Color;

import com.cstav.genshinstrument.Main;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * <p>Responsible for loading and processing the instrument style JSON object
 * (commonly used as {@code instrument_style.json}).</p>
 * It contains:
 * <ul>
 * <li><b>note_theme</b> - An array representing RGB values. Used for the Note Grid's text.</li>
 * <li><b>note_pressed_theme</b> - An array representing RGB values. Used for the Note Grid's text when pressed.</li>
 * </ul>
 * This class must be initialized during mod setup.
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Main.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class InstrumentThemeLoader {
    private static final ArrayList<InstrumentThemeLoader> LOADERS = new ArrayList<>();
    private static final Color DEF_NOTE_THEME = Color.BLACK, DEF_PRESSED_THEME = Color.BLACK;

    private final ResourceLocation InstrumentStyleLocation;
    private Color noteTheme, pressedNoteTheme;
    private InstrumentThemeLoadedEvent onThemeChanged;
    
    /**
     * Initializes a new Instrument Theme Loader and subsribes it to the resource load event.
     * @param instrumentStyleLocation The location of the instrument's JSON styler
     */
    public InstrumentThemeLoader(ResourceLocation instrumentStyleLocation) {
        this.InstrumentStyleLocation = instrumentStyleLocation;

        LOADERS.add(this);
    }


    @SubscribeEvent
    public static void registerRloadEvent(final RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new ResourceManagerReloadListener() {

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                for (final InstrumentThemeLoader lyreLoader : LOADERS) {
                    JsonObject style;
                    try {
                        style = JsonParser.parseReader(
                            resourceManager.getResource(lyreLoader.getInstrumentStyleLocation()).get().openAsReader()
                        ).getAsJsonObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }

                    lyreLoader.noteTheme = getTheme(style.get("note_theme"),
                        DEF_NOTE_THEME);
                    lyreLoader.pressedNoteTheme = getTheme(style.get("note_pressed_theme"),
                        DEF_PRESSED_THEME);

                    if (lyreLoader.onThemeChanged != null)
                        lyreLoader.onThemeChanged.run(lyreLoader.noteTheme, lyreLoader.pressedNoteTheme);
                }
            }
            private static Color getTheme(final JsonElement rgbArray, final Color def) {
                try {
                    final JsonArray rgb = rgbArray.getAsJsonArray();
                    return new Color(
                        rgb.get(0).getAsInt(), rgb.get(1).getAsInt(), rgb.get(2).getAsInt()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    return def;
                }
            }
            
        });
    }


    public ResourceLocation getInstrumentStyleLocation() {
        return InstrumentStyleLocation;
    }

    public Color getNoteTheme() {
        return noteTheme;
    }
    public void setNoteTheme(Color noteTheme) {
        onThemeChanged.run(this.noteTheme = noteTheme, pressedNoteTheme);
    }
    
    public Color getPressedNoteTheme() {
        return pressedNoteTheme;
    }
    public void setPressedNoteTheme(Color pressedNoteTheme) {
        onThemeChanged.run(noteTheme, this.pressedNoteTheme = pressedNoteTheme);
    }

    public void setOnThemeChanged(InstrumentThemeLoadedEvent onThemeChanged) {
        this.onThemeChanged = onThemeChanged;
    }


    @OnlyIn(Dist.CLIENT)
    @FunctionalInterface
    public static interface InstrumentThemeLoadedEvent {
        void run(final Color noteTheme, final Color pressedNoteTheme);
    }

}
