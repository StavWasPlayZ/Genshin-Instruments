package com.cstav.genshinstrument.client.gui.screen.instrument.ukelele;

import com.cstav.genshinstrument.client.config.enumType.NoteGridLabel;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid.GridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButtonRenderer;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteNotation;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.grid.NoteGridButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.label.NoteLabelSupplier;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class UkuleleNoteButton extends NoteGridButton {
    private static final List<NoteLabelSupplier>
        NOTATIONAL_LABELS = Stream.of(
            NoteGridLabel.NOTE_NAME,
            NoteGridLabel.DO_RE_MI,
            NoteGridLabel.FIXED_ABC,
            NoteGridLabel.FIXED_DO_RE_MI
        ).map(NoteGridLabel::getLabelSupplier).toList(),
        //TODO: This should later be determined via a simple boolean.
        FIXED_LABELS = Stream.of(
            NoteGridLabel.FIXED_ABC,
            NoteGridLabel.FIXED_DO_RE_MI
        ).map(NoteGridLabel::getLabelSupplier).toList()
    ;

    public UkuleleNoteButton(int row, int column, GridInstrumentScreen instrumentScreen) {
        super(row, column, instrumentScreen);
    }

    @Override
    protected NoteButtonRenderer initNoteRenderer() {
        return new UkuleleNoteButtonRenderer(this, this::getLabelTexture);
    }

    @Override
    public NoteNotation getNotation() {
        if (column == 0)
            return NoteNotation.NONE;
        return super.getNotation();
    }

    @Override
    public @NotNull Component getMessage() {
        // Change the top row if it is of a notational label type.
        // (As defined above.)
        if (column == 0) {
            if (NOTATIONAL_LABELS.contains(getLabelSupplier())) {
                return Component.literal(getChordNameOfRow());
            }
        }

        return super.getMessage();
    }

    public String getChordNameOfRow() {
        return getNoteName();
    }
}
