package gotstyle.model;

import java.awt.Color;

import replete.ui.lay.Lay;

public enum Status {
    GOOD(Lay.clr("25CC00")),
    PENDING(Lay.clr("FF960C")),
    BAD(Color.red);

    private Color color;
    private Status(Color color) {
        this.color = color;
    }
    public Color getColor() {
        return color;
    }
}
