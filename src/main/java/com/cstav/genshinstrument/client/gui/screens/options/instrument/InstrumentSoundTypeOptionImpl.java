package com.cstav.genshinstrument.client.gui.screens.options.instrument;

import java.util.List;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.networking.packets.lyre.InstrumentPacket;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.FrameWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * Because of OpenAL or whatever, surround sounds are bad.
 * This class makes {@link SoundOptionsScreen} have an option for the audio channel type.
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = Main.MODID, bus = Bus.FORGE)
public abstract class InstrumentSoundTypeOptionImpl {
    private static final String BTN_TRANS_KEY = "button.genshinstrument.audioChannels";

    private static AbstractWidget lowestWidget;
    private static CycleButton<InstrumentSoundType> instrumentSoundType;

    @SubscribeEvent
    public static void onScreenRendered(final ScreenEvent.Init.Post event) {
        final Screen screen = event.getScreen();
        if (!(screen instanceof SoundOptionsScreen))
            return;
            
        lowestWidget = getLowestWidget(event.getListenersList());
        int buttonWidth = lowestWidget.getWidth();
        if (buttonWidth < 200) //dunno
            buttonWidth *= 2;


        instrumentSoundType = CycleButton.<InstrumentSoundType>builder((soundType) ->
            Component.translatable(BTN_TRANS_KEY +"."+ soundType.toString().toLowerCase())
        )
            .withValues(InstrumentSoundType.values())
            //TODO: from configs
            .withInitialValue(InstrumentSoundType.MIXED)

            .withTooltip((soundType) -> Tooltip.create(switch (soundType) {
                case MIXED -> translatableArgs(BTN_TRANS_KEY+".mixed.tooltip", InstrumentPacket.MIXED_RANGE);
                case STEREO -> Component.translatable(BTN_TRANS_KEY+".stereo.tooltip");
                default -> Component.empty();
            }
            ))
            .create(0, 0, buttonWidth + 10, 20, Component.translatable(BTN_TRANS_KEY));
            
        
        FrameWidget.alignInRectangle(instrumentSoundType,
            0, 0,
            screen.width, screen.height, .5f, 0);
        event.addListener(instrumentSoundType);
    }
    @SubscribeEvent
    public static void onRender(final ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof SoundOptionsScreen))
            return;
        
        instrumentSoundType.setY(
            (
                (lowestWidget == null) ? 32 :
                (lowestWidget.getY() + lowestWidget.getHeight())
            ) + 10
        );
    }

    /**
     * Tooltip is being annoying and not rpelacing my args.
     * So, fine, I'll do it myself.
     * @param key The translation key
     * @param arg The thing to replace with %s
     * @return What should've been return by {@link Component#translatable(String, Object...)}
     */
    private static MutableComponent translatableArgs(final String key, final Object arg) {
        return Component.literal(
            Component.translatable(key).getString().replace("%s", arg.toString())
        );
    }

    private static AbstractWidget getLowestWidget(final List<? extends GuiEventListener> children) {
        for (int i = children.size() - 1; i >= 0; i--) {
            final AbstractWidget result = getLowestWidget(children.get(i));

            if (result != null)
                return result;
        }

        return null;
    }
    private static AbstractWidget getLowestWidget(final GuiEventListener listener) {
        if (listener instanceof ContainerEventHandler)
            return getLowestWidget(((ContainerEventHandler)listener).children());
        if (listener instanceof AbstractWidget) {
            final AbstractWidget widget = (AbstractWidget)listener;
            // Exclude the done component
            if (widget.getMessage() != CommonComponents.GUI_DONE)
                return widget;
        }

        return null;
    }

}
