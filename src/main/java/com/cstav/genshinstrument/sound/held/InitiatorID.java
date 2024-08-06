package com.cstav.genshinstrument.sound.held;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * A tuple holding an initiator type and identifier.
 */
public record InitiatorID(String type, String identifier) {
    public static final int MAX_UTF_LEN = 65; // account for ":"

    public InitiatorID {
        if ((type.length() + identifier.length()) > MAX_UTF_LEN - 1) {
            throw new IllegalArgumentException("Initiator type and ID must not exceed length " + MAX_UTF_LEN);
        }
    }

    /**
     * @param id In the format of {@code type}:{@code identifier}
     */
    public InitiatorID(String id) {
        this(decompose(id));
    }
    private InitiatorID(String[] decomposed) {
        this(decomposed[0], decomposed[1]);
    }

    private static String[] decompose(final String id) {
        final String[] result = id.split(":");

        assert result.length == 2 : ("ID must be in the format of type:identifier - received " + id);
        return result;
    }


    /**
     * @param initiatorId The entity initiator's ID
     * @param oInitiatorId The custom initiator ID
     * @return The custom initiator ID if possible, otherwise
     * the entity's.
     */
    public static InitiatorID getEither(final Optional<Integer> initiatorId, final Optional<InitiatorID> oInitiatorId) {
        return oInitiatorId.orElseGet(() -> assertIIDPresent(
            initiatorId
                .map(Minecraft.getInstance().level::getEntity)
                .map(InitiatorID::fromEntity)
        ));
    }

    /**
     * Asserts that the initiator ID provided is present, and returns it.
     * Throws an {@link AssertionError} otherwise.
     */
    private static InitiatorID assertIIDPresent(Optional<InitiatorID> initiatorID) {
        assert initiatorID.isPresent() : "Must either have an entity initiator or an initiator ID!";
        return initiatorID.get();
    }


    /**
     * @return The initiator ID for the provided object.
     */
    public static InitiatorID fromObj(@NotNull Object initiator) {
        return (initiator instanceof Entity entity)
            ? fromEntity(entity)
            : new InitiatorID("other", initiator.toString());
    }
    public static InitiatorID fromEntity(@NotNull Entity entity) {
        return new InitiatorID("entity", String.valueOf(entity.getId()));
    }


    public static InitiatorID readFromNetwork(final FriendlyByteBuf buf) {
        return new InitiatorID(
            buf.readUtf(),
            buf.readUtf()
        );
    }

    public void writeToNetwork(final FriendlyByteBuf buf) {
        buf.writeUtf(type);
        buf.writeUtf(identifier);
    }


    public String toString() {
        return String.format("%s:%s", type, identifier);
    }

}
