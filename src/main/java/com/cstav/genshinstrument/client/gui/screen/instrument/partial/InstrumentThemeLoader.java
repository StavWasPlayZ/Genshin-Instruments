package com.cstav.genshinstrument.client.gui.screen.instrument.partial;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.util.CommonUtil;
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
import org.slf4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * Responsible for loading and processing the instrument style JSON object, used as {@code instrument_style.json}.
 * See {@link InstrumentThemeLoader#loadColorTheme(JsonObject) implementations} to learn more about built-in properties.
 * </p>
 * 
 * This class must be initialized during mod setup.
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = GInstrumentMod.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class InstrumentThemeLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String JSON_STYLER_NAME = "instrument_style.json";

    public static final ResourceLocation
        INSTRUMENTS_META_LOC = InstrumentScreen.getInternalResourceFromGlob("instruments.meta.json"),
        GLOBAL_LOC = InstrumentScreen.getInternalResourceFromGlob("instrument/global")
    ;

    private static boolean isGlobalThemed;
    public static boolean isGlobalThemed() {
        return isGlobalThemed;
    }
        

    private static final HashMap<ResourceLocation, JsonObject> CACHES = new HashMap<>();


    private static final ArrayList<InstrumentThemeLoader> LOADERS = new ArrayList<>();
    private static final Color DEF_PRESSED_THEME = new Color(255, 249, 239);

    public final ResourceLocation resourcesRootDir, instrumentId;
    private final boolean ignoreGlobal;

    private Color
        labelPressed, labelReleased,
        notePressed, noteReleased,
        noteRing
    ;

    private final ArrayList<Consumer<JsonObject>> listeners = new ArrayList<>();
    
    /**
     * Initializes a new Instrument Theme Loader and subscribes it to the resource load event.
     * @param resourceRootDir The location of the root resources folder to derive styles from
     * @param ignoreGlobal When a global resource pack is enabled, defines whether this theme loader ignores it
     */
    public InstrumentThemeLoader(ResourceLocation resourceRootDir, ResourceLocation instrumentId, boolean ignoreGlobal) {
        this.resourcesRootDir = resourceRootDir;
        this.instrumentId = instrumentId;
        this.ignoreGlobal = ignoreGlobal;

        LOADERS.add(this);
        addListener(this::loadColorTheme);
    }
    /**
     * Initializes a new Instrument Theme Loader and subscribes it to the resource load event.
     * @param resourceRootDir The location of the root resources folder to derive styles from
     */
    public InstrumentThemeLoader(ResourceLocation resourceRootDir, ResourceLocation instrumentId) {
        this(resourceRootDir, instrumentId, false);
    }
    /**
     * Initializes a new Instrument Theme Loader and subscribes it to the resource load event.
     * @param instrumentId The ID of the instrument in question,
     * as well as the location of the root resources directory to derive styles from
     */
    public InstrumentThemeLoader(ResourceLocation instrumentId) {
        this(InstrumentScreen.getInstrumentRootPath(instrumentId), instrumentId);
    }

    public static InstrumentThemeLoader fromOther(ResourceLocation otherInstrumentId, ResourceLocation instrumentId) {
        return new InstrumentThemeLoader(InstrumentScreen.getInstrumentRootPath(otherInstrumentId), instrumentId);
    }


    public void addListener(final Consumer<JsonObject> themeLoader) {
        listeners.add(themeLoader);
    }

    private void loadColorTheme(final JsonObject theme) {
        if (!theme.has("label") || !theme.has("note")) {
            loadLegacyTheme(theme);
            return;
        }

        setPressStatedTheme(theme, "note",
            this::setNotePressed, this::setNoteReleased,
            DEF_PRESSED_THEME, Color.BLACK
        );
        setPressStatedTheme(theme, "label",
            this::setLabelPressed, this::setLabelReleased,
            notePressed(), Color.BLACK
        );

        setNoteRing(getTheme(theme, "note_ring", labelReleased()));
    }
    public void setPressStatedTheme(JsonObject theme, String propName,
            Consumer<Color> pressConsumer, Consumer<Color> releaseConsumer,
            Color defPress, Color defRelease) {
        final JsonObject pressThemes = theme.getAsJsonObject(propName);
 
        if (pressThemes == null) {
            pressConsumer.accept(defPress);
            releaseConsumer.accept(defRelease);
            return;
        }

        pressConsumer.accept(getTheme(pressThemes, "pressed", defPress));
        releaseConsumer.accept(getTheme(pressThemes, "released", defRelease));
    }

    @Deprecated(forRemoval = true)
    private void loadLegacyTheme(final JsonObject theme) {
        LOGGER.warn("The active resourcepack is using the legacy instrument styler format on instrument "+instrumentId+"!");
        LOGGER.warn("The format is deprecated and will be left unsupported, and should be migrated to the new format.");
        LOGGER.warn("Please update your pack, contact the author of it, or visit the Genshin Instruments GitHub repository to learn more."); //TODO add link to resourcepack wiki page

        setNoteTheme(getTheme(theme, "note_theme", Color.BLACK));
        setLabelTheme(getTheme(theme, "label_theme", Color.BLACK));
        setPressedNoteTheme(getTheme(theme, "note_pressed_theme", DEF_PRESSED_THEME));
        setNoteRingTheme(getTheme(theme, "note_ring_theme", getNoteTheme()));

        setLabelPressed(getPressedNoteTheme());
    }


    /**
     * @param rgbArray The array represenation of an RGB value
     * @param def The default value of the theme
     * @return The theme as specified in the RGB array, or the default if 
     * any exception occured.
     * 
     * @see tryGetProperty
     */
    public Color getTheme(JsonObject theme, String propertyName, Color def) {
        final JsonElement rgbArray = theme.get(propertyName);

        if (rgbArray == null || !rgbArray.isJsonArray())
            return def;

        return tryGetProperty(propertyName, rgbArray.getAsJsonArray(), (rgb) -> new Color(
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
    public <T, J extends JsonElement> T tryGetProperty(String property, J element, Function<J, T> getter, T def) {
        try {
            return getter.apply(element);
        } catch (Exception e) {
            LOGGER.error("Error retrieving JSON property for "+instrumentId, e);
            return def;
        }
    }


    @SubscribeEvent
    public static void registerReloadEvent(final RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new ResourceManagerReloadListener() {

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                InstrumentThemeLoader.reload(resourceManager);
            }
            
        });
    }

    private static void reload(final ResourceManager resourceManager) {
        // Handle global resource packs
        isGlobalThemed = false;

        try {
            final boolean isGlobalThemed = getJsonFromResource(resourceManager, INSTRUMENTS_META_LOC).get("is_global_pack").getAsBoolean();

            if (isGlobalThemed)
                LOGGER.info("Instrument global themes enabled; loading all instrument resources from "+GLOBAL_LOC);
        } catch (Exception e) {}


        for (final InstrumentThemeLoader instrumentLoader : LOADERS)
            instrumentLoader.performReload(resourceManager);

        CACHES.clear();
    }

    private void performReload(final ResourceManager resourceManager) {
        final String logSuffix = " for "+instrumentId;

        final ResourceLocation styleLocation = getStylerLocation();
        JsonObject styleInfo;
        
        try {

            // If it is already cached, then let it be
            if (CACHES.containsKey(styleLocation)) {
                styleInfo = CACHES.get(styleLocation);
    
                for (final Consumer<JsonObject> listener : listeners)
                    listener.accept(styleInfo);
    
                LOGGER.info("Loaded instrument style from already cached "+styleLocation + logSuffix);
                return;
            }
    
    
            styleInfo = getJsonFromResource(resourceManager, styleLocation);
    
            // Call all load listeners on the current loader
            for (final Consumer<JsonObject> listener : listeners)
                listener.accept(styleInfo);
    
            
            CACHES.put(styleLocation, styleInfo);
            LOGGER.info("Loaded and cached instrument style from "+styleLocation + logSuffix);

        } catch (Exception e) {
            LOGGER.error("Met an exception upon loading the instrument styler from "+styleLocation + logSuffix, e);
        }

    }

    private static JsonObject getJsonFromResource(ResourceManager resourceManager, ResourceLocation location) throws IOException {
        return JsonParser.parseReader(
            new InputStreamReader(resourceManager.getResource(location).getInputStream())
        ).getAsJsonObject();
    }



    public ResourceLocation getResourcesRootDir() {
        return resourcesRootDir;
    }

    public ResourceLocation getStylerLocation() {
        return CommonUtil.withSuffix(
            (!ignoreGlobal && isGlobalThemed) ? GLOBAL_LOC : getResourcesRootDir(),
            "/"+JSON_STYLER_NAME
        );
    }



    public Color labelPressed() {
        return labelPressed;
    }
    public void setLabelPressed(Color labelPressed) {
        this.labelPressed = labelPressed;
    }

    public Color labelReleased() {
        return labelReleased;
    }
    public void setLabelReleased(Color labelReleased) {
        this.labelReleased = labelReleased;
    }


    public Color notePressed() {
        return notePressed;
    }
    public void setNotePressed(Color notePressed) {
        this.notePressed = notePressed;
    }

    public Color noteReleased() {
        return noteReleased;
    }
    public void setNoteReleased(Color noteReleased) {
        this.noteReleased = noteReleased;
    }

    
    public Color noteRing() {
        return getColorTheme(noteRing);
    }
    public void setNoteRing(Color noteRingTheme) {
        this.noteRing = noteRingTheme;
    }


    
    /* --------- Legacy Styler Properties --------- */
    //TODO remove in v6.0

    @Deprecated(forRemoval = true)
    public Color getNoteTheme() {
        return getColorTheme(labelReleased);
    }
    @Deprecated(forRemoval = true)
    public void setNoteTheme(Color noteTheme) {
        this.labelReleased = noteTheme;
    }
    
    @Deprecated(forRemoval = true)
    public Color getPressedNoteTheme() {
        return getColorTheme(notePressed);
    }
    @Deprecated(forRemoval = true)
    public void setPressedNoteTheme(Color pressedNoteTheme) {
        this.notePressed = pressedNoteTheme;
    }

    @Deprecated(forRemoval = true)
    public Color getLabelTheme() {
        return getColorTheme(noteReleased);
    }
    @Deprecated(forRemoval = true)
    public void setLabelTheme(Color labelTheme) {
        this.noteReleased = labelTheme;
    }

    @Deprecated(forRemoval = true)
    public Color getNoteRingTheme() {
        return getColorTheme(noteRing);
    }
    @Deprecated(forRemoval = true)
    public void setNoteRingTheme(Color noteRingTheme) {
        this.noteRing = noteRingTheme;
    }


    protected Color getColorTheme(final Color theme) {
        return getTheme(theme, Color.BLACK);
    }

    protected <T> T getTheme(final T theme, final T def) {
        return (theme == null) ? def : theme;
    }

}