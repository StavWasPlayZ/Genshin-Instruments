package com.cstav.genshinstrument.client.gui.screens.instrument.partial.note;

import com.cstav.genshinstrument.client.gui.GuiAnimationController;

public class NoteButtonAnimationController extends GuiAnimationController {

    // The size of this button in double
    protected double dSize;
    protected NoteButton button;
    public NoteButtonAnimationController(float duration, float targetValue, final NoteButton button) {
        super(duration, targetValue);
        this.button = button;
    }


    @Override
    protected void animFrame(float deltaValue) {
        // Assuming the shape will always be a square
        if (getAnimState() == AnimationState.START)
            dSize -= deltaValue * 1.5;
        else
            dSize += deltaValue * 1.5;
        
        final int size = (int)dSize;
        button.setWidth(size);
        button.setHeight(size);

        button.setPosition(
            (NoteButton.getSize() - button.getWidth()) / 2 + button.getInitX(),
            (NoteButton.getSize() - button.getWidth()) / 2 + button.getInitY()
        );
    }

    @Override
    protected void resetAnimVars() {
        super.resetAnimVars();

        button.setWidth(NoteButton.getSize());
        button.setHeight(NoteButton.getSize());
        dSize = NoteButton.getSize();

        button.setPosition(button.getInitX(), button.getInitY());
    }
    
}
