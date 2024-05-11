package com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.held;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButtonRenderer;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteRing;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.animation.HeldNoteAnimationController;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.animation.NoteAnimationController;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class HeldNoteButtonRenderer extends NoteButtonRenderer {
    private static final int RING_ADDITION_INTERVAL = 4;

    public HeldNoteButtonRenderer(HeldGridNoteButton noteButton, Supplier<ResourceLocation> noteTextureProvider) {
        super(noteButton, noteTextureProvider);
    }

    private HeldGridNoteButton getBtn() {
        return (HeldGridNoteButton) noteButton;
    }

    private float ringTimeAlive = 0;
    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick, InstrumentThemeLoader themeLoader) {
        if (getBtn().isHeld()) {
            ringTimeAlive += partialTick;
            if (ringTimeAlive > RING_ADDITION_INTERVAL) {
                addRing(true);
                ringTimeAlive = 0;
            }
        } else {
            ringTimeAlive = 0;
        }

        super.render(gui, mouseX, mouseY, partialTick, themeLoader);
    }
    public void playNoteAnimation(final boolean isForeign) {
        foreignPlaying = isForeign;
        playHold();
        addRing();
    }

    @Override
    public void addRing() {
        addRing(false);
    }
    public void addRing(final boolean isConsecutive) {
        final NoteRing ring = new HeldNoteRing(getBtn(), foreignPlaying, isConsecutive);
        rings.add(ring);
        ring.playAnim();
    }

    @Override
    protected NoteAnimationController initNoteAnimation() {
        // Divide by 2 because we are doing the start-end animation for each
        // Unlike NoteAnimationController which does them at once
        return new HeldNoteAnimationController(NOTE_DUR / 2, NOTE_TARGET_VAL / 2, getBtn());
    }
    private HeldNoteAnimationController noteAnimation() {
        return (HeldNoteAnimationController)noteAnimation;
    }

    public void playRelease() {
        noteAnimation().playReleased(foreignPlaying);
    }
    public void playHold() {
        noteAnimation().playHold(foreignPlaying);
    }
}
