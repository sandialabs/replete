package replete.ui.sp;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import replete.ui.GuiUtil;
import replete.ui.lay.Lay;

public class RulerPanel extends JPanel {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    private static final int MARGIN_TOP = 3;         // 3 for JTextPane, 0 for JTextArea?
    private static final int DIGIT_WIDTH = 7;        // Based on font used for line numbers
    private static final int CMP_MARGIN_LHS = 5;     // Margin needed if on left of viewport
    private static final int CMP_MARGIN_RHS = 1;     // Margin needed if on right of viewport
    private static final int LINE_NUM_MARGIN = 2;    // Margin on either side of line numbers
    private static final int RANGE_AND_ICON_MARGIN = 16;
    private static final int DEFAULT_LINE_HEIGHT = 14;   // 16 For Arial/12pt, 14 For Courier New/12pt
    private static final Font FONT = new Font("Courier New", Font.PLAIN, 12);
    private static final int DEFAULT_BASELINE_PIXELS = 3;
    private static final Color DEFAULT_BG_COLOR = Color.white;
    private static final int DEFAULT_MIN_RULER_DIGIT_COLUMNS = 1;

    // Other

    private boolean showRangesAndIcons = false;
    private int baselinePix = DEFAULT_BASELINE_PIXELS;
    private int lineHeight = DEFAULT_LINE_HEIGHT;
    private int minRulerDigitColumns = DEFAULT_MIN_RULER_DIGIT_COLUMNS;
    private int rows;
    private RulerModel rhModel;
    private Color marginFg = Color.black;
    private Color marginBg = new JPanel().getBackground();
    private boolean rightToLeft = false;
    private int startLineNumber = 1;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RulerPanel(RulerModel rhModel) {
        this.rhModel = rhModel;
        rhModel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                repaint();
            }
        });
        setBackground(DEFAULT_BG_COLOR);
        setRowCount(1);
        ToolTipManager.sharedInstance().registerComponent(this);
        ToolTipManager.sharedInstance().setInitialDelay(0);
        addMouseMotionListener(new MouseMotionListener() {
            public void mouseMoved(MouseEvent e) {
                updateCursor(e);
            }
            public void mouseDragged(MouseEvent e) {
                updateCursor(e);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                List<RulerDescriptor> underMouse = getUnderMouse(e.getY(), false);
                if(underMouse.size() != 0) {
                    int mouseLine = (e.getY() - MARGIN_TOP) / lineHeight;
                    for(RulerDescriptor desc : underMouse) {
                        RulerMouseClickEvent e2 = new RulerMouseClickEvent(
                            e.getX(), e.getY(), mouseLine, underMouse);
                        desc.getClickListener().mouseClicked(e2);
                    }
                }
            }
        });
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public int getBaselinePixels() {
        return baselinePix;
    }
    public boolean isRightToLeft() {
        return rightToLeft;
    }
    public boolean isShowRangesAndIcons() {
        return showRangesAndIcons;
    }
    public int getMinRulerDigitColumns() {
        return minRulerDigitColumns;
    }
    public int getStartLineNumber() {
        return startLineNumber;
    }

    // Mutators

    public void setBaselinePixels(int baselinePix) {
        this.baselinePix = baselinePix;
        repaint();
    }
    public void setRightToLeft(boolean rightToLeft) {
        this.rightToLeft = rightToLeft;
        updateWidth();
    }
    public void setMarginForeground(Color c) {
        marginFg = c;
        repaint();
    }
    public void setMarginBackground(Color c) {
        marginBg = c;
        repaint();
    }
    public void setHeight(int height) {
        Lay.hn(this, "prefh=" + height);
        Lay.hn(this, "minh=" + height);
    }
    public void setRowCount(int r) {
        rows = r;
        updateWidth();
    }
    public void setLineHeight(int height) {
        lineHeight = height;
        repaint();
    }
    public void setShowRangesAndIcons(boolean showRangesAndIcons) {
        this.showRangesAndIcons = showRangesAndIcons;
        updateWidth();
    }
    public void setMinRulerDigitColumns(int minRulerDigitColumns) {
        this.minRulerDigitColumns = minRulerDigitColumns;
        updateWidth();
    }
    public void setStartLineNumber(int startLineNumber) {
        this.startLineNumber = startLineNumber;
        updateWidth();
    }

    private void updateWidth() {
        int cmpMargin = rightToLeft ? CMP_MARGIN_RHS : CMP_MARGIN_LHS;
        int offset = startLineNumber - 1;
        int digits = ("" + (rows + offset)).length();
        digits = Math.max(digits, minRulerDigitColumns);
        int width = DIGIT_WIDTH * digits + cmpMargin + LINE_NUM_MARGIN * 2;
        if(showRangesAndIcons) {
            width += RANGE_AND_ICON_MARGIN;
        }
        Lay.hn(this, "prefw=" + width);
        Lay.hn(this, "minw=" + width);
        repaint();
    }


    ///////////////
    // TOOL TIPS //
    ///////////////

    @Override
    public String getToolTipText(MouseEvent event) {
        Point p = new Point(event.getX(), event.getY());
        String t = getToolTip(p);
        if(t != null) {
            return t;
        }
        return super.getToolTipText(event);
    }

    @Override
    public Point getToolTipLocation(MouseEvent event) {
        String t = getToolTip(event.getPoint());
        if (t != null) {
            int offset = rightToLeft ? 16 - 0 : 16;
            // NOTE: If TT extends off of native window, will cause flicker.
            // Solution: using the calculated with of TT and possible discovery of whether or not
            // TT is outside window, can shift left.
            return new Point(event.getPoint().x + offset, event.getPoint().y);
        }
        return super.getToolTipLocation(event);
    }

    private String getToolTip(Point p) {
        List<RulerDescriptor> underMouse = getUnderMouse(p.y, true);
        String toolTip = "";
        boolean first = true;
        for(RulerDescriptor d : underMouse) {
            if(!first) {
                toolTip += "; ";
            }
            toolTip += d.getToolTip();
            first = false;
        }
        return toolTip.equals("") ? null : toolTip;
    }


    ///////////
    // PAINT //
    ///////////

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(!rightToLeft) {
            paintOnLeftHandSide(g);
        } else {
            paintOnRightHandSide(g);
        }

        // Debug
//        g.setColor(Color.black);
//        for(int y = MARGIN_TOP; y < getHeight(); y += LINE_HEIGHT) {
//            g.drawLine(0, y, 3, y);
//        }
    }

    private void paintOnLeftHandSide(Graphics g) {
        int wd = getMinimumSize().width;
        int ht = getMinimumSize().height;

        // Right margin line (1 px to right of margin area)
        g.setColor(marginBg.darker());
        g.drawLine(wd - CMP_MARGIN_LHS, 0,
                   wd - CMP_MARGIN_LHS, ht);

        // Margin shading
        g.setColor(marginBg);
        g.fillRect(0, 0, wd - CMP_MARGIN_LHS, ht);

        // Line numbers
        g.setFont(FONT);
        g.setColor(marginFg);
        int offset = startLineNumber - 1;
        for(int row = offset; row < rows + offset; row++) {
            int rowBase1 = row + 1;
            String rowStr = rowBase1 + "";
            int baseline = (row - offset + 1) * lineHeight - DEFAULT_BASELINE_PIXELS + MARGIN_TOP;
            g.drawString(rowStr,
                wd - GuiUtil.stringWidth(g, rowStr) - CMP_MARGIN_LHS - LINE_NUM_MARGIN,
                baseline);
        }

        if(showRangesAndIcons) {

            // Range Highlights
            for(String cat : rhModel.getRangeHighlights().keySet()) {
                Map<LineNumberRange, RangeHighlightDescriptor> catRangeHighlights = rhModel.getRangeHighlights().get(cat);
                for(LineNumberRange lines : catRangeHighlights.keySet()) {
                    RangeHighlightDescriptor rhDescriptor = catRangeHighlights.get(lines);

                    if(lines.start < rows) {
                        int end = lines.endNonIncl < rows ? lines.endNonIncl : rows - 1;
                        int topY = lines.start * lineHeight + MARGIN_TOP;
                        int linesCovered = end - lines.start;
                        int height = linesCovered * lineHeight - 1;

                        // Filled rectangle
                        g.setColor(rhDescriptor.getColor());
                        g.fillRect(0, topY, RANGE_AND_ICON_MARGIN, height);

                        // Boundary lines
                        g.setColor(rhDescriptor.getColor().darker());
                        g.drawLine(0, topY,
                            0 + RANGE_AND_ICON_MARGIN - 1, topY);
                        g.drawLine(0, topY + height,
                            0 + RANGE_AND_ICON_MARGIN - 1, topY + height);
                    }
                }
            }

            // Icons
            for(String cat : rhModel.getIcons().keySet()) {
                Map<LineNumberRange, IconDescriptor> catIcons = rhModel.getIcons().get(cat);
                for(LineNumberRange lines : catIcons.keySet()) {
                    for(int line = lines.getStart(); line < lines.getEndNonIncl(); line++) {
                        if(line < rows + offset) {
                            IconDescriptor imgDescriptor = catIcons.get(lines);
                            int topY = (line - offset) * lineHeight + MARGIN_TOP;
                            g.drawImage(imgDescriptor.getIcon().getImage(),
                                0, topY, RANGE_AND_ICON_MARGIN, lineHeight, null);
                        }
                    }
                }
            }
        }
    }

    private void paintOnRightHandSide(Graphics g) {
        int wd = getMinimumSize().width;
        int ht = getMinimumSize().height;

        // Right left line (1 px to right of scroll bar)
        g.setColor(marginBg.darker());
        g.drawLine(0, 0,
                   0, ht);

        // Margin shading
        g.setColor(marginBg);
        g.fillRect(CMP_MARGIN_RHS, 0, wd, ht);

        // Line numbers
        g.setFont(FONT);
        g.setColor(marginFg);
        int offset = startLineNumber - 1;
        for(int row = offset; row < rows + offset; row++) {
            int rowBase1 = row + 1;
            String rowStr = rowBase1 + "";
            int baseline = (row - offset + 1) * lineHeight - DEFAULT_BASELINE_PIXELS + MARGIN_TOP;
            g.drawString(rowStr,
                wd - GuiUtil.stringWidth(g, rowStr) - LINE_NUM_MARGIN,
                baseline);
        }

        if(showRangesAndIcons) {

            // Range Highlights
            for(String cat : rhModel.getRangeHighlights().keySet()) {
                Map<LineNumberRange, RangeHighlightDescriptor> catRangeHighlights = rhModel.getRangeHighlights().get(cat);
                for(LineNumberRange lines : catRangeHighlights.keySet()) {
                    RangeHighlightDescriptor rhDescriptor = catRangeHighlights.get(lines);

                    if(lines.start < rows) {
                        int end = lines.endNonIncl < rows ? lines.endNonIncl : rows - 1;
                        int topY = lines.start * lineHeight + MARGIN_TOP;
                        int linesCovered = end - lines.start;
                        int height = linesCovered * lineHeight - 1;

                        // Filled rectangle
                        g.setColor(rhDescriptor.getColor());
                        g.fillRect(CMP_MARGIN_RHS, topY, RANGE_AND_ICON_MARGIN, height);

                        // Boundary lines
                        g.setColor(rhDescriptor.getColor().darker());
                        g.drawLine(CMP_MARGIN_RHS, topY,
                            CMP_MARGIN_RHS + RANGE_AND_ICON_MARGIN - 1, topY);
                        g.drawLine(CMP_MARGIN_RHS, topY + height,
                            CMP_MARGIN_RHS + RANGE_AND_ICON_MARGIN - 1, topY + height);
                    }
                }
            }

            // Icons
            for(String cat : rhModel.getIcons().keySet()) {
                Map<LineNumberRange, IconDescriptor> catIcons = rhModel.getIcons().get(cat);
                for(LineNumberRange lines : catIcons.keySet()) {
                    for(int line = lines.getStart(); line < lines.getEndNonIncl(); line++) {
                        if(line < rows + offset) {
                            IconDescriptor imgDescriptor = catIcons.get(lines);
                            int topY = (line - offset) * lineHeight + MARGIN_TOP;
                            g.drawImage(imgDescriptor.getIcon().getImage(),
                                CMP_MARGIN_RHS, topY, RANGE_AND_ICON_MARGIN, lineHeight, null);
                        }
                    }
                }
            }
        }
    }


    //////////
    // MISC //
    //////////

    private List<RulerDescriptor> getUnderMouse(int y, boolean forToolTip) {
        List<RulerDescriptor> clickables = new ArrayList<>();
        int offset = (startLineNumber - 1);
        int mouseLine = (y - MARGIN_TOP) / lineHeight + offset;

        for(String cat : rhModel.getIcons().keySet()) {
            Map<LineNumberRange, IconDescriptor> catIcons = rhModel.getIcons().get(cat);
            for(LineNumberRange lines : catIcons.keySet()) {
                IconDescriptor id = catIcons.get(lines);
                if(mouseLine >= lines.getStart() && mouseLine < Math.min(lines.getEndNonIncl(), rows + offset)) {
                    if(forToolTip) {
                        if(id.getToolTip() != null) {
                            clickables.add(id);
                        }
                    } else {
                        if(id.getClickListener() != null) {
                            clickables.add(id);
                        }
                    }
                }
            }
        }

        for(String cat : rhModel.getRangeHighlights().keySet()) {
            Map<LineNumberRange, RangeHighlightDescriptor> catRangeHighlights = rhModel.getRangeHighlights().get(cat);
            for(LineNumberRange lines : catRangeHighlights.keySet()) {
                RangeHighlightDescriptor rd = catRangeHighlights.get(lines);
                if(mouseLine >= lines.getStart() && mouseLine < Math.min(lines.getEndNonIncl(), rows + offset)) {
                    if(forToolTip) {
                        if(rd.getToolTip() != null) {
                            clickables.add(rd);
                        }
                    } else {
                        if(rd.getClickListener() != null) {
                            clickables.add(rd);
                        }
                    }
                }
            }
        }

        return clickables;
    }

    private void updateCursor(MouseEvent e) {
        List<RulerDescriptor> underMouse = getUnderMouse(e.getY(), false);
        if(underMouse.size() == 0) {
            setCursor(Cursor.getDefaultCursor());
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }
}
