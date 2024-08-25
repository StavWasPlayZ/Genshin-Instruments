package com.cstav.genshinstrument.item;

import com.cstav.genshinstrument.GInstrumentMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.cstav.genshinstrument.networking.packet.instrument.util.InstrumentPacketUtil.sendOpenPacket;

@EventBusSubscriber(modid = GInstrumentMod.MODID, bus = Bus.MOD)
public class GIItems {
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GInstrumentMod.MODID);
    public static void register(final IEventBus bus) {
        ITEMS.register(bus);
    }

    public static final RegistryObject<Item>
        WINDSONG_LYRE = ITEMS.register("windsong_lyre", () ->
            new InstrumentItem(
                (player) -> sendOpenPacket(player, loc("windsong_lyre"))
            )
        ),
        VINTAGE_LYRE = ITEMS.register("vintage_lyre", () ->
            new InstrumentItem(
                (player) -> sendOpenPacket(player, loc("vintage_lyre"))
            )
        ),

        FLORAL_ZITHER = ITEMS.register("floral_zither", () ->
            new InstrumentItem(
                (player) -> sendOpenPacket(player, loc("floral_zither"))
            )
        ),

        GLORIOUS_DRUM = ITEMS.register("glorious_drum", () ->
            new InstrumentItem(
                (player) -> sendOpenPacket(player, loc("glorious_drum"))
            )
        ),

        NIGHTWIND_HORN = ITEMS.register("nightwind_horn", () ->
            new NightwindHornItem(
                (player) -> sendOpenPacket(player, loc("nightwind_horn"))
            )
        )
    ;

    private static ResourceLocation loc(final String path) {
        return new ResourceLocation(GInstrumentMod.MODID, path);
    }

}
