package com.cstav.genshinstrument.item;

import com.cstav.genshinstrument.GInstrumentMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.cstav.genshinstrument.networking.packet.instrument.util.InstrumentPacketUtil.sendOpenPacket;

@EventBusSubscriber(modid = GInstrumentMod.MODID, bus = Bus.MOD)
public class GIItems {
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GInstrumentMod.MODID);
    public static void register(final IEventBus bus) {
        ITEMS.register(bus);
    }

    public static final RegistryObject<Item>
        WINDSONG_LYRE = register("windsong_lyre", (id) ->
            new InstrumentItem(
                (player) -> sendOpenPacket(player, loc("windsong_lyre")),
                id
            )
        ),
        VINTAGE_LYRE = register("vintage_lyre", (id) ->
            new InstrumentItem(
                (player) -> sendOpenPacket(player, loc("vintage_lyre")),
                id
            )
        ),

        FLORAL_ZITHER = register("floral_zither", (id) ->
            new InstrumentItem(
                (player) -> sendOpenPacket(player, loc("floral_zither")),
                id
            )
        ),

        GLORIOUS_DRUM = register("glorious_drum", (id) ->
            new InstrumentItem(
                (player) -> sendOpenPacket(player, loc("glorious_drum")),
                id
            )
        ),

        NIGHTWIND_HORN = register("nightwind_horn", (id) ->
            new NightwindHornItem(
                (player) -> sendOpenPacket(player, loc("nightwind_horn")),
                id
            )
        )
    ;

    private static ResourceLocation loc(final String path) {
        return GInstrumentMod.loc(path);
    }


    private static RegistryObject<Item> register(final String name, final Function<ResourceKey<Item>, Item> itemSupplier) {
        return ITEMS.register(name, () -> itemSupplier.apply(
            ResourceKey.create(Registries.ITEM, GInstrumentMod.loc(name)))
        );
    }


    @SubscribeEvent
    public static void registerItemsToTab(final BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES))
            return;

        for (final RegistryObject<Item> itemObj : ITEMS.getEntries())
            event.accept(itemObj.get());
    }

}
