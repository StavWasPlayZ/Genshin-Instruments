package com.cstav.genshinstrument;

import com.cstav.genshinstrument.item.ModItems;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = Main.MODID, bus = Bus.MOD)
public class ModCreativeModeTabs {

    public static final CreativeModeTab instrumentsTab = (new CreativeModeTab("genshinstrument.itemGroup.instruments") {

        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.FLORAL_ZITHER.get());
        }

    }).hideScroll();

}
