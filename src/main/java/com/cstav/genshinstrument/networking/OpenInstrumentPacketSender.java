package com.cstav.genshinstrument.networking;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

@FunctionalInterface
public interface OpenInstrumentPacketSender {
    void send(final ServerPlayer player, final InteractionHand hand);
}