package replete.ui.drag;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JViewport;


// To supplement not having this kind of functionality within Component/JComponent
public class MouseDragHelper {


    ////////////
    // FIELDS //
    ////////////

    private final Color DRAG_CONTEXT_BORDER   = new Color( 52, 175,  97, 120);   // Lay.clr("6382BF");
    private final Color DRAG_CONTEXT_FILL     = new Color(127, 226, 150, 120);   // Lay.clr("B8CFE5");
    private final Color DRAG_CONTEXT_BORDER_R = new Color(255,   0,   0, 120);   // Lay.clr("6382BF");
    private final Color DRAG_CONTEXT_FILL_R   = new Color(225,  25,  40, 120);   // Lay.clr("B8CFE5");

    private DragContext dragContext;

    private JComponent contextCmp;
    private MouseDragHelped contextHelped;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public MouseDragHelper(JComponent context) {
        contextCmp = context;
        contextHelped = (MouseDragHelped) context;
    }


    ////////////////////////////
    // DRAG (LASSO) SELECTION //
    ////////////////////////////

    public void setMouseDragSelection(boolean enabled) {
        if(enabled) {
            contextCmp.addMouseListener(dragSetUnsetListener);
            contextCmp.addMouseMotionListener(dragSelListener);
            contextCmp.addKeyListener(dragKeyListener);
        } else {
            contextCmp.removeMouseListener(dragSetUnsetListener);
            contextCmp.removeMouseMotionListener(dragSelListener);
            clearDragRectangle();
        }
    }

    private void clearDragRectangle() {
        dragContext = null;
        contextCmp.repaint();
    }

    private KeyListener dragKeyListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            updateDrag(e);
        }
        @Override
        public void keyReleased(KeyEvent e) {
            updateDrag(e);
        }
    };

    private MouseListener dragSetUnsetListener = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            if(!contextHelped.hasSelection(e)) {
                dragContext = new DragContext(
                    e.getPoint(), e.isControlDown(), e.isShiftDown(), e.isAltDown(), contextCmp);
                contextCmp.repaint();
            }
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            clearDragRectangle();
        }
    };

    private MouseMotionListener dragSelListener = new MouseMotionAdapter() {
        @Override
        public void mouseDragged(MouseEvent e) {
            if(dragContext != null) {
                dragContext.setEnd(
                    e.getPoint(), e.isControlDown(), e.isShiftDown(), e.isAltDown());
                JViewport p = (JViewport) contextCmp.getParent();
                Rectangle vr = p.getViewRect();
                if(!vr.contains(e.getPoint())) {

                    int dx = 0;
                    if(e.getX() >= vr.x + vr.width) {
                        dx = e.getX() - (vr.x + vr.width) + 1;
                    } else if(e.getX() < vr.x) {
                        dx = e.getX() - vr.x;
                    }

                    int dy = 0;
                    if(e.getY() >= vr.y + vr.height) {
                        dy = e.getY() - (vr.y + vr.height) + 1;
                    } else if(e.getY() < vr.y) {
                        dy = e.getY() - vr.y;
                    }

                    int nx;
                    if(dx < 0) {
                        nx = Math.max(vr.x + dx, 0);   // Left edge boundary
                    } else if(dx > 0) {
                        if(dx + vr.x + vr.width < p.getViewSize().width) {
                            nx = vr.x + dx;            // Ok to add
                        } else {
                            nx = p.getViewSize().width - vr.width - 1;   // Max right edge boundary
                        }
                    } else {
                        nx = vr.x;   // No change
                    }

                    int ny;
                    if(dy < 0) {
                        ny = Math.max(vr.y + dy, 0);   // Upper edge boundary
                    } else if(dy > 0) {
                        if(dy + vr.y + vr.height < p.getViewSize().height) {
                            ny = vr.y + dy;            // Ok to add
                        } else {
                            ny = p.getViewSize().height - vr.height - 1;   // Max lower edge boundary
                        }
                    } else {
                        ny = vr.y;   // No change
                    }

                    p.setViewPosition(new Point(nx, ny));
                }

                updateSelectedFromDragContext();
            }
        }
    };

    private void updateSelectedFromDragContext() {
        if(dragContext.selected && !dragContext.ctrl) {
            contextHelped.clearSelection();
        }

        RectangleIterator iterator = contextHelped.getRectangleIterator(
            dragContext.x, dragContext.y);

        while(iterator.hasNext()) {
            Rectangle rowBounds = iterator.next();
            if(rowBounds == null) {
                continue;
            }
            if(dragContext.ctrl && dragContext.remove) {
                if(rowBounds.intersects(dragContext.toRectangle())) {
                    iterator.removeSelection();
                }
            } else {
                if(rowBounds.intersects(dragContext.toRectangle())) {
                    if(!dragContext.selected && !dragContext.ctrl) {
                        contextHelped.clearSelection();
                        dragContext.selected = true;
                    }
                    iterator.addSelection();
                }
            }

            // Check to see if this row is now below the user's
            // drag selection and stop the loop if so.
            if(rowBounds.y > dragContext.y + dragContext.h + 1) {  // +1 fudge
//                break;
            }
        }

        contextHelped.updateCleanUp();

        contextCmp.repaint();
    }

    private void updateDrag(KeyEvent e) {
        int removeKey = KeyEvent.VK_BACK_QUOTE;
        if(dragContext != null && dragContext.start != null && dragContext.end != null) {
            if(e.getKeyCode() == KeyEvent.VK_SHIFT || e.getKeyCode() == KeyEvent.VK_ALT ||
                            e.getKeyCode() == removeKey) {
                dragContext.setModifiers(e.isControlDown(), e.isShiftDown(), e.isAltDown(),
                    e.getKeyCode() == removeKey && e.getID() == KeyEvent.KEY_PRESSED);
                updateSelectedFromDragContext();
            }
        }
    }


    public void paint(Graphics g) {
        if(dragContext != null && dragContext.start != null && dragContext.end != null) {
            if(dragContext.ctrl && dragContext.remove) {
                g.setColor(DRAG_CONTEXT_FILL_R);
            } else {
                g.setColor(DRAG_CONTEXT_FILL);
            }
            g.fillRect(dragContext.x, dragContext.y, dragContext.w, dragContext.h);
            if(dragContext.ctrl && dragContext.remove) {
                g.setColor(DRAG_CONTEXT_BORDER_R);
            } else {
                g.setColor(DRAG_CONTEXT_BORDER);
            }
            g.drawRect(dragContext.x, dragContext.y, dragContext.w - 1, dragContext.h - 1);
        }
    }
}
