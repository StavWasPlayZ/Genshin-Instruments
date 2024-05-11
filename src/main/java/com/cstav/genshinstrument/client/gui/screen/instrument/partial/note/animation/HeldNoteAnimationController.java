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
        if (!holding) {
            // Keep holdin; call super-super reset only
            duration = initDuration;
            targetValue = initTargetValue;

            animTime = 0;
        } else {
            super.resetAnimVars();
        }
//        super.resetAnimVars();
//        //TODO make these the holding params
//        if (holding) {
//            button.setWidth(button.instrumentScreen.getNoteSize());
//            button.setHeight(button.instrumentScreen.getNoteSize());
//            dSize = button.instrumentScreen.getNoteSize();
//
//            button.setPosition(button.getInitX(), button.getInitY());
//        }
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
