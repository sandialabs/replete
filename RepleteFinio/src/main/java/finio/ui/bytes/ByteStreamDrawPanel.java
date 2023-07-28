package finio.ui.bytes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.text.StringUtil;
import replete.ui.GuiUtil;
import replete.ui.drag.MouseDragHelped;
import replete.ui.drag.MouseDragHelper;
import replete.ui.drag.RectangleIterator;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;

public class ByteStreamDrawPanel extends RPanel implements MouseDragHelped {


        ////////////
        // FIELDS //
        ////////////

        public static final int         DEFAULT_ROWS        = Integer.MAX_VALUE;
        public static final int         DEFAULT_COLUMNS     = 0;    // Move Down When Extracted
        public static final Orientation DEFAULT_ORIENTATION = Orientation.HORIZONTAL;
        public static final ViewMode    DEFAULT_VIEW_MODE   = ViewMode.DEC_UNSIGNED;
        public static final int         BYTE_V_MARGIN       = 4;
        public static final int         BYTE_H_MARGIN       = 4;
        public static final int         RULER_L_MARGIN      = 20;
        public static final int         RULER_T_MARGIN      = 30;

        public ByteStream    stream       = new ByteStream();
        private Orientation  orientation  = DEFAULT_ORIENTATION;
        private ViewMode     viewMode     = DEFAULT_VIEW_MODE;
        private int          desiredRows  = DEFAULT_ROWS;
        private int          desiredCols  = DEFAULT_COLUMNS;
        private int          resolvedRows = 0;    // Not calculated yet
        private int          resolvedCols = 0;    // Not calculated yet
        private Font         font         = new Font("Monospaced", Font.PLAIN, 12);
        private int          rowHeight;
        private int          colWidth;
        private Set<Integer> selectedIndices = new HashSet<>();    // Primitive selection capability
        private int          lastByteIndexPainted = -1;
        private long         lastPaintDuration = 0;

        private MouseDragHelper helper = new MouseDragHelper(this);


        /////////////////
        // CONSTRUCTOR //
        /////////////////

        public ByteStreamDrawPanel() {
            Lay.hn(this, "bg=210");
            recalcPreferredDimension();
            setMouseDragSelection(true);
        }


        //////////////////////////
        // ACCESSORS / MUTATORS //
        //////////////////////////

        // Accessors

        public Orientation getOrientation() {
            return orientation;
        }
        public ViewMode getViewMode() {
            return viewMode;
        }
        public int getDesiredRows() {
            return desiredRows;
        }
        public int getDesiredCols() {
            return desiredCols;
        }
        public int getResolvedRows() {
            return resolvedRows;
        }
        public int getResolvedCols() {
            return resolvedCols;
        }
        public int getRowHeight() {
            return rowHeight;
        }
        public int getColWidth() {
            return colWidth;
        }
        public long getLastPaintDuration() {
            return lastPaintDuration;
        }

        // Mutators

        public void setStream(ByteStream stream) {
            this.stream = stream;
            recalcPreferredDimension();
            repaint();
        }
        public void setOrientation(Orientation orientation) {
            this.orientation = orientation;
            recalcPreferredDimension();
            repaint();
        }
        public void setViewMode(ViewMode viewMode) {
            this.viewMode = viewMode;
            repaint();
        }
        public void setDesiredColumns(int desiredCols) {
            this.desiredCols = desiredCols;
            recalcPreferredDimension();
            repaint();
        }
        public void setDesiredRows(int desiredRows) {
            this.desiredRows = desiredRows;
            recalcPreferredDimension();
            repaint();
        }

        private void recalcPreferredDimension() {
            byte[] data = stream.getData();

            if(desiredRows <= 0 && desiredCols <= 0) {
                resolvedRows = DEFAULT_ROWS;
                resolvedCols = DEFAULT_COLUMNS;
            } else {
                resolvedRows = desiredRows;
                resolvedCols = desiredCols;
            }
            if(orientation == Orientation.HORIZONTAL) {
                if(resolvedCols <= 0) {
                    resolvedCols = (int) Math.ceil((double) data.length / resolvedRows);
                }
            } else {
                if(resolvedRows <= 0) {
                    resolvedRows = (int) Math.ceil((double) data.length / resolvedCols);
                }
            }
            rowHeight = 30;   // Calculate
            colWidth = 100;   // Calculate
            Dimension dim = new Dimension(
                RULER_L_MARGIN + resolvedCols * colWidth,
                RULER_T_MARGIN + resolvedRows * rowHeight
            );
            setMinimumSize(dim);
            setPreferredSize(dim);
            updateUI();
            fireStateUpdatedNotifier();
        }


        ////////////////
        // OVERRIDDEN //
        ////////////////

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if(resolvedCols <= 0 && resolvedRows <= 0) {
                throw new IllegalStateException();
            }
            long T = System.currentTimeMillis();
            paintInner(g);
            lastPaintDuration = System.currentTimeMillis() - T;
        }

        private void paintInner(Graphics g) {
            g.setColor(Color.pink);
            g.fillRect(0, 0, RULER_L_MARGIN, 10000);
            g.fillRect(0, 0, 10000, RULER_T_MARGIN);

            helper.paint(g);
            g.setColor(Color.black);

            for(int c = 0; c < resolvedCols; c++) {
                String msg = "" + c;
                int x = RULER_L_MARGIN + c * colWidth;
                int y = 0;
                int xMid = x + colWidth / 2;
                int msgWidth = GuiUtil.stringWidth(g, msg);
                int msgHeight = GuiUtil.stringHeight(g);
                int msgX = xMid - msgWidth / 2;
                int msgY = y + msgHeight + 3;
                g.setFont(font);
                g.drawString(msg, msgX, msgY);
            }

            byte[] data = stream.getData();
            for(int b = 0; b < data.length; b++) {

                Rectangle bounds = getByteRegionBounds(b);
                if(bounds == null) {
                    lastByteIndexPainted = b - 1;
                    break;
                }

                Color bgInner = Color.white;

                // Get this byte's value
                byte bValue = data[b];
                int  iValue = bValue & 0xFF;

                // Calculate text to display
                String msg;
                switch(viewMode) {
                    case DEC_SIGNED:   msg = String.valueOf(bValue);         break;
                    case DEC_UNSIGNED: msg = String.valueOf(iValue);         break;
                    case OCTAL:        msg = Integer.toOctalString(iValue);  break;
                    case HEX_LOWER:    msg = StringUtil.padLeft(
                                           Integer.toHexString(iValue),
                                           '0', 2);
                        break;
                    case HEX_UPPER:    msg = StringUtil.padLeft(
                                           Integer.toHexString(iValue).toUpperCase(),
                                           '0', 2);
                        break;
                    case BINARY_DOTS:  msg = null; break;
                    case BINARY_01:    msg = StringUtil.padLeft(
                                           Integer.toBinaryString(iValue),
                                           '0', 8);
                                       msg = msg.substring(0, 4) + " " + msg.substring(4);
                        break;
                    case ASCII_127:
                        if(iValue < 32 || iValue > 127) {
                            msg = "#" + iValue;
                            bgInner = Lay.clr("FFF39B");
                        } else {
                            msg = "" + (char) iValue;
                        }
                        break;
                    default:           msg = "ERR";
                }

                int x = bounds.x;
                int y = bounds.y;

                int xMid = x + colWidth / 2;
                g.drawRect(x, y, colWidth - 1, rowHeight - 1);

                int xInner = x + BYTE_H_MARGIN;
                int yInner = y + BYTE_V_MARGIN;
                int wInner = colWidth - BYTE_H_MARGIN * 2;
                int hInner = rowHeight - BYTE_V_MARGIN * 2;
                g.setColor(selectedIndices.contains(b) ? Color.yellow : bgInner);
                g.fillRect(xInner, yInner, wInner, hInner);
                g.setColor(Color.black);
                g.drawRect(xInner, yInner, wInner - 1, hInner);

                // Paint the displayed text
                if(msg != null) {
                    int msgWidth = GuiUtil.stringWidth(g, msg);
                    int msgHeight = GuiUtil.stringHeight(g);
                    int msgX = xMid - msgWidth / 2;
                    int msgY = y + msgHeight + 3;
                    g.setFont(font);
                    g.drawString(msg, msgX, msgY);
                } else {
                    // draw bits
                }
            }

            lastByteIndexPainted = data.length - 1;
        }

        private int getByteIndex(int x, int y) {
            x -= RULER_L_MARGIN;
            y -= RULER_T_MARGIN;

            int col = x / colWidth;
            if(col >= resolvedCols) {
                return -1;
            }

            int row = y / rowHeight;
            if(row >= resolvedRows) {
                return -1;
            }

            int index;
            if(orientation == Orientation.HORIZONTAL) {
                index = resolvedCols * row + col;
            } else {
                index = resolvedRows * col + row;
            }
            if(index > lastByteIndexPainted) {
                index = -1;
            }

            return index;
        }

        private Rectangle getByteRegionBounds(int index) {

            // Calculate row, col for this value
            int row;
            int col;

            if(orientation == Orientation.HORIZONTAL) {
                col = index % resolvedCols;
                row = index / resolvedCols;
            } else {
                row = index % resolvedRows;
                col = index / resolvedRows;
            }

            if(resolvedRows > 0 && row >= resolvedRows ||
                            resolvedCols > 0 && col >= resolvedCols) {
                return null;
            }

            // Calculate outer region
            int x = col * colWidth + RULER_L_MARGIN;
            int y = row * rowHeight + RULER_T_MARGIN;

            return new Rectangle(x, y, colWidth, rowHeight);
        }


        //////////////
        // NOTIFIER //
        //////////////

        private ChangeNotifier stateUpdatedNotifier = new ChangeNotifier(this);
        public void addStateUpdatedListener(ChangeListener listener) {
            stateUpdatedNotifier.addListener(listener);
        }
        private void fireStateUpdatedNotifier() {
            stateUpdatedNotifier.fireStateChanged();
        }


        ////////////////////////////
        // DRAG (LASSO) SELECTION //
        ////////////////////////////

        @Override
        public void setMouseDragSelection(boolean enabled) {
            helper.setMouseDragSelection(enabled);
        }
        @Override
        public boolean hasSelection(MouseEvent e) {
            return getByteIndex(e.getPoint().x, e.getPoint().y) != -1;
        }
        @Override
        public RectangleIterator getRectangleIterator(int x, int y) {
            return new ByteRegionIterator(x, y);
        }
        @Override
        public void clearSelection() {
            selectedIndices.clear();
        }

        @Override
        public void updateCleanUp() {
//            setLeadSelectionPath(null);
        }

        private class ByteRegionIterator extends RectangleIterator {
            private int nextIndex;
            private int curIndex;
            public ByteRegionIterator(int x, int y) {
                curIndex = nextIndex = 0;
            }
            @Override
            public boolean hasNext() {
                byte[] data = stream.getData();
                return nextIndex < data.length;
            }
            @Override
            public Rectangle next() {
                curIndex = nextIndex;
                return getByteRegionBounds(nextIndex++);
            }
            @Override
            public void addSelection() {
                selectedIndices.add(curIndex);
            }
            @Override
            public void removeSelection() {
                selectedIndices.remove(curIndex);
            }
        }
    }
