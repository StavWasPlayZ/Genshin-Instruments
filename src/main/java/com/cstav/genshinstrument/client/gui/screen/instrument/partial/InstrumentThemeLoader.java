package com.cstav.genshinstrument.client.gui.screen.instrument.partial;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
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
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
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

    public final ResourceLocation resourcesRootDir;
    /**
     * The ID of the instrument using the resources.
     * Used for logging purposes.
     */
    public final ResourceLocation instrumentId;
    /**
     * The ID of the used instrument's resources
     */
    public final ResourceLocation subjectInstrumentId;

    private final boolean ignoreGlobal;

    private Color
        rawLabelPressed = Color.BLACK, rawLabelReleased = Color.BLACK,
        rawNotePressed = Color.BLACK, rawNoteReleased = Color.BLACK,
        rawNoteRing = Color.BLACK
    ;

    private final ArrayList<Consumer<JsonObject>> listeners = new ArrayList<>();
    
    /**
     * Initializes a new Instrument Theme Loader and subscribes it to the resource load event.
     * @param resourceRootDir The location of the root resources folder to derive styles from
     * @param instrumentId The ID of the instrument using the resources. Used for logging purposes.
     * @param subjectInstrumentId The ID of the used instrument's resources
     * @param ignoreGlobal When a global resource pack is enabled, defines whether this theme loader ignores it
     */
    public InstrumentThemeLoader(ResourceLocation resourceRootDir,
                                 ResourceLocation instrumentId,
                                 ResourceLocation subjectInstrumentId,
                                 boolean ignoreGlobal) {
        this.resourcesRootDir = resourceRootDir;
        this.instrumentId = instrumentId;
        this.subjectInstrumentId = subjectInstrumentId;
        this.ignoreGlobal = ignoreGlobal;

        LOADERS.add(this);
        addListener(this::loadColorTheme);
    }

    /**
     * Initializes a new Instrument Theme Loader and subscribes it to the resource load event.
     * @param resourceRootDir The location of the root resources folder to derive styles from
     * @param instrumentId The instrument ID
     * @param ignoreGlobal When a global resource pack is enabled, defines whether this theme loader ignores it
     */
    public InstrumentThemeLoader(ResourceLocation resourceRootDir,
                                 ResourceLocation instrumentId,
                                 boolean ignoreGlobal) {
        this(resourceRootDir, instrumentId, instrumentId, ignoreGlobal);
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

    public static InstrumentThemeLoader fromOther(InstrumentThemeLoader other, ResourceLocation instrumentId) {
        return new InstrumentThemeLoader(
            other.resourcesRootDir,
            instrumentId,
            other.subjectInstrumentId,
            other.ignoreGlobal
        );
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
            this::setRawNotePressed, this::setNoteReleased,
            DEF_PRESSED_THEME, Color.BLACK
        );
        setPressStatedTheme(theme, "label",
            this::setLabelPressed, this::setLabelReleased,
            rawNotePressed, Color.BLACK
        );

        setRawNoteRing(getTheme(theme, "note_ring", rawLabelReleased));
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
        setNoteRingTheme(getTheme(theme, "note_ring_theme", rawLabelReleased));

        setLabelPressed(rawNotePressed);
    }


    /**
     * @param propertyName The name of the RGB array representation within {@code theme}
     * @param def The default value of the theme
     * @return The theme as specified in the RGB array, or the default if 
     * any exception occurred.
     * 
     * @see InstrumentThemeLoader#tryGetProperty
     */
    public Color getTheme(JsonObject theme, String propertyName, Color def) {
        final JsonElement rgbArray = theme.get(propertyName);

        if (rgbArray == null || !rgbArray.isJsonArray())
            return def;

        return tryGetProperty(rgbArray.getAsJsonArray(), (rgb) -> new Color(
            rgb.get(0).getAsInt(), rgb.get(1).getAsInt(), rgb.get(2).getAsInt()
        ), def);
    }
    /**
     * @param <T> The type of property to return
     * @param <J> The type of the json element
     * @param getter The method for getting the desired element
     * @param def The default value of the theme
     * @return Either the value of the getter, or the given default if
     * any exception occurred.
     */
    protected <T, J extends JsonElement> T tryGetProperty(J element, Function<J, T> getter, T def) {
        try {
            return getter.apply(element);
        } catch (Exception e) {
            LOGGER.error("Error retrieving JSON property for "+instrumentId, e);
            return def;
        }
    }


    //#region File Reading

    @SubscribeEvent
    public static void registerReloadEvent(final RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) InstrumentThemeLoader::reload);
    }

    private static void reload(final ResourceManager resourceManager) {
        updateIsGlobalThemed(resourceManager);

        for (final InstrumentThemeLoader instrumentLoader : LOADERS)
            instrumentLoader.performReload(resourceManager);

        CACHES.clear();
    }

    private static void updateIsGlobalThemed(final ResourceManager resourceManager) {
        isGlobalThemed = false;
        final Optional<Resource> instrumentsMeta = resourceManager.getResource(INSTRUMENTS_META_LOC);

        if (instrumentsMeta.isEmpty()) {
            LOGGER.warn("No instrument meta found for " + INSTRUMENTS_META_LOC + "!");
            return;
        }

        try (final BufferedReader reader = instrumentsMeta.get().openAsReader()) {
            isGlobalThemed = JsonParser.parseReader(reader)
                .getAsJsonObject()
                .get("is_global_pack")
                .getAsBoolean();
        } catch (Exception e) {}

        if (isGlobalThemed)
            LOGGER.info("Instrument global themes enabled; loading all instrument resources from "+GLOBAL_LOC);
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

        } catch (Exception e) {
            LOGGER.error("Met an exception upon loading the instrument styler from "+styleLocation + logSuffix, e);
        }

        // Make sure styler exists
        final Optional<Resource> styler = resourceManager.getResource(styleLocation);
        if (styler.isEmpty()) {
            LOGGER.error("Could not retrieve styler information from "+styleLocation+"!");
            return;
        }

        try (final BufferedReader reader = styler.get().openAsReader()) {
            styleInfo = JsonParser.parseReader(reader).getAsJsonObject();

            // Call all load listeners on the current loader
            for (final Consumer<JsonObject> listener : listeners)
                listener.accept(styleInfo);

            CACHES.put(styleLocation, styleInfo);
        } catch (Exception e) {
            LOGGER.error("Met an exception upon loading the instrument styler from "+styleLocation + logSuffix, e);
        }

        LOGGER.info("Loaded and cached instrument style from "+styleLocation + logSuffix);
    }

    //#endregion


    public ResourceLocation getResourcesRootDir() {
        return resourcesRootDir;
    }

    public ResourceLocation getStylerLocation() {
        return ((!ignoreGlobal && isGlobalThemed) ? GLOBAL_LOC : getResourcesRootDir())
            .withSuffix("/"+JSON_STYLER_NAME);
    }


    public Color getRawLabelPressed() {
        return rawLabelPressed;
    }
    public Color getRawLabelReleased() {
        return rawLabelReleased;
    }
    public Color getRawNotePressed() {
        return rawNotePressed;
    }
    public Color getRawNoteReleased() {
        return rawNoteReleased;
    }
    public Color getRawNoteRing() {
        return rawNoteRing;
    }

    public Color labelPressed(final NoteButton noteButton) {
        return rawLabelPressed;
    }
    public void setLabelPressed(Color labelPressed) {
        this.rawLabelPressed = labelPressed;
    }

    public Color labelReleased(final NoteButton noteButton) {
        return rawLabelReleased;
    }
    public void setLabelReleased(Color labelReleased) {
        this.rawLabelReleased = labelReleased;
    }


    public Color notePressed(final NoteButton noteButton) {
        return rawNotePressed;
    }
    public void setRawNotePressed(Color rawNotePressed) {
        this.rawNotePressed = rawNotePressed;
    }

    public Color noteReleased(final NoteButton noteButton) {
        return rawNoteReleased;
    }
    public void setNoteReleased(Color noteReleased) {
        this.rawNoteReleased = noteReleased;
    }

    
    public Color noteRing(final NoteButton noteButton) {
        return rawNoteRing;
    }
    public void setRawNoteRing(Color noteRingTheme) {
        this.rawNoteRing = noteRingTheme;
    }


    
    /* --------- Legacy Styler Properties --------- */
    //#region TODO remove in v6.0

    @Deprecated(forRemoval = true)
    public Color getNoteTheme(final NoteButton noteButton) {
        return getColorTheme(noteButton, rawLabelReleased);
    }
    @Deprecated(forRemoval = true)
    public void setNoteTheme(Color noteTheme) {
        this.rawLabelReleased = noteTheme;
    }
    
    @Deprecated(forRemoval = true)
    public Color getPressedNoteTheme(final NoteButton noteButton) {
        return getColorTheme(noteButton, rawNotePressed);
    }
    @Deprecated(forRemoval = true)
    public void setPressedNoteTheme(Color pressedNoteTheme) {
        this.rawNotePressed = pressedNoteTheme;
    }

    @Deprecated(forRemoval = true)
    public Color getLabelTheme(final NoteButton noteButton) {
        return getColorTheme(noteButton, rawNoteReleased);
    }
    @Deprecated(forRemoval = true)
    public void setLabelTheme(Color labelTheme) {
        this.rawNoteReleased = labelTheme;
    }

    @Deprecated(forRemoval = true)
    public Color getNoteRingTheme(final NoteButton noteButton) {
        return getColorTheme(noteButton, rawNoteRing);
    }
    @Deprecated(forRemoval = true)
    public void setNoteRingTheme(Color noteRingTheme) {
        this.rawNoteRing = noteRingTheme;
    }

    @Deprecated(forRemoval = true)
    protected Color getColorTheme(final NoteButton noteButton, final Color theme) {
        return getTheme(theme, Color.BLACK);
    }

    @Deprecated(forRemoval = true)
    protected <T> T getTheme(final T theme, final T def) {
        return (theme == null) ? def : theme;
    }

    //#endregion

}