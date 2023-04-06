package com.cstav.genshinstrument.util;

public class RGBColor {
    
    private int red, green, blue;
    public RGBColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public RGBColor invert() {
        return new RGBColor(255 - red, 255 - green, 255 - blue);
    }

    public int getNumeric() {
        return (red << 16) | (green << 8) | blue;
    }


    public int getRed() {
        return red;
    }
    public void setRed(final int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }
    public void setGreen(final int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }
    public void setBlue(final int blue) {
        this.blue = blue;
    }
}
