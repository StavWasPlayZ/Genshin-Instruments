package com.cstav.genshinstrument.client.gui.screens.instrument.partial;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;

import com.cstav.genshinstrument.GInstrumentMod;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;

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
@EventBusSubscriber(modid = GInstrumentMod.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class InstrumentThemeLoader {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final HashMap<ResourceLocation, JsonObject> CACHES = new HashMap<>();


    private static final ArrayList<InstrumentThemeLoader> LOADERS = new ArrayList<>();
    private static final Color DEF_NOTE_PRESSED_THEME = new Color(255, 249, 239);

    private final ResourceLocation InstrumentStyleLocation;
    private Color noteTheme, pressedNoteTheme, labelTheme;

    private ArrayList<Consumer<JsonObject>> listeners = new ArrayList<>();
    
    /**
     * Initializes a new Instrument Theme Loader and subsribes it to the resource load event.
     * @param instrumentStyleLocation The location of the instrument's JSON styler
     */
    public InstrumentThemeLoader(final ResourceLocation instrumentStyleLocation) {
        this.InstrumentStyleLocation = instrumentStyleLocation;

        LOADERS.add(this);
        addListener(this::loadColorTheme);
    }


    public void addListener(final Consumer<JsonObject> themeLoader) {
        listeners.add(themeLoader);
    }

    public void loadColorTheme(final JsonObject theme) {
        setNoteTheme(getTheme(theme.get("note_theme"), Color.BLACK));
        setLabelTheme(getTheme(theme.get("label_theme"), Color.BLACK));
        setPressedNoteTheme(getTheme(theme.get("note_pressed_theme"), DEF_NOTE_PRESSED_THEME));
    }

    /**
     * @param rgbArray The array represenation of an RGB value
     * @param def The default value of the theme
     * @return The theme as specified in the RGB array, or the default if 
     * any exception occured.
     * 
     * @see tryGetProperty
     */
    public static Color getTheme(final JsonElement rgbArray, final Color def) {
        if (rgbArray == null || !rgbArray.isJsonArray())
            return def;

        return tryGetProperty(rgbArray.getAsJsonArray(), (rgb) -> new Color(
            rgb.get(0).getAsInt(), rgb.get(1).getAsInt(), rgb.get(2).getAsInt()
        ), def);
    }
    /**
     * @param <T> The type of property to return
     * @param <J> The type of the json element
     * @param element The element to try and take the value from
     * @param getter The method for getting the desired element
     * @param def The default value of the theme
     * @return Either the value of the getter, or the default if 
     * any exception occured.
     */
    public static <T, J extends JsonElement> T tryGetProperty(final J element, Function<J, T> getter, final T def) {
        try {
            return getter.apply(element);
        } catch (Exception e) {
            LOGGER.error("Error retrieving property from JSON element", e);
            return def;
        }
    }


    @SubscribeEvent
    public static void registerReloadEvent(final RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new ResourceManagerReloadListener() {

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                for (final InstrumentThemeLoader instrumentLoader : LOADERS) {
                    final ResourceLocation styleLocation = instrumentLoader.getInstrumentStyleLocation();
                    
                    try {
                        JsonObject styleInfo;

                        // If it is already cached, then let it be
                        if (CACHES.containsKey(styleLocation)) {
                            styleInfo = CACHES.get(styleLocation);

                            for (final Consumer<JsonObject> listener : instrumentLoader.listeners)
                                listener.accept(styleInfo);

                            LOGGER.info("Loaded instrument style from the already cached "+styleLocation);
                            continue;
                        }


                        styleInfo = JsonParser.parseReader(
                            resourceManager.getResource(styleLocation).get().openAsReader()
                        ).getAsJsonObject();

                        // Call all load listeners on the current loader
                        for (final Consumer<JsonObject> listener : instrumentLoader.listeners)
                            listener.accept(styleInfo);

                        
                        CACHES.put(styleLocation, styleInfo);
                        LOGGER.info("Loaded and cached instrument style from "+styleLocation);

                    } catch (Exception e) {
                        LOGGER.error("Met an exception upon loading the instrument styler from "+styleLocation, e);
                        continue;
                    }
                }

                CACHES.clear();
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
        this.noteTheme = noteTheme;
    }
    
    public Color getPressedNoteTheme() {
        return pressedNoteTheme;
    }
    public void setPressedNoteTheme(Color pressedNoteTheme) {
        this.pressedNoteTheme = pressedNoteTheme;
    }

    public Color getLabelTheme() {
        return labelTheme;
    }
    public void setLabelTheme(Color labelTheme) {
        this.labelTheme = labelTheme;
    }

}