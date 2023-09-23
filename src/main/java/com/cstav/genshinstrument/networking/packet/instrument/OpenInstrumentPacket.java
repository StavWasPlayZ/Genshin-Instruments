package com.cstav.genshinstrument.networking.packet.instrument;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.cstav.genshinstrument.client.gui.screen.instrument.drum.AratakisGreatAndGloriousDrumScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.floralzither.FloralZitherScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.vintagelyre.VintageLyreScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.windsonglyre.WindsongLyreScreen;
import com.cstav.genshinstrument.networking.IModPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class OpenInstrumentPacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;
    private static final Map<String, Supplier<Function<InteractionHand, Screen>>> INSTRUMENT_MAP = Map.of(
        "windsong_lyre", () -> WindsongLyreScreen::new,
        "vintage_lyre", () -> VintageLyreScreen::new,
        "floral_zither", () -> FloralZitherScreen::new,
        "glorious_drum", () -> AratakisGreatAndGloriousDrumScreen::new
    );

    protected Map<String, Supplier<Function<InteractionHand, Screen>>> getInstrumentMap() {
        return INSTRUMENT_MAP;
    }


    private final String instrumentType;
    private final Optional<InteractionHand> hand;
    public OpenInstrumentPacket(final String instrumentScreen, final InteractionHand hand) {
        this.instrumentType = instrumentScreen;
        this.hand = Optional.ofNullable(hand);
    }

    public OpenInstrumentPacket(FriendlyByteBuf buf) {
        instrumentType = buf.readUtf();
        hand = buf.readOptional((fbb) -> fbb.readEnum(InteractionHand.class));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(instrumentType);
        buf.writeOptional(hand, FriendlyByteBuf::writeEnum);
    }


    @Override
    public void handle(final Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
            Minecraft.getInstance().setScreen(
                getInstrumentMap().get(instrumentType).get().apply(hand.orElse(null))
            )
        );
    }
}