package com.cstav.genshinstrument.client.gui.screen.instrument.partial.note;

import java.awt.Point;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.animation.RingAnimationController;
import com.cstav.genshinstrument.client.util.ClientUtil;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoteRing {
    public static final String RING_GLOB_FILENAME = "note/ring.png";

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
    
    
    public void render(final PoseStack stack, final InstrumentThemeLoader themeLoader) {
        if (!ringAnimation.isPlaying())
            return;

        ringAnimation.update();

        final Point ringCenter = ClientUtil.getInitCenter(note.getInitX(), note.getInitY(), note.instrumentScreen.getNoteSize(), size);


        ClientUtil.setShaderColor(themeLoader.noteRing(), alpha);
        ClientUtil.displaySprite(AbstractInstrumentScreen.getInternalResourceFromGlob(RING_GLOB_FILENAME));

        GuiComponent.blit(stack,
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
