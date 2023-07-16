package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note;

import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.sound.NoteSound;
import com.mojang.logging.LogUtils;

import net.minecraft.network.FriendlyByteBuf;

/**
 * <p>
 * A class used for identifying {@link NoteButton note buttons} over network.
 * By default, uses a button's {@link NoteSound} as an identifier
 * </p>
 * All implementors must include a constructor that gets type {@link FriendlyByteBuf}.
 */
public class NoteButtonIdentifier {
    
    private NoteSound sound;
    public NoteButtonIdentifier(final NoteSound sound) {
        this.sound = sound;
    }

    public NoteButtonIdentifier(final FriendlyByteBuf buf) {
        sound = NoteSound.readFromNetwork(buf);
    }
    public void writeToNetwork(final FriendlyByteBuf buf) {
        buf.writeUtf(getClass().getName());
        sound.writeToNetwork(buf);
    }


    public boolean matches(NoteButtonIdentifier other) {
        return sound == other.sound;
    }

    

    @Override
    public boolean equals(Object other) {
        if (other instanceof NoteButtonIdentifier)
            return matches((NoteButtonIdentifier)other);
        return false;
    }


    public static NoteButtonIdentifier readIdentifier(FriendlyByteBuf buf) {
        try {
            return ModPacketHandler.getValidIdentifier(buf.readUtf())
                .getDeclaredConstructor(FriendlyByteBuf.class).newInstance(buf);
        } catch (Exception e) {
            LogUtils.getLogger().error("Error initializing button identifier", e);
            return null;
        }
    }

}
