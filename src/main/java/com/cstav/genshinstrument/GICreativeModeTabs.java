package com.cstav.genshinstrument;

import com.cstav.genshinstrument.item.ModItems;

import com.cstav.genshinstrument.item.GIItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class GICreativeModeTabs {

    public static final CreativeModeTab instrumentsTab = new CreativeModeTab("genshinstrument.instruments") {

        @Override
        public ItemStack makeIcon() {
            return new ItemStack(GIItems.FLORAL_ZITHER.get());
        }

    };

}
