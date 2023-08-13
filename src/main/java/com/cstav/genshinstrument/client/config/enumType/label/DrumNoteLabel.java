package com.cstav.genshinstrument.client.config.enumType.label;

import com.cstav.genshinstrument.client.gui.screens.instrument.drum.DrumNoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.INoteLabel;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings.DrumKeys;

import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum DrumNoteLabel implements INoteLabel {
	KEYBOARD_LAYOUT((note) -> {
		final DrumNoteButton dnb = dn(note);
		final DrumKeys keys = dnb.btnType.getKeys();

		return INoteLabel.upperComponent((dnb.isRight ? keys.right : keys.left).getDisplayName());
	}),
	DON_KA((note) ->
		Component.translatable(dn(note).btnType.getTransKey())
	),
	NOTE_NAME((note) -> Component.literal(
		note.getCutNoteName()
	)),
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