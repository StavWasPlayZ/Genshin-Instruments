package com.cstav.genshinstrument.networking.packets.instrument;

import java.util.Map;
import java.util.function.Supplier;

import com.cstav.genshinstrument.client.gui.screens.instrument.floralZither.FloralZitherScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.vintageLyre.VintageLyreScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.windsongLyre.WindsongLyreScreen;
import com.cstav.genshinstrument.networking.ModPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class OpenInstrumentPacket implements ModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;
    private static final Map<String, Runnable> OPEN_INSTRUMENT = Map.of(
        "lyre", () -> WindsongLyreScreen.open(),
        "zither", () -> FloralZitherScreen.open(),
        "vintage_lyre", () -> VintageLyreScreen.open()
    );


    final String instrument;
    public OpenInstrumentPacket(final String instrument) {
        this.instrument = instrument;
    }
    public OpenInstrumentPacket(FriendlyByteBuf buf) {
        instrument = buf.readUtf();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(instrument);
    }


    @Override
    public boolean handle(final Supplier<Context> supplier) {
        final Context context = supplier.get();

        context.enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> OPEN_INSTRUMENT.get(instrument))
        );

        return true;
    }
}