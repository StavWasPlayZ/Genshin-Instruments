package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note;

import java.awt.Point;

import com.cstav.genshinstrument.Main;
import com.cstav.genshinstrument.client.ClientUtil;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.note.animation.RingAnimationController;

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

    public NoteRing(final NoteButton note, final boolean isForeign) {
        this.note = note;

        if (isForeign)
            ringAnimation.play(-.4f);
        else
            ringAnimation.play();
    }
    
    
    public void render(final GuiGraphics gui) {
        if (!ringAnimation.isPlaying())
            return;

        ringAnimation.update();

        final Point ringCenter = ClientUtil.getInitCenter(note.getInitX(), note.getInitY(), NoteButton.getSize(), size);


        ClientUtil.setShaderColor(note.instrumentScreen.getThemeLoader().getNoteTheme(), alpha);

        gui.blit(
            new ResourceLocation(Main.MODID, AbstractInstrumentScreen.getGlobalRootPath() + RING_GLOB_FILENAME),
            ringCenter.x, ringCenter.y,
            0, 0,
            size, size,
            size, size
        );

        ClientUtil.resetShaderColor();
    }

    public boolean isPlaying() {
        return ringAnimation.isPlaying();
    }

}
