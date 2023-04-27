package com.cstav.genshinstrument.client.gui.screens.options.widget.copied;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

public abstract class AbstractWidget2 extends AbstractWidget {

    public AbstractWidget2(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);
    }

    public boolean isHovered;

    public void setX(final int x) {
        this.x = x;
    }
    public int getX() {
        return x;
    }

    public void setY(final int y) {
        this.y = y;
    }
    public int getY() {
        return y;
    }
    
}
