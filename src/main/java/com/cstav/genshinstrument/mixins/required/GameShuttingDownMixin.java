package com.cstav.genshinstrument.mixins.required;

import com.cstav.genshinstrument.event.GameShuttingDownEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class GameShuttingDownMixin {

    @Inject(at = @At("TAIL"), method = "close")
    public void closeInjector(final CallbackInfo info) {
        MinecraftForge.EVENT_BUS.post(new GameShuttingDownEvent());
    }

}
