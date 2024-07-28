package com.cstav.genshinstrument.sound.held;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

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
     * @return The initiator ID for the provided object.
     */
    public static InitiatorID fromObj(@NotNull Object initiator) {
        return (initiator instanceof Entity entity)
            ? new InitiatorID("entity", String.valueOf(entity.getId()))
            : new InitiatorID("other", initiator.toString());
    }


    public static InitiatorID readFromNetwork(final FriendlyByteBuf buf) {
        final String[] initId = buf.readUtf(MAX_UTF_LEN).split(":");
        return new InitiatorID(initId[0], initId[1]);
    }

    public void writeToNetwork(final FriendlyByteBuf buf) {
        buf.writeUtf(toString());
    }


    public String toString() {
        return String.format("%s:%s", type, identifier);
    }

}
