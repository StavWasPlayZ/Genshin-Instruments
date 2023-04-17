package com.cstav.genshinstrument.client.gui.screens.instrument.drum;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteLabel;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.client.keyMaps.KeyMappings.DrumKeys;

import net.minecraft.network.chat.Component;

public enum DrumNoteLabel implements NoteLabel {
	DON_KA((note) -> Component.translatable(dn(note).btnType.getTransKey())),
	KEYBOARD_LAYOUT((note) -> {
        final DrumNoteButton dnb = dn(note);
        final DrumKeys keys = dnb.btnType.getKeys();

        return NoteLabel.upperComponent((dnb.isRight ? keys.right : keys.left).getDisplayName());
    });


    private final NoteLabelSupplier labelSupplier;
    private DrumNoteLabel(final NoteLabelSupplier supplier) {
        labelSupplier = supplier;
    }

	@Override
	public NoteLabelSupplier getLabelSupplier() {
        return labelSupplier;
	}
	@Override
	public NoteLabel[] getValues() {
        return DrumNoteLabel.values();
	}


    private static DrumNoteButton dn(final NoteButton btn) {
        return (DrumNoteButton)btn;
    }
    
}