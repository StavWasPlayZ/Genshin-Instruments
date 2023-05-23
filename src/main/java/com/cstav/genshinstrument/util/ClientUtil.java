package com.cstav.genshinstrument.util;

import java.awt.Point;

public class ClientUtil {
    
    /**
     * @return The point in the center of the described widget
     */
    public static Point getInitCenter(int initX, int initY, int initSize, int currSize) {
        return new Point(
            (initSize - currSize) / 2 + initX,
            (initSize - currSize) / 2 + initY
        );
    }

}
