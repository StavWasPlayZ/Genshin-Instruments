package com.cstav.genshinstrument.networking.buttonidentifier;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.NoteGridButton;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NoteGridButtonIdentifier extends NoteButtonIdentifier {

    private int row, column;
    @OnlyIn(Dist.CLIENT)
    public NoteGridButtonIdentifier(final NoteGridButton button) {
        super(button);
        this.row = button.row;
        this.column = button.column;
    }

    public NoteGridButtonIdentifier(FriendlyByteBuf buf) {
        super(buf);
        row = buf.readInt();
        column = buf.readInt();
    }
    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        super.writeToNetwork(buf);
        buf.writeInt(row);
        buf.writeInt(column);
    }


    @Override
    public boolean matches(NoteButtonIdentifier other) {
        return MatchType.forceMatch(other, this::gridMatch);
    }
    private boolean gridMatch(final NoteGridButtonIdentifier other) {
        return (row == other.row) && (column == other.column);
    }

}