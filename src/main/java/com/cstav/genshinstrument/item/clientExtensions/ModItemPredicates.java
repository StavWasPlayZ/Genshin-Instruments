package com.cstav.genshinstrument.item.clientExtensions;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModItemPredicates {

    public static void register() {
        ItemProperties.registerGeneric(new ResourceLocation(GInstrumentMod.MODID, "instrument_open"),
            ModItemPredicates::instrumentOpenPredicate
        );
    }

    /**
     * Derives {@link InstrumentOpen} capability as item model predicate
     */
    public static float instrumentOpenPredicate(ItemStack pStack, ClientLevel pLevel, LivingEntity pEntity, int pSeed) {
        if (!(pEntity instanceof Player player))
            return 0;

        final InstrumentScreen screen = InstrumentScreen.getCurrentScreen().orElse(null);
        if (screen == null)
            return 0;

        final InteractionHand hand = screen.interactionHand.orElse(null);
        // If the hand is empty, this is not an item instrument.
        if (hand == null)
            return 0;

        return ItemStack.matches(pStack, player.getItemInHand(hand)) ? 1 : 0;
    }

}
