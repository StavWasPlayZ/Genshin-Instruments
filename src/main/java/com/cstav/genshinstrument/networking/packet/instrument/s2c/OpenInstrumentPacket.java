package com.cstav.genshinstrument.networking.packet.instrument.s2c;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.gui.screen.instrument.drum.AratakisGreatAndGloriousDrumScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.floralzither.FloralZitherScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.nightwind_horn.windsonglyre.NightwindHornScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.vintagelyre.VintageLyreScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.windsonglyre.WindsongLyreScreen;
import com.cstav.genshinstrument.networking.IModPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.Map;
import java.util.function.Supplier;

/**
 * A S2C packet telling the target client
 * to open a specific instrument screen
 */
public class OpenInstrumentPacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;
    private static final Map<String, Supplier<Supplier<Screen>>> INSTRUMENT_MAP = Map.of(
        "windsong_lyre", () -> WindsongLyreScreen::new,
        "vintage_lyre", () -> VintageLyreScreen::new,
        "floral_zither", () -> FloralZitherScreen::new,
        "glorious_drum", () -> AratakisGreatAndGloriousDrumScreen::new,
        "nightwind_horn", () -> NightwindHornScreen::new
    );

    protected Map<String, Supplier<Supplier<Screen>>> getInstrumentMap() {
        return INSTRUMENT_MAP;
    }


    private final String instrumentType;
    public OpenInstrumentPacket(final String instrumentScreen) {
        this.instrumentType = instrumentScreen;
    }

    public OpenInstrumentPacket(FriendlyByteBuf buf) {
        instrumentType = buf.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(instrumentType);
    }


    @Override
    public void handle(final Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            try {
                Minecraft.getInstance().setScreen(
                    getInstrumentMap().get(instrumentType).get().get()
                );
            } catch (Exception e) {
                GInstrumentMod.LOGGER.error("Exception thrown trying to open an instrument screen " + instrumentType, e);
            }
        });
    }
}