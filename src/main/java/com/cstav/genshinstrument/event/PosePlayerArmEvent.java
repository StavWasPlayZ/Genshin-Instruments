package com.cstav.genshinstrument.event;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
@OnlyIn(Dist.CLIENT)
public class PosePlayerArmEvent extends Event {

    public final Player player;
    public final HandType hand;
    public final ModelPart arm;

    public PosePlayerArmEvent(final Player player, final HandType hand, final ModelPart arm) {
        this.player = player;
        this.hand = hand;
        this.arm = arm;
    }

    @OnlyIn(Dist.CLIENT)
    public enum HandType {
        LEFT, RIGHT
    }

}