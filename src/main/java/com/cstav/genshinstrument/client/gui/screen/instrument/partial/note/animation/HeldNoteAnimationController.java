package com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.animation;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.held.HeldGridNoteButton;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeldNoteAnimationController extends NoteAnimationController {
    public HeldNoteAnimationController(double duration, double targetValue, HeldGridNoteButton button) {
        super(duration, targetValue, button);
    }

    private boolean holding;

    @Override
    protected void animFrame(final double targetTime, final double deltaValue) {
        // Assuming the shape will always be a square
        if (holding)
            dSize -= deltaValue * SCALE_FACTOR;
        else
            dSize += deltaValue * SCALE_FACTOR;

        updateButton();
    }

    @Override
    protected void resetAnimVars() {
        super.resetAnimVars();

        if (!holding) {
            final int size = (int)(button.instrumentScreen.getNoteSize() - targetValue);
            button.setWidth(size);
            button.setHeight(size);
            dSize = size;

            button.moveToCenter();
        }
    }

    public void playReleased(final boolean isForeign) {
        holding = false;
        play(isForeign);
    }
    public void playHold(boolean isForeign) {
        holding = true;
        play(isForeign);
    }

    @Override
    public void stop() {
        holding = !holding;
        super.stop();
    }
}
