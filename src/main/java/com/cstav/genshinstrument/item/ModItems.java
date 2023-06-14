package com.cstav.genshinstrument.item;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.sounds.ModSounds;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);
    public static void register(final IEventBus bus) {
        ITEMS.register(bus);
    }

    public static final RegistryObject<Item>
        WINDSONG_LYRE = ITEMS.register("windsong_lyre", () ->
            new InstrumentItem(
                (player) -> InstrumentItem.sendOpenRequest(player, "windsong_lyre"),
                ModSounds.WINDSONG_LYRE_NOTE_SOUNDS
            )
        ),
        VINTAGE_LYRE = ITEMS.register("vintage_lyre", () ->
            new InstrumentItem(
                (player) -> InstrumentItem.sendOpenRequest(player, "vintage_lyre"),
                ModSounds.VINTAGE_LYRE_NOTE_SOUNDS
            )
        ),

        FLORAL_ZITHER = ITEMS.register("floral_zither", () ->
            new InstrumentItem(
                (player) -> InstrumentItem.sendOpenRequest(player, "floral_zither"),
                ModSounds.ZITHER_NEW_NOTE_SOUNDS, ModSounds.ZITHER_OLD_NOTE_SOUNDS
            )
        )
    ;

}
