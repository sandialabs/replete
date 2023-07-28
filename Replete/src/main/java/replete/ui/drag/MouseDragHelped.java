package replete.ui.drag;

import java.awt.event.MouseEvent;

public interface MouseDragHelped {
    void setMouseDragSelection(boolean enabled);
    boolean hasSelection(MouseEvent e);
    void clearSelection();
    RectangleIterator getRectangleIterator(int x, int y);
    void updateCleanUp();
}
