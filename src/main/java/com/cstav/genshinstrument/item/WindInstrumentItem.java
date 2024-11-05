package com.cstav.genshinstrument.item;

import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.ModArmPose;
import com.cstav.genshinstrument.networking.OpenInstrumentPacketSender;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class WindInstrumentItem extends InstrumentItem {

    public WindInstrumentItem(OpenInstrumentPacketSender onOpenRequest, ResourceKey<Item> id) {
        super(onOpenRequest, id);
    }
    public WindInstrumentItem(OpenInstrumentPacketSender onOpenRequest, Properties properties) {
        super(onOpenRequest, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {

            @Override
            public @Nullable HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
            return (
                (entityLiving instanceof Player player)
                ? (InstrumentOpenProvider.isOpen(player) && InstrumentOpenProvider.isItem(player))
                    ? ModArmPose.PLAYING_WIND_INSTRUMENT
                    : null
                : null
            );
            }

        });
    }

}
