package com.cstav.genshinstrument.networking.buttonidentifiers;

import java.util.function.Function;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.sound.NoteSound;
import com.mojang.logging.LogUtils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    @OnlyIn(Dist.CLIENT)
    public NoteButtonIdentifier(final NoteButton note) {
        this(note.sound);
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

    /**
     * <p>Executes match methods such that if the current {@code matchFunction} returned {@code false},
     * the {@code unmatchFunction} will execute in its stead.</p>
     * If the type of {@code other} and {@code T} do not match, then {@code unmatchFunction} will be executed.
     * @param <T> The type of the identifier to expect
     * @param other
     * @param matchFunction The function for when the type is as expected
     * @param unmatchFunction The function for when the type is as unexpected (generic, {@link NoteButtonIdentifier})
     * @return The result of the identification process
     */
    @SuppressWarnings("unchecked")
    public static <T extends NoteButtonIdentifier> boolean hierarchyMatch(NoteButtonIdentifier other,
            Function<T, Boolean> matchFunction, Function<NoteButtonIdentifier, Boolean> unmatchFunction) {
                
        try {
            return matchFunction.apply((T)other) || unmatchFunction.apply(other);
        } catch (ClassCastException e) {
            return unmatchFunction.apply(other);
        }
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
