package com.cstav.genshinstrument.networking.packet.instrument.s2c;

import com.cstav.genshinstrument.client.gui.screen.instrument.InstrumentScreenRegistry;
import com.cstav.genshinstrument.networking.IModPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

/**
 * A S2C packet telling the target client
 * to open a specific instrument screen
 */
public class OpenInstrumentPacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;

    private final ResourceLocation instrumentType;
    public OpenInstrumentPacket(final ResourceLocation instrumentScreen) {
        this.instrumentType = instrumentScreen;
    }

    public OpenInstrumentPacket(FriendlyByteBuf buf) {
        instrumentType = buf.readResourceLocation();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(instrumentType);
    }


    @Override
    public void handle(final Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
            InstrumentScreenRegistry.setScreenByID(instrumentType)
        );
    }
}