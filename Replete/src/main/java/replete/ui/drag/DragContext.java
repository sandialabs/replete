package replete.ui.drag;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;

class DragContext {
    Point start;
    Point end;
    boolean selected = false;
    int x;
    int y;
    int w;
    int h;
    boolean ctrl;
    boolean shift;
    boolean alt;
    boolean remove;

    JComponent contextCmp;

    public DragContext(Point start, boolean ctrl, boolean shift, boolean alt, JComponent contextCmp) {
        this.start = start;

        this.ctrl = ctrl;
        this.shift = shift;
        this.alt = alt;

        this.contextCmp = contextCmp;
    }

    public void setEnd(Point end, boolean ctrl, boolean shift, boolean alt) {
        this.end = end;

        this.ctrl = ctrl;
        this.shift = shift;
        this.alt = alt;

        updateBounds();
    }

    public void setModifiers(boolean ctrl, boolean shift, boolean alt, boolean remove) {
        this.ctrl = ctrl;
        this.shift = shift;
        this.alt = alt;
        this.remove = remove;

        updateBounds();
    }

    private void updateBounds() {
        int sx = shift ? 0 : start.x;
        int ex = shift ? contextCmp.getWidth() - 1 : end.x;

        int sy = alt ? 0 : start.y;
        int ey = alt ? contextCmp.getHeight() - 1 : end.y;

        x = Math.min(sx, ex);
        y = Math.min(sy, ey);
        w = Math.abs(sx - ex) + 1;
        h = Math.abs(sy - ey) + 1;
    }

    public Rectangle toRectangle() {
        return new Rectangle(x, y, w, h);
    }
}
