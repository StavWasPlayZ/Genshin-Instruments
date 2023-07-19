package com.cstav.genshinstrument.networking.packet.instrument;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.cstav.genshinstrument.client.gui.screens.instrument.drum.AratakisGreatAndGloriousDrumScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.floralzither.FloralZitherScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.vintagelyre.VintageLyreScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.windsonglyre.WindsongLyreScreen;
import com.cstav.genshinstrument.networking.ModPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class OpenInstrumentPacket implements ModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;
    private static final Map<String, Supplier<Function<InteractionHand, AbstractInstrumentScreen>>> OPEN_INSTRUMENT = Map.of(
        "windsong_lyre", () -> WindsongLyreScreen::new,
        "vintage_lyre", () -> VintageLyreScreen::new,
        "floral_zither", () -> FloralZitherScreen::new,
        "glorious_drum", () -> AratakisGreatAndGloriousDrumScreen::new
    );


    private final String instrumentType;
    private final InteractionHand hand;
    public OpenInstrumentPacket(final String instrumentScreen, final InteractionHand hand) {
        this.instrumentType = instrumentScreen;
        this.hand = hand;
    }

    public OpenInstrumentPacket(FriendlyByteBuf buf) {
        instrumentType = buf.readUtf();
        hand = buf.readEnum(InteractionHand.class);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(instrumentType);
        buf.writeEnum(hand);
    }


    @Override
    public boolean handle(final Supplier<Context> supplier) {
        final Context context = supplier.get();

        context.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                Minecraft.getInstance().setScreen(OPEN_INSTRUMENT.get(instrumentType).get().apply(hand)));
        });

        return true;
    }
}