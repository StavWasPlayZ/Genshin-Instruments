package com.cstav.genshinstrument.item;

import com.cstav.genshinstrument.Main;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(modid = Main.MODID, bus = Bus.MOD)
public class ModItems {
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);
    public static void register(final IEventBus bus) {
        ITEMS.register(bus);
    }

    public static final RegistryObject<Item>
        LYRE = ITEMS.register("windsong_lyre", () ->
            new InstrumentItem((player) -> InstrumentItem.sendOpenRequest(player, "lyre"))
        ),
        VINTAGE_LYRE = ITEMS.register("vintage_lyre", () ->
            new InstrumentItem((player) -> InstrumentItem.sendOpenRequest(player, "vintage_lyre"))
        ),
        ZITHER = ITEMS.register("floral_zither", () ->
            new InstrumentItem((player) -> InstrumentItem.sendOpenRequest(player, "zither"))
        )
    ;


    @SubscribeEvent
    public static void registerItemsToTab(final CreativeModeTabEvent.BuildContents event) {
        if (!event.getTab().equals(CreativeModeTabs.TOOLS_AND_UTILITIES))
            return;

        for (final RegistryObject<Item> itemObj : ITEMS.getEntries())
            event.accept(itemObj.get());
    }

}
