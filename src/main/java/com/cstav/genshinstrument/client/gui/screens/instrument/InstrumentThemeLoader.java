package com.cstav.genshinstrument.client.gui.screens.instrument;

import java.io.IOException;
import java.util.ArrayList;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.util.RGBColor;
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

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Main.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class InstrumentThemeLoader {
    private static final ArrayList<InstrumentThemeLoader> LOADERS = new ArrayList<>();

    private final ResourceLocation lyreStyleLocation;
    private final RGBColor defNoteTheme, defPressedNoteTheme;
    private RGBColor noteTheme, pressedNoteTheme;
    private LyreThemeLoadedEvent onThemeChanged;
    
    public InstrumentThemeLoader(ResourceLocation lyreStyleLocation, RGBColor defNoteTheme, RGBColor defPressedNoteTheme) {
        this.lyreStyleLocation = lyreStyleLocation;
        noteTheme = this.defNoteTheme = defNoteTheme;
        pressedNoteTheme = this.defPressedNoteTheme = defPressedNoteTheme;

        LOADERS.add(this);
    }


    @SubscribeEvent
    public static void reloadLyreTheme(final RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new ResourceManagerReloadListener() {

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                for (final InstrumentThemeLoader lyreLoader : LOADERS) {
                    JsonObject style;
                    try {
                        style = JsonParser.parseReader(
                            resourceManager.getResource(lyreLoader.getLyreStyleLocation()).get().openAsReader()
                        ).getAsJsonObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }

                    lyreLoader.noteTheme = getTheme(style.get("note_theme"),
                        lyreLoader.defNoteTheme);
                    lyreLoader.pressedNoteTheme = getTheme(style.get("note_pressed_theme"),
                        lyreLoader.defPressedNoteTheme);

                    if (lyreLoader.onThemeChanged != null)
                        lyreLoader.onThemeChanged.run(lyreLoader.noteTheme, lyreLoader.pressedNoteTheme);
                }
            }
            private static RGBColor getTheme(final JsonElement rgbArray, final RGBColor def) {
                try {
                    final JsonArray rgb = rgbArray.getAsJsonArray();
                    return new RGBColor(
                        rgb.get(0).getAsInt(), rgb.get(1).getAsInt(), rgb.get(2).getAsInt()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    return def;
                }
            }
            
        });
    }


    public RGBColor getDefNoteTheme() {
        return defNoteTheme;
    }
    public RGBColor getDefPressedNoteTheme() {
        return defPressedNoteTheme;
    }
    public ResourceLocation getLyreStyleLocation() {
        return lyreStyleLocation;
    }

    public RGBColor getNoteTheme() {
        return noteTheme;
    }
    public void setNoteTheme(RGBColor noteTheme) {
        onThemeChanged.run(this.noteTheme = noteTheme, pressedNoteTheme);
    }
    
    public RGBColor getPressedNoteTheme() {
        return pressedNoteTheme;
    }
    public void setPressedNoteTheme(RGBColor pressedNoteTheme) {
        onThemeChanged.run(noteTheme, this.pressedNoteTheme = pressedNoteTheme);
    }

    public void setOnThemeChanged(LyreThemeLoadedEvent onThemeChanged) {
        this.onThemeChanged = onThemeChanged;
    }


    @FunctionalInterface
    public static interface LyreThemeLoadedEvent {
        void run(final RGBColor noteTheme, final RGBColor pressedNoteTheme);
    }

}
