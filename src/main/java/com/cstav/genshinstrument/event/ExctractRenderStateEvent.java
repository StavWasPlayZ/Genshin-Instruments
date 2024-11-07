package com.cstav.genshinstrument.event;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;

/**
 * An event fired when render states are applied.<br/>
 * Fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS Forge event bus}.
 * @see ExctractRenderStateEvent.Pre
 * @see ExctractRenderStateEvent.Post
 */
public abstract class ExctractRenderStateEvent extends Event {
    public final EntityRenderer<?, ?> renderer;
    public final Entity entity;
    public final EntityRenderState state;
    public final float packedLight;

    public ExctractRenderStateEvent(EntityRenderer<?, ?> renderer, Entity entity, EntityRenderState state, float packedLight) {
        this.renderer = renderer;
        this.entity = entity;
        this.state = state;
        this.packedLight = packedLight;
    }

    /**
     * An event fired when render states are to be applied.
     * Fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS Forge event bus}.
     * @see ExctractRenderStateEvent.Pre
     * @see ExctractRenderStateEvent.Post
     */
    public static class Pre extends ExctractRenderStateEvent {
        public Pre(EntityRenderer<?, ?> renderer, Entity entity, EntityRenderState state, float packedLight) {
            super(renderer, entity, state, packedLight);
        }
    }
    /**
     * An event fired when render states have been applied.
     * You may modify existing render state fields here.<br/>
     * Fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS Forge event bus}.
     * @see ExctractRenderStateEvent.Pre
     * @see ExctractRenderStateEvent.Post
     */
    public static class Post extends ExctractRenderStateEvent {
        public Post(EntityRenderer<?, ?> renderer, Entity entity, EntityRenderState state, float packedLight) {
            super(renderer, entity, state, packedLight);
        }
    }
}
