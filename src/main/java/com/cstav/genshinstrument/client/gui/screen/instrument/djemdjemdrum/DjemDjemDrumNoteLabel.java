package com.cstav.genshinstrument.client.gui.screen.instrument.djemdjemdrum;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.label.INoteLabel;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.label.NoteLabelSupplier;
import com.cstav.genshinstrument.util.LabelUtil;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum DjemDjemDrumNoteLabel implements INoteLabel {
	//TODO: Add the labels
	KEYBOARD_LAYOUT((note) ->
		INoteLabel.upperComponent(ddd(note).getKey().getDisplayName())
	),
	QWERTY((note) ->
		INoteLabel.getQwerty(ddd(note).getKey())
	),
//
//	DON_KA((note) ->
//		Component.translatable(dn(note).btnType.getTransKey())
//	),
	NOTE_NAME((note) -> Component.literal(
		note.getFormattedNoteName()
	)),
	DO_RE_MI((note) ->
        LabelUtil.toDoReMi(note.getFormattedNoteName())
    ),

    NONE(NoteLabelSupplier.EMPTY);


    private final NoteLabelSupplier labelSupplier;
    private DjemDjemDrumNoteLabel(final NoteLabelSupplier supplier) {
        labelSupplier = supplier;
    }

	public static INoteLabel[] availableVals() {
        return INoteLabel.filterQwerty(values(), ModClientConfigs.DJEM_DJEM_DRUM_LABEL_TYPE.get(), QWERTY);
    }


	@Override
	public NoteLabelSupplier getLabelSupplier() {
        return labelSupplier;
	}


	private static DjemDjemDrumNoteButton ddd(final NoteButton btn) {
        return (DjemDjemDrumNoteButton)btn;
    }
}