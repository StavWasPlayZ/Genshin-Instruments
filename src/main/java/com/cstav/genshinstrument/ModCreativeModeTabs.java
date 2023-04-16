package com.cstav.genshinstrument;

import com.cstav.genshinstrument.item.ModItems;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = Main.MODID, bus = Bus.MOD)
public class ModCreativeModeTabs {
    
    private static CreativeModeTab instrumentsTab;
    public static CreativeModeTab getInstrumentsTab() {
        return instrumentsTab;
    }

    @SubscribeEvent
    public static void registerCreativeModeTabs(final CreativeModeTabEvent.Register event) {
        
        instrumentsTab = event.registerCreativeModeTab(new ResourceLocation(Main.MODID, "instruments_tab"), (builder) -> builder
                .title(Component.translatable("genshinstrument.itemGroup.instruments"))
                .icon(() -> new ItemStack(ModItems.FLORAL_ZITHER.get()))
                .noScrollBar()
                
                .displayItems((features, output, opTabEnabled) ->
                    ModItems.ITEMS.getEntries().forEach((item) ->
                        output.accept(item.get())
                    )
                )
        );

    }

}
