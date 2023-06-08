package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note;

import java.awt.Point;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.ClientUtil;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.animation.RingAnimationController;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoteRing {
    public static final String RING_GLOB_FILENAME = "ring.png";

    protected final RingAnimationController ringAnimation = new RingAnimationController(.3f, 40, this);

    public final NoteButton note;
    public int size;
    public float alpha;

    public NoteRing(final NoteButton note) {
        this.note = note;
        ringAnimation.play();
    }
    
    
    public void render(final GuiGraphics gui) {
        if (!ringAnimation.isPlaying())
            return;

        ringAnimation.update();

        RenderSystem.setShaderColor(
            note.colorTheme.getRed() / 255f,
            note.colorTheme.getGreen() / 255f,
            note.colorTheme.getBlue() / 255f,
            alpha
        );


        final Point ringCenter = ClientUtil.getInitCenter(note.getInitX(), note.getInitY(), NoteButton.getSize(), size);
        gui.blit(
            new ResourceLocation(Main.MODID, AbstractInstrumentScreen.getGlobalRootPath() + RING_GLOB_FILENAME),
            ringCenter.x, ringCenter.y,
            0, 0,
            size, size,
            size, size
        );

        // Reset render state
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    public boolean isPlaying() {
        return ringAnimation.isPlaying();
    }

}
