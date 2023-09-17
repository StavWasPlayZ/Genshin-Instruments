package com.cstav.genshinstrument.client.gui.screen.instrument.partial.note;

import java.awt.Point;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.animation.RingAnimationController;
import com.cstav.genshinstrument.client.util.ClientUtil;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoteRing {
    public static final String RING_GLOB_FILENAME = "ring.png";

    protected final RingAnimationController ringAnimation;

    public final NoteButton note;
    public int size;
    public float alpha;

    public NoteRing(final NoteButton note, final boolean isForeign) {
        this.note = note;
        ringAnimation = new RingAnimationController(.3f, 40, this);

        // Immediately play
        if (isForeign)
            ringAnimation.play(-.4f);
        else
            ringAnimation.play();
    }
    
    
    public void render(final GuiGraphics gui, final InstrumentThemeLoader themeLoader) {
        if (!ringAnimation.isPlaying())
            return;

        ringAnimation.update();

        final Point ringCenter = ClientUtil.getInitCenter(note.getInitX(), note.getInitY(), note.instrumentScreen.getNoteSize(), size);


        ClientUtil.setShaderColor(themeLoader.getNoteRingTheme(), alpha);

        gui.blit(AbstractInstrumentScreen.getInternalResourceFromGlob(RING_GLOB_FILENAME),
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
