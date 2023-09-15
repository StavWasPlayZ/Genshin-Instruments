package com.cstav.genshinstrument.util;

import static java.util.Map.entry;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.label.INoteLabel;
import com.mojang.logging.LogUtils;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class LabelUtil {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public static final String[]
        DO_RE_MI = {
            "do", "re", "mi", "fa", "so", "la", "ti"
        }
    ;
    public static final char[]
        ABC = {
            'C', 'D', 'E', 'F', 'G', 'A', 'B'
        }
    ;

    private static final HashMap<Character, String> ABC_TO_DO_RE_MI = new HashMap<>();
    static {
        for (int i = 0; i < ABC.length; i++)
            ABC_TO_DO_RE_MI.put(ABC[i], DO_RE_MI[i]);
    }


    // Pitch system implementation
    // Literally could not have been done w/o Specy's instrument app, oh my gosh their method is genius
    // Either that or I'm just too much of a novice on music theory

    /**
     * @implNote Scales map taken from Specy's
     * <a href=https://github.com/Specy/genshin-music/blob/19dfe0e2fb8081508bd61dd47289dcb2d89ad5e3/src/Config.ts#L89>
     * Genshin Music app configs
     * </a>
     */
    public static final Map<String, String[]> NOTE_SCALES = Map.ofEntries(
        entry("Cb", strArr("Cb", "Dbb", "Db", "Ebb", "Eb", "Fb", "Gbb", "Gb", "Abb", "Ab", "Bbb", "Bb")),
        entry("C", strArr("C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B")),
        entry("C#", strArr("C#", "D", "D#", "E", "E#", "F#", "G", "G#", "A", "A#", "B", "B#")),
        entry("Db", strArr("Db", "Ebb", "Eb", "Fb", "F", "Gb", "Abb", "Ab", "Bbb", "Bb", "Cb", "C")),
        entry("D", strArr("D", "Eb", "E", "F", "F#", "G", "Ab", "A", "Bb", "B", "C", "C#")),
        entry("D#", strArr("D#", "E", "E#", "F#", "F##", "G#", "A", "A#", "B", "B#", "C#", "C##")),
        entry("Eb", strArr("Eb", "Fb", "F", "Gb", "G", "Ab", "Bbb", "Bb", "Cb", "C", "Db", "D")),
        entry("E", strArr("E", "F", "F#", "G", "G#", "A", "Bb", "B", "C", "C#", "D", "D#")),
        entry("E#", strArr("E#", "F#", "F##", "G#", "G##", "A#", "B", "B#", "C#", "C##", "D#", "D##")),
        entry("Fb", strArr("Fb", "Gbb", "Gb", "Abb", "Ab", "Bbb", "Cbb", "Cb", "Dbb", "Db", "Ebb", "Eb")),
        entry("F", strArr("F", "Gb", "G", "Ab", "A", "Bb", "Cb", "C", "Db", "D", "Eb", "E")),
        entry("F#", strArr("F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "E#")),
        entry("Gb", strArr("Gb", "Abb", "Ab", "Bbb", "Bb", "Cb", "Dbb", "Db", "Ebb", "Eb", "Fb", "F")),
        entry("G", strArr("G", "Ab", "A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "F#")),
        entry("G#", strArr("G#", "A", "A#", "B", "B#", "C#", "D", "D#", "E", "E#", "F#", "F##")),
        entry("Ab", strArr("Ab", "Bbb", "Bb", "Cb", "C", "Db", "Ebb", "Eb", "Fb", "F", "Gb", "G")),
        entry("A", strArr("A", "Bb", "B", "C", "C#", "D", "Eb", "E", "F", "F#", "G", "G#")),
        entry("A#", strArr("A#", "B", "B#", "C#", "C##", "D#", "E", "E#", "F#", "F##", "G#", "G##")),
        entry("Bb", strArr("Bb", "Cb", "C", "Db", "D", "Eb", "Fb", "F", "Gb", "G", "Ab", "A")),
        entry("B", strArr("B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#")),
        entry("B#", strArr("B#", "C#", "C##", "D#", "D##", "E#", "F#", "F##", "G#", "G##", "A#", "A##"))
    );
    public static final int NOTES_PER_SCALE = NOTE_SCALES.get("C").length;

    private static String[] strArr(final String... arr) {
        return arr;
    }
    

    public static String getNoteName(final int pitch, final String[] noteLayout, final int offset) {
        final String baseNote = noteLayout[CommonUtil.wrapAround(offset, noteLayout.length)];

        final String[] scale = NOTE_SCALES.get(baseNote);
        return scale[(CommonUtil.doublyPyWrap(pitch, scale.length))];
    }

    /**
     * @param omitIfAcuurate If the {@link ModClientConfigs#ACCURATE_NOTES} setting is enabled,
     * get the natural version of the note only
     * @return The given note, replaced with accurate accidentals unicodes
     */
    public static String formatNoteName(final String noteName, final boolean omitIfAccurate) {
        if (noteName.isEmpty())
            return "";
            
        String result = String.valueOf(noteName.charAt(0));
        if (!(omitIfAccurate && ModClientConfigs.ACCURATE_NOTES.get()))
            result += noteName.substring(1)
                .replaceAll("##", "\u00D7")
                .replaceAll("#", "♯")
                .replaceAll("b", "\u266D");

        return result;
    }

    public static Component toDoReMi(final String noteName) {
        if (noteName.isEmpty()) {
            LOGGER.warn("Cannot convert empty note to Do Re Mi!");
            return Component.empty();
        }

        return Component.translatable(INoteLabel.TRANSLATABLE_PATH + ABC_TO_DO_RE_MI.get(noteName.charAt(0)))
            .append(noteName.substring(1));
    }


    public static int getABCOffset(final NoteButton noteButton) {
        final ResourceLocation instrumentId = noteButton.instrumentScreen.getInstrumentId();
        final String noteName = noteButton.getNoteName();

        if (noteName.isEmpty()) {
            LOGGER.warn("Cannot get ABC offset for an instrument without a note layout! ("+instrumentId+")");
            return 0;
        }

        final char note = noteName.charAt(0);

        for (int i = 0; i < ABC.length; i++)
            if (note == ABC[i])
                return i;

        LOGGER.warn("Could not get note "+note+" for instrument "+noteButton.instrumentScreen.getInstrumentId()+"!");
        return 0;
    }

}