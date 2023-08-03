package com.cstav.genshinstrument;

import com.cstav.genshinstrument.item.ModItems;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeModeTabs {

    public static final CreativeModeTab instrumentsTab = (new CreativeModeTab("genshinstrument.instruments") {

        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.FLORAL_ZITHER.get());
        }

    });

}
