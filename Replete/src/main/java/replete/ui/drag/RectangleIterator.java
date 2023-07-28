package replete.ui.drag;

import java.awt.Rectangle;
import java.util.Iterator;

public abstract class RectangleIterator implements Iterator<Rectangle> {
    @Override
    public void remove() {
        // Nothing
    }
    public abstract void removeSelection();
    public abstract void addSelection();
}
