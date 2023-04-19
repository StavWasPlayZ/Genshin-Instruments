package com.cstav.genshinstrument.client.gui.screens.instrument.drum;

import static com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteLabelSupplier.create;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.INoteLabel;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.client.keyMaps.KeyMappings.DrumKeys;

import net.minecraft.network.chat.Component;

public enum DrumNoteLabel implements INoteLabel {
	DON_KA(create(() -> (note) ->
		Component.translatable(dn(note).btnType.getTransKey())
	)),
	KEYBOARD_LAYOUT(create(() -> (note) -> {
		final DrumNoteButton dnb = dn(note);
		final DrumKeys keys = dnb.btnType.getKeys();

		return INoteLabel.upperComponent((dnb.isRight ? keys.right : keys.left).getDisplayName());
	})),
    NONE(NoteLabelSupplier.EMPTY);


    private final NoteLabelSupplier labelSupplier;
    private DrumNoteLabel(final NoteLabelSupplier supplier) {
        labelSupplier = supplier;
    }

	@Override
	public NoteLabelSupplier getLabelSupplier() {
        return labelSupplier;
	}
	@Override
	public INoteLabel[] getValues() {
        return DrumNoteLabel.values();
	}


	private static DrumNoteButton dn(final NoteButton btn) {
        return (DrumNoteButton)btn;
    }
}