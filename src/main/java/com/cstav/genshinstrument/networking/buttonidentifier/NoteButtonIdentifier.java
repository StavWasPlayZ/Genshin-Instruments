package com.cstav.genshinstrument.networking.buttonidentifier;

import java.util.function.Function;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.mojang.logging.LogUtils;

import net.minecraft.network.FriendlyByteBuf;

/**
 * <p>
 * A class used for identifying {@link NoteButton note buttons} over network.
 * </p>
 * All implementors must include a constructor that gets a type {@link FriendlyByteBuf}.
 */
public abstract class NoteButtonIdentifier {
    
    public void writeToNetwork(final FriendlyByteBuf buf) {
        buf.writeUtf(getClass().getName());
    }

    public abstract boolean matches(NoteButtonIdentifier other);


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



    /**
     * A class holding methods to simplify the usage of the {@link NoteButtonIdentifier#matches matches} function
     */
    public static abstract class MatchType {
        /**
         * <p>Executes the match methods such that if the current {@code matchFunction} returned {@code false},
         * the {@code unmatchFunction} will execute in its stead.</p>
         * If the type of {@code other} and {@code T} do not match, then {@code unmatchFunction} will be executed.
         * @param <T> The type of the identifier to expect
         * @param other
         * @param matchFunction The function for when the type is as expected
         * @param unmatchFunction The function for when the type is unexpected (generic, {@link NoteButtonIdentifier})
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
        /**
         * <p>Executes the match methods such that only that if {@code other} did not match the type of {@code matchFunction},
         * the {@code unmatchFunction} will execute in its stead.</p>
         * @param <T> The type of the identifier to expect
         * @param other
         * @param matchFunction The function for when the type is as expected
         * @param unmatchFunction The function for when the type is unexpected (generic, {@link NoteButtonIdentifier})
         * @return The result of the identification process
         */
        @SuppressWarnings("unchecked")
        public static <T extends NoteButtonIdentifier> boolean perferMatch(NoteButtonIdentifier other,
                Function<T, Boolean> matchFunction, Function<NoteButtonIdentifier, Boolean> unmatchFunction) {
                    
            try {
                return matchFunction.apply((T)other);
            } catch (ClassCastException e) {
                return unmatchFunction.apply(other);
            }
        }
        
        /**
         * Executes the given match method such that if the expected type does not match {@code other},
         * {@code false} will be returned.
         * @param <T> The type of the identifier to expect
         * @param other
         * @param matchFunction The function for when the type is as expected
         * @return The result of the identification process, or {@code false} if the expected type does not match
         * 
         * 
         * @apiNote It is generally recommended not to use this match method,
         * because a lower type of identifier may be triggered as a result of 3rd-party initiation of notes.
         * 
         * Implementors should instead consider using any of the other types
         * with {@link DefaultNoteButtonIdentifier the default identifier} as their basis.
         */
        @SuppressWarnings("unchecked")
        public static <T extends NoteButtonIdentifier> boolean forceMatch(NoteButtonIdentifier other,
                Function<T, Boolean> matchFunction) {
                    
            try {
                return matchFunction.apply((T)other);
            } catch (ClassCastException e) {
                return false;
            }
        }
    }

}
