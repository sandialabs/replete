package replete.ui.text.bubble;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.ui.ClipboardUtil;
import replete.ui.DrawUtil;
import replete.ui.GuiUtil;
import replete.ui.button.RButton;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.text.RTextArea;

// TODO: look at jpanel constructors, jtextfield constructors
// look at fonts, look at when panel is huge
// non-ASCII characters?
// rebubble feasible?

// * repositioning bugs (still jumps left or right sometimes)
// * selection cursor and bubbles to the left and right move shift

// Features Could Add But Probably Wont:
//  - Document API for currentText like other text components (for events, etc.)
//  - Caret API (dot, blink rate, etc.) like other components
//  - Events about when specific bubbles are added/removed
//  - Turning trim bubbles off affects previous bubbles (would have to keep all input chars in sep buffer)
//  - Paint divider character in margin so it's obvious ?  setShowDividerChars
//  - Full rectangles get little "X" to click to remove? setEnableDeleteButtons

public class BubbleTextField extends RPanel {


    ///////////
    // ENUMS //
    ///////////

    private enum Anchor {
        LEFT,
        RIGHT
    }


    ////////////
    // FIELDS //
    ////////////

    // Constants

    private static final int TEXT_FIELD_MARGIN_X = 2;  // Distance from left/right edges of component (not of border)
    private static final int TEXT_FIELD_MARGIN_Y = 2;  // Distance from top/bottom edges of component (not of border)
    private static final int BUBBLE_PADDING_X    = 3;  // Bubble padding is space between
    private static final int BUBBLE_PADDING_Y    = 2;  // bubble text and bubble borders
    private static final int BUBBLE_SPACING      = 2;  // Space between bubbles or between
                                                       // a bubble and the current text
    private static final int ROUNDED_SIZE = 13;
    private static final int CURSOR_WIDTH = 1;

    private static Color DEFAULT_BUBBLE_BG_COLOR       = Lay.clr("FFD8D8");   // Light red
    private static Color DEFAULT_BUBBLE_FG_COLOR       = Lay.clr("333333");   // Dark gray
    private static Color DEFAULT_BUBBLE_DEBUG_FG_COLOR = Color.red;
    private static Color DEFAULT_BUBBLE_BORDER_COLOR   = Color.red;
    private static Color SELECTED_BG_COLOR             = Lay.clr("B8CFE5");
    private static Color SELECTED_BORDER_COLOR         = Lay.clr("6382BF");

    private static int BLINK_SPEED = 500;  // ms

    private static List<Color[]> rotatingColors = new ArrayList<>();  // Defined lower

    // Principal Content
    private String currentText = "";
    private List<String> bubbles = Collections.synchronizedList(new ArrayList<String>());

    // Configuration
    private String bubbleChars = ";,";
    private boolean trimBubbles = false;
    private BubbleReplacer replacer;
    private BubbleValidator validator;

    // Colors
    private Color bubbleBgColor = DEFAULT_BUBBLE_BG_COLOR;
    private Color bubbleBorderColor = DEFAULT_BUBBLE_BORDER_COLOR;
    private Color bubbleDebugFgColor = DEFAULT_BUBBLE_DEBUG_FG_COLOR;
    private Color bubbleFgColor = DEFAULT_BUBBLE_FG_COLOR;
    private boolean useRotatingColors = false;

    // Cursor / Selected Bubble
    private int cursorPos = 0;            // cursor "dot"
    private int cursorMark = 0;
    private Anchor anchor = Anchor.LEFT;
    private int anchorPos = 0;
    private int leftShift = 0;

    // Internal
    private boolean errorState = false;
    private boolean hasFocus = false;
    private Timer blinkTimer;
    private Timer errorStateTimer;
    private boolean hideCursor = false;
    private int contentWidth = 0;
    private boolean repaintUpdateWidth = true;
    private boolean repaintUpdateAnchor = true;
    private boolean debug = false;
    private boolean editable = true;
    private int textHeight = -1;

    private Map<Integer, Integer> pixelToPos = new LinkedHashMap<>();
    private Integer[] ptpKeys;
    private Integer[] ptpValues;

    static {
        rotatingColors.add(new Color[] {Lay.clr("FFD8D8"), Color.red});           // Red
        rotatingColors.add(new Color[] {Lay.clr("FFF3DB"), Lay.clr("FFAC47")});   // Yellow
        rotatingColors.add(new Color[] {Lay.clr("D6FFD6"), Lay.clr("71E288")});   // Green
        rotatingColors.add(new Color[] {Lay.clr("D8FDFF"), Lay.clr("67A9E0")});   // Teal
        rotatingColors.add(new Color[] {Lay.clr("D5D3FF"), Lay.clr("2119FF")});   // Blue
        rotatingColors.add(new Color[] {Lay.clr("FDC4FF"), Lay.clr("CE2DC9")});   // Purple
    }


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public BubbleTextField() {
        setBackground(Color.white);
        setFocusable(true);
        setPreferredSize(new Dimension(200, 30));
        setBorder(new JTextField().getBorder());
        setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                hasFocus = true;
                repaint();
                startBlink();
            }
            @Override
            public void focusLost(FocusEvent e) {
                createBubble(true);
                hasFocus = false;
                repaint();
                stopBlink();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                int pos = getPixelToPos(e.getX());
                if(pos != cursorPos) {
                    startBlink();
                }
                setCursorPosAndMark(pos);
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKey(e);
            }
        });
    }

    private int getPixelToPos(int pixelX) {
        pixelX += leftShift;
        for(int k = 0; k < ptpKeys.length; k++) {
            int posRightBoundary = ptpKeys[k];
            if(pixelX < posRightBoundary) {
                int pos = ptpValues[k];
                if(pos >= 0) {
                    int posRightBoundaryPrev = k > 0 ? ptpKeys[k - 1] : 0;
                    if(pixelX >= ((posRightBoundary - posRightBoundaryPrev) / 2 + posRightBoundaryPrev)) {
                        return pos + 1;
                    }
                }
                return pos;
            }
        }
        return currentText.length();
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getCurrentText() {
        return currentText;
    }
    public List<String> getBubbles() {
        return Collections.unmodifiableList(bubbles);   // TODO: Needs to be synchronized
    }
    public String getBubbleChars() {
        return bubbleChars;
    }
    public boolean isTrimBubbles() {
        return trimBubbles;
    }
    public BubbleReplacer getReplacer() {
        return replacer;
    }
    public BubbleValidator getValidator() {
        return validator;
    }
    public Color getBubbleFgColor() {
        return bubbleFgColor;
    }
    public Color getBubbleDebugFgColor() {
        return bubbleDebugFgColor;
    }
    public Color getBubbleBgColor() {
        return bubbleBgColor;
    }
    public Color getBubbleBorderColor() {
        return bubbleBorderColor;
    }
    public boolean isUseRotatingColors() {
        return useRotatingColors;
    }
    public boolean isDebug() {
        return debug;
    }
    public boolean isEditable() {
        return editable;
    }

    // Accessors (Computed)

    public boolean hasIncompleteBubble() {
        boolean all = true;
        int pos = all ? currentText.length() : cursorPos;

        if(pos <= 0) {
            return false;
        }

        String nextAdd = currentText.substring(0, pos);

        if(trimBubbles) {
            nextAdd = nextAdd.trim();
        }

        return !nextAdd.equals("") && !isValid(nextAdd);
    }

    // Mutators

    // Does not perform bubble character searching.
    // Anything happen to cursorPos?
    public void setCurrentText(String currentText) {
        this.currentText = currentText;
        repaintUpdate(true, false);
        fireChangeNotifier();
    }
    public void setBubbles(List<String> newBubbles) {
        bubbles.clear();
        if(newBubbles != null) {
            for(String bubble : newBubbles) {
                String add = trimBubbles ? bubble.trim() : bubble;
                ensureValid(add);
                bubbles.add(add);
            }
        }
        repaintUpdate(true, false);
        fireChangeNotifier();
    }
    public void addBubble(String bubble) {
        String add = trimBubbles ? bubble.trim() : bubble;
        ensureValid(add);
        bubbles.add(add);
        repaintUpdate(true, false);
        fireChangeNotifier();
    }
    public void insertBubble(int index, String bubble) {
        String add = trimBubbles ? bubble.trim() : bubble;
        ensureValid(add);
        bubbles.add(index, add);
        repaintUpdate(true, false);
        fireChangeNotifier();
    }
    public String removeBubble(int index) {
        String bubble = bubbles.remove(index);
        repaintUpdate(true, false);
        fireChangeNotifier();
        return bubble;
    }
    private void removeBubbles(int start, int endIncl) {
        for(int i = endIncl; i >= start; i--) {
            bubbles.remove(i);
        }
        repaintUpdate(true, false);
        fireChangeNotifier();
    }
    public void clearBubbles() {
        bubbles.clear();
        repaintUpdate(true, false);
        fireChangeNotifier();
    }
    // Anything happen with cursorPos?
    public String expandBubble(int bpos) {
        String bubble = bubbles.get(bpos);
        currentText = bubble + currentText;
        bubbles.remove(bpos);
        repaintUpdate(true, false);
        fireChangeNotifier();
        return bubble;
    }
    public BubbleTextField setBubbleChars(String bubbleChars) {
        this.bubbleChars = bubbleChars;
        // Don't need to repaint as this doesn't affect existing bubbles
        return this;
    }
    public BubbleTextField setTrimBubbles(boolean trimBubbles) {
        this.trimBubbles = trimBubbles;
        // Don't need to repaint as this doesn't affect existing bubbles
        return this;
    }
    public void setReplacer(BubbleReplacer replacer) {
        this.replacer = replacer;
        repaintUpdate(true, false);
    }
    public BubbleTextField setValidator(BubbleValidator validator) {
        this.validator = validator;
        // Don't need to repaint as this doesn't affect existing bubbles
        return this;
    }
    public BubbleTextField setBubbleFgColor(Color bubbleFgColor) {
        this.bubbleFgColor = bubbleFgColor;
        repaint();
        return this;
    }
    public BubbleTextField setBubbleDebugFgColor(Color bubbleDebugFgColor) {
        this.bubbleDebugFgColor = bubbleDebugFgColor;
        repaint();
        return this;
    }
    public BubbleTextField setBubbleBgColor(Color bubbleBgColor) {
        this.bubbleBgColor = bubbleBgColor;
        repaint();
        return this;
    }
    public BubbleTextField setBubbleBorderColor(Color bubbleBorderColor) {
        this.bubbleBorderColor = bubbleBorderColor;
        repaint();
        return this;
    }
    public BubbleTextField setUseRotatingColors(boolean useRotatingColors) {
        this.useRotatingColors = useRotatingColors;
        repaint();
        return this;
    }
    public BubbleTextField setDebug(boolean debug) {
        this.debug = debug;
        repaint();
        return this;
    }
    public BubbleTextField setEditable(boolean editable) {
        this.editable = editable;
        if(editable) {
            setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        repaint();
        return this;
    }

    // Mutators (Internal)

    private void setCursorPosAndMark(String bubble) {
        setCursorPosAndMark(bubble.length());
    }

    private void setCursorPosAndMark(int c) {
        cursorPos = c;
        cursorMark = cursorPos;
        repaintUpdate(false, true);
    }

    private void setCursorPosAndMark(int c, int m) {
        cursorPos = c;
        cursorMark = m;
        repaintUpdate(false, true);
    }

    private void decrementCursorPosAndMark() {
        if(cursorPos > -bubbles.size()) {
            cursorPos--;
            cursorMark = cursorPos;
            repaintUpdate(false, true);

        } else if(cursorMark != cursorPos) {
            cursorMark = cursorPos;
            repaintUpdate(false, true);
        }
    }

    private void incrementCursorPosAndMark() {
        if(cursorPos < currentText.length()) {
            cursorPos++;
            cursorMark = cursorPos;
            repaintUpdate(false, true);

        } else if(cursorMark != cursorPos) {
            cursorMark = cursorPos;
            repaintUpdate(false, true);
        }
    }

    private void decrementCursorPos() {
        if(cursorPos > -bubbles.size()) {
            cursorPos--;
            repaintUpdate(false, true);
            // Leave behind mark
        }
    }

    private void incrementCursorPos() {
        if(cursorPos < currentText.length()) {
            cursorPos++;
            repaintUpdate(false, true);
            // Leave behind mark
        }
    }

    // Mutators (Other)

    public void rebubble() {
        // TODO: according to current 1) bubble chars 2) trim bubbles and 3) validator (maybe)
        // Would have to keep the exact bubble characters used to make this work...
        repaint();
    }


    /////////////
    // REPAINT //
    /////////////

    private void repaintUpdate(boolean w, boolean a) {
        repaintUpdateWidth = repaintUpdateWidth || w;
        repaintUpdateAnchor = repaintUpdateAnchor || a;
        repaint();
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

//    @Override          TODO: not sure how to do this yet...
//    public Dimension getPreferredSize() {
//        Dimension dim = super.getPreferredSize();
//        Insets insets = getInsets();
//        dim.height =
//            textHeight +                 // Text
//            BUBBLE_PADDING_Y * 2 +       // Bubble
//            insets.top + insets.bottom;  // Border
//        return dim;
////        ps.width = contentWidth;
////        return ps;
////        System.out.println(textHeight);
////        return super.getPreferredSize();
//    }

    //wwwwwwwwwasdflskfrrrrr[rrr] repeatable test case of failure!!!!!!
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        //
        if(debug) {
            DrawUtil.grid(g2, getWidth(), getHeight());
        }

        // If the contents of the text field have changed in any way,
        // via addition or removal of a bubble, or the changing of the
        // current text, then this flag will be set and the new width
        // of the new content should be recalculated.
        if(repaintUpdateWidth) {
            contentWidth = calcWidth(g2);
            repaintUpdateWidth = false;
        }

        // If the cursor has changed position in any way, via an
        // increment, decrement, home key, end key, or mouse click,
        // make sure that the cursor is still visible within the
        // text field.  If it is not visible, update an anchor which
        // will make it visible again.
        if(repaintUpdateAnchor) {
            int w = getWidth();
            int wMinusBorder = w - 2 * TEXT_FIELD_MARGIN_X;

            if(contentWidth <= wMinusBorder) {
                anchorPos = 0;
                anchor = Anchor.LEFT;
                int newLeftShift = 0;
                if(newLeftShift != leftShift) {
                    System.out.println("LS: " + leftShift + " => " + newLeftShift);
                }
                leftShift = newLeftShift;

            } else {
                int prevLeftShift = leftShift;
                int cursorPixelPos = calcWidthBefore(g2, cursorPos);

                if(cursorPixelPos - prevLeftShift > wMinusBorder) {
                    anchorPos = cursorPos;
                    anchor = Anchor.RIGHT;

                } else if(cursorPixelPos - prevLeftShift < 0) {
                    anchorPos = cursorPos;
                    anchor = Anchor.LEFT;

                }
            }
            repaintUpdateAnchor = false;
//            System.out.println("PAINT AFTER anchor=[" + anchor + "] " + anchorPos);
        }

        if(anchorPos > getMaxCursorPos()) {
            anchorPos = getMaxCursorPos();
        }
        int newLeftShift = calculateLeftShift(g2);
        if(newLeftShift != leftShift) {
//            System.out.println("LS: " + leftShift + " => " + newLeftShift);
        }
        leftShift = newLeftShift;

        // Now that the content width, anchors, and left shift have been
        // possibly updated above, draw the contents of the text field
        // onto the component.
        draw(g2);
    }

    private int calcWidthBefore(Graphics2D g2, int stopAtPos) {
        return calcOrDrawGetWidth(g2, false, stopAtPos);
    }
    private int calcWidth(Graphics2D g2) {
        return calcOrDrawGetWidth(g2, false, null);
    }
    private void draw(Graphics2D g2) {
        calcOrDrawGetWidth(g2, true, null);
    }

    private int calcOrDrawGetWidth(Graphics2D g2, boolean draw, Integer stopAtPos) {
        if(stopAtPos != null) {
            draw = false;           // Make sure no drawing when stopping at a position
        }

        // DRAWING //
        if(draw) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        }
        // DRAWING //

        int newTH = GuiUtil.stringHeight(g2);
        if(newTH != textHeight) {
//            revalidate();   TODO: working on get preferred height now
        }
        textHeight = newTH;

        int TEXT_FIELD_TO_TEXT = getHeight() / 2 - Math.round(textHeight / 2.0F);

        FontMetrics fm = g2.getFontMetrics(g2.getFont());

        int newContentWidth = 0;             // Needed pixel width to draw contents

        pixelToPos.clear();

        for(int b = 0;
                b < bubbles.size() &&
                    (stopAtPos == null || b - bubbles.size() < stopAtPos); b++) {

            String bubble = bubbles.get(b);

            // Replace completed bubbles with their replacement text
            if(replacer != null) {
                String newBubble = replacer.replaceComplete(bubble);
                if(newBubble != null) {
                    bubble = newBubble;
                }
            }

            int bw = GuiUtil.stringWidth(g2, bubble);   // Bubble Width
            int bwp = bw + BUBBLE_PADDING_X * 2;        // Bubble Width w/ Padding

            // DRAWING //
            if(draw) {
                Color bgColor, bdrColor;

                // Choose the bubble background and border colors
                boolean selected;
                if(cursorMark != cursorPos) {
                    selected = (cursorMark < 0 || cursorPos < 0);
                    if(selected) {
                        int startIdx;
                        int endIdx;
                        if(cursorMark < cursorPos) {
                            startIdx = Math.max(getMinCursorPos(), cursorMark);
                            endIdx = Math.min(-1, cursorPos);
                        } else {
                            startIdx = Math.max(getMinCursorPos(), cursorPos);
                            endIdx = Math.min(-1, cursorMark);
                        }
                        int cp = bubbleIdxToCursorPos(b);
                        selected = cp >= startIdx && cp <= endIdx;
                    }
                } else {
                    selected = cursorPos < 0 && bubbleIdxToCursorPos(b) == cursorPos;
                }
                if(selected) {
                    bgColor = SELECTED_BG_COLOR;
                    bdrColor = SELECTED_BORDER_COLOR;

                } else if(useRotatingColors) {
                    Color[] clrs = rotatingColors.get(b % rotatingColors.size());
                    bgColor = clrs[0];
                    bdrColor = clrs[1];

                } else {
                    bgColor = bubbleBgColor;
                    bdrColor = bubbleBorderColor;
                }

                // Set color for bubble background
                if(debug) {
                    Color tBgColor = new Color(
                        bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 200);
                    g2.setColor(tBgColor);
                } else {
                    g2.setColor(bgColor);
                }

                // Draw bubble background
                g2.fillRoundRect(
                    TEXT_FIELD_MARGIN_X + newContentWidth - leftShift,
                    TEXT_FIELD_TO_TEXT - BUBBLE_PADDING_Y,   // This will have to change if the text field is way larger than the bubbles height
                    bwp,
                    textHeight + BUBBLE_PADDING_Y * 2,
                    ROUNDED_SIZE,
                    ROUNDED_SIZE
                );

                // Set color for bubble border
                if(debug) {
                    Color tBgColor = new Color(
                        bdrColor.getRed(), bdrColor.getGreen(), bdrColor.getBlue(), 200);
                    g2.setColor(tBgColor);
                } else {
                    g2.setColor(bdrColor);
                }

                // Draw bubble border
                g2.drawRoundRect(
                    TEXT_FIELD_MARGIN_X + newContentWidth - leftShift,
                    TEXT_FIELD_TO_TEXT - BUBBLE_PADDING_Y,
                    bwp - 1,                              // -1 due to Graphics API
                    textHeight + BUBBLE_PADDING_Y * 2 - 1,    // Review height in depth eventually
                    ROUNDED_SIZE,
                    ROUNDED_SIZE
                );

                // Set color for bubble text
                if(debug) {
                    g2.setColor(bubbleDebugFgColor);
                } else {
                    g2.setColor(bubbleFgColor);
                }

                // Draw the bubble text
                g2.drawString(
                    bubble,
                    TEXT_FIELD_MARGIN_X + newContentWidth + BUBBLE_PADDING_X - leftShift,
                    TEXT_FIELD_TO_TEXT + fm.getAscent());
            }
            // DRAWING //

            newContentWidth += bwp + BUBBLE_SPACING;

            pixelToPos.put(newContentWidth, bubbleIdxToCursorPos(b));

        } // End of loop

        // DRAWING //
        if(draw) {

            // Draw any selection in the current text
            if((cursorMark != cursorPos && (cursorMark > 0 || cursorPos > 0))) {
                int startIdx;
                int endIdx;
                if(cursorMark < cursorPos) {
                    startIdx = Math.max(0, cursorMark);
                    endIdx = cursorPos;
                } else {
                    startIdx = Math.max(0, cursorPos);
                    endIdx = cursorMark;
                }
//                System.out.printf("m=%d p=%d%n", cursorMark, cursorPos);
                String highlightedText = currentText.substring(startIdx, endIdx);  // OOB ex here
                String beforeHT = currentText.substring(0, startIdx);
                int bhw = GuiUtil.stringWidth(g2, beforeHT);
                int hw = GuiUtil.stringWidth(g2, highlightedText);
                g2.setColor(SELECTED_BG_COLOR);
                g2.fillRect(
                    TEXT_FIELD_MARGIN_X + newContentWidth + bhw - leftShift,
                    TEXT_FIELD_TO_TEXT,            // TODO
                    hw + 1,       // JTextField apparently likes to extend the selection box by 1 pixel into the next character's space
                    textHeight);
            }

            // Set the color for the current text
            if(errorState || debug) {
                g2.setColor(bubbleDebugFgColor);
            } else {
                g2.setColor(getForeground());
            }

            // Draw the current text
            g2.drawString(
                currentText,
                TEXT_FIELD_MARGIN_X + newContentWidth - leftShift,
                TEXT_FIELD_TO_TEXT + fm.getAscent());

            if(hasFocus) {
                if(cursorPos >= 0 && !hideCursor && editable) {

                    // Determine position of the cursor
                    String leftOfCursorText = currentText.substring(0,
                        Math.min(cursorPos, currentText.length()));
                    int w2 = GuiUtil.stringWidth(g2, leftOfCursorText);
                    int curLeft = w2 + newContentWidth;

                    // Set color for cursor
                    if(debug) {
                        g2.setColor(Color.green);
                    } else {
                        g2.setColor(Color.black);
                    }

                    // Draw the cursor
                    int curX = TEXT_FIELD_MARGIN_X + curLeft - leftShift;
                    g2.drawLine(
                        curX,
                        TEXT_FIELD_TO_TEXT,
                        curX,
                        TEXT_FIELD_TO_TEXT + textHeight - 1);
                }
            }
        }
        // DRAWING //

        int tempWidth = newContentWidth;
        for(int c = 0; c < currentText.length(); c++) {
            int chw = GuiUtil.stringWidth(g2, "" + currentText.charAt(c));
            tempWidth += chw;
            pixelToPos.put(tempWidth, c);
        }
        ptpKeys = pixelToPos.keySet().toArray(new Integer[0]);
        ptpValues = pixelToPos.values().toArray(new Integer[0]);

        // If we want the entire length calculated, add all the pixels
        // required by the entire current text.
        if(stopAtPos == null) {
            int cw = GuiUtil.stringWidth(g2, currentText);    // Current Text Width
            newContentWidth += cw;

            // Add cursor width.
            newContentWidth += CURSOR_WIDTH;

        // Else if we want to stop at a certain position within the
        // current text, only add the pixels of those characters.
        } else if(stopAtPos > 0) {
            String leftOfPosText = currentText.substring(0, stopAtPos);
            int cwPartial = GuiUtil.stringWidth(g2, leftOfPosText);
            newContentWidth += cwPartial;
            // Do not add cursor because that is unrelated to the
            // purpose of the method call when stopAtPos is provided.

        }
        // else if stopAtPos <= 0 then a bubble is highlighted or cursor at
        // beginning of current text so don't add anything.

        return newContentWidth;
    }

    private int calculateLeftShift(Graphics2D g2) {
        int w = getWidth();
        int wMinusBorder = w - 2 * TEXT_FIELD_MARGIN_X;

        // Content does not exceed width so no need to shift.
        if(contentWidth <= wMinusBorder) {
            return 0;
        }

        if(anchor == Anchor.LEFT) {
            return calcWidthBefore(g2, anchorPos);
        }

        int startX = calcWidthBefore(g2, anchorPos);
        return startX - wMinusBorder + 1;
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    // More notifiers later perhaps?

    private ChangeNotifier changeNotifier = new ChangeNotifier(this);
    public void addChangeListener(ChangeListener listener) {
        changeNotifier.addListener(listener);
    }
    private void fireChangeNotifier() {
        changeNotifier.fireStateChanged();
    }

    private ChangeNotifier actionNotifier = new ChangeNotifier(this);
    public void addActionListener(ChangeListener listener) {
        actionNotifier.addListener(listener);
    }
    private void fireActionNotifier() {
        actionNotifier.fireStateChanged();
    }

    private ChangeNotifier msgNotifier = new ChangeNotifier(this);

    public void addMsgListener(ChangeListener listener) {
        msgNotifier.addListener(listener);
    }

    private void fireMessage(String hi) {
        msgNotifier.setSource(hi);
        msgNotifier.fireStateChanged();
    }


    //////////
    // MISC //
    //////////

    public void check() {
        createBubble(true);
    }

    private void createBubble(boolean all) {
        int pos = all ? currentText.length() : cursorPos;

        if(pos <= 0) {
            return;
        }

        String nextAdd = currentText.substring(0, pos);

        if(trimBubbles) {
            nextAdd = nextAdd.trim();
        }

        if(!nextAdd.equals("")) {
            if(!isValid(nextAdd)) {
                errorState = true;

                if(errorStateTimer != null) {
                    errorStateTimer.cancel();
                    errorStateTimer = null;
                }

                errorStateTimer = new Timer("BubbleTextField.ErrorStateTimer", true);

                errorStateTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        errorState = false;
                        GuiUtil.safeSync(new Runnable() {
                            @Override
                            public void run() {
                                repaint();
                            }
                        });
                    }
                }, 1000);

                repaint();
            } else {
                bubbles.add(nextAdd);
                currentText = currentText.substring(pos);
                cursorPos = 0;
                cursorMark = 0;

                repaintUpdate(true, true);
                fireChangeNotifier();
            }
        }
    }

    private void ensureValid(String bubble) {
        if(!isValid(bubble)) {
            throw new IllegalArgumentException("Bubble content is not valid");
        }
    }

    private boolean isValid(String bubble) {
        return validator == null || validator.bubbleValid(bubble);
    }

    private void startBlink() {
        stopBlink();

        blinkTimer = new Timer("BubbleTextField.BlinkTimer", true);

        hideCursor = false;
        repaint();

        blinkTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                hideCursor = !hideCursor;
                GuiUtil.safeSync(() -> repaint());
            }
        }, BLINK_SPEED, BLINK_SPEED);
    }

    private void stopBlink() {
        if(blinkTimer != null) {
            blinkTimer.cancel();
            blinkTimer = null;
        }
        hideCursor = true;
        repaint();
    }

    private int getMinCursorPos() {
        return -bubbles.size();
    }
    private int getMaxCursorPos() {
        return currentText.length();
    }
    private int bubbleIdxToCursorPos(int pos) {
        return pos - bubbles.size();
    }


    ////////////////
    // HANDLE KEY //
    ////////////////

    private void handleKey(KeyEvent e) {
        if(!editable) {
            return;       // Kind of over-reaching at this point but it works.
        }

//        System.out.println("BEFORE anchor=[" + anchor + "] " + anchorPos);
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_BACK_SPACE) {

            if(cursorMark != cursorPos) {
                removeSelected();
            } else {
                if(cursorPos <= 0) {
                    if(!bubbles.isEmpty()) {
                        int bpos = cursorPos == 0 ?
                            bubbles.size() - 1 :
                            cursorPos + bubbles.size();
                        String bubble = expandBubble(bpos);
                        if(anchor == Anchor.RIGHT && anchorPos == cursorPos) {
                            anchorPos = bubble.length();
                        }
                        setCursorPosAndMark(bubble);
                    }

                } else {
                    setCurrentText(
                        currentText.substring(0, cursorPos - 1) +
                        currentText.substring(cursorPos)
                    );
                    decrementCursorPosAndMark();
                }
            }

        } else if(code == KeyEvent.VK_DELETE) {

            if(cursorMark != cursorPos) {
                removeSelected();
            } else {
                if(cursorPos >= 0 && cursorPos < currentText.length()) {
                    setCurrentText(
                        currentText.substring(0, cursorPos) +
                        currentText.substring(cursorPos + 1)
                    );

                } else if(cursorPos < 0) {
                    int bpos = cursorPos + bubbles.size();
                    removeBubble(bpos);
                    incrementCursorPosAndMark();
                }
            }

        } else if(code == KeyEvent.VK_LEFT) {
            if(e.isShiftDown()) {

                // Highlight one word to the left
                if(e.isControlDown()) {

                // Highlight one character to the left
                } else {
                    decrementCursorPos();
                }

            // Move cursor one word to the left
            } else if(e.isControlDown()) {

            // Move cursor one character to the left
            } else {
                decrementCursorPosAndMark();
            }

        } else if(code == KeyEvent.VK_RIGHT) {
            if(e.isShiftDown()) {

                // Highlight one word to the right
                if(e.isControlDown()) {

                // Highlight one character to the right
                } else {
                    incrementCursorPos();
                }

            // Move cursor one word to the right
            } else if(e.isControlDown()) {

            // Move cursor one character to the right
            } else {
                incrementCursorPosAndMark();
            }

        } else if(code == KeyEvent.VK_HOME) {
            setCursorPosAndMark(getMinCursorPos());

        } else if(code == KeyEvent.VK_END) {
            setCursorPosAndMark(getMaxCursorPos());

        } else if(code == KeyEvent.VK_A && e.isControlDown()) {
            selectAll();

        } else if(code == KeyEvent.VK_C && e.isControlDown()) {
            copy();

        } else if(code == KeyEvent.VK_X && e.isControlDown()) {
            cut();

        } else if(code == KeyEvent.VK_V && e.isControlDown()) {
            paste();

        } else if(
            !e.isAltDown() && (
            code == KeyEvent.VK_ENTER || Character.getType(e.getKeyChar()) != Character.UNASSIGNED)) {

//Map<Integer, String> map = new LinkedHashMap<>();
//map.put(0, "UNASSIGNED");
//map.put(1, "UPPERCASE_LETTER");
//map.put(2, "LOWERCASE_LETTER");
//map.put(3, "TITLECASE_LETTER");
//map.put(4, "MODIFIER_LETTER");
//map.put(5, "OTHER_LETTER");
//map.put(6, "NON_SPACING_MARK");
//map.put(7, "ENCLOSING_MARK");
//map.put(8, "COMBINING_SPACING_MARK");
//map.put(9, "DECIMAL_DIGIT_NUMBER");
//map.put(10, "LETTER_NUMBER");
//map.put(11, "OTHER_NUMBER");
//map.put(12, "SPACE_SEPARATOR");
//map.put(13, "LINE_SEPARATOR");
//map.put(14, "PARAGRAPH_SEPARATOR");
//map.put(15, "CONTROL");
//map.put(16, "FORMAT");
//map.put(18, "PRIVATE_USE");
//map.put(19, "SURROGATE");
//map.put(20, "DASH_PUNCTUATION");
//map.put(21, "START_PUNCTUATION");
//map.put(22, "END_PUNCTUATION");
//map.put(23, "CONNECTOR_PUNCTUATION");
//map.put(24, "OTHER_PUNCTUATION");
//map.put(25, "MATH_SYMBOL");
//map.put(26, "CURRENCY_SYMBOL");
//map.put(27, "MODIFIER_SYMBOL");
//map.put(28, "OTHER_SYMBOL");
//map.put(29, "INITIAL_QUOTE_PUNCTUATION");
//map.put(30, "FINAL_QUOTE_PUNCTUATION");
//map.put(0xFFFFFFFF, "ERROR");
//
//            System.out.println(map.get(Character.getType(e.getKeyChar())));

            char ch = e.getKeyChar();

            if(bubbleChars.indexOf(ch) != -1) {
                createBubble(false);

            } else if(cursorPos >= 0) {
                setCurrentText(
                    currentText.substring(0, cursorPos) +
                    e.getKeyChar() +
                    currentText.substring(cursorPos)
                );
                incrementCursorPosAndMark();
            }

            if(code == KeyEvent.VK_ENTER) {
                fireActionNotifier();
            }
        }

//        System.out.println("AFTER anchor=[" + anchor + "] " + anchorPos);
    }

    private void selectAll() {
        setCursorPosAndMark(getMinCursorPos(), getMaxCursorPos());
    }

    private void paste() {
        int cursorMin = Math.min(cursorMark, cursorPos);
        if(cursorMin >= 0) {
            String val = ClipboardUtil.get();
            if(!val.isEmpty()) {
                int cursorMax = Math.max(cursorMark, cursorPos);
                String left = currentText.substring(0, cursorMin);
                String right = currentText.substring(cursorMax);
                setCurrentText(left + val + right);
                setCursorPosAndMark(cursorMark + val.length());
            }
        }
    }

    private void copy() {
        String result = "";
        String divider = bubbleChars.charAt(0) + " ";    // TODO: Configurable
        int cursorMin = Math.min(cursorMark, cursorPos);
        int cursorMax = Math.max(cursorMark, cursorPos);
        if(cursorMin < 0) {
            int cursorRight = Math.min(Math.min(-1, cursorMax), cursorMax);
            for(int i = cursorMin; i <= cursorRight; i++) {
                if(!result.isEmpty()) {
                    result += divider;
                }
                result += bubbles.get(i + bubbles.size());
            }
        }
        if(cursorMax > 0) {
            if(!result.isEmpty()) {
                result += divider;
            }
            result += currentText.substring(Math.max(0, cursorMin), cursorMax);
        }
        if(!result.isEmpty()) {
            StringSelection selection = new StringSelection(result);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
        }
    }

    private String removeSelected() {
        String result = "";
        String divider = bubbleChars.charAt(0) + " ";   // TODO: Configurable
        int cursorMin = Math.min(cursorMark, cursorPos);
        int cursorMax = Math.max(cursorMark, cursorPos);
        int bubRem = 0;
        if(cursorMin < 0) {
            int cursorRight = Math.min(Math.min(-1, cursorMax), cursorMax);
            for(int i = cursorMin; i <= cursorRight; i++) {
                if(!result.isEmpty()) {
                    result += divider;
                }
                result += bubbles.get(i + bubbles.size());
                bubRem++;
            }
            removeBubbles(cursorMin + bubbles.size(), cursorRight + bubbles.size());
        }
        if(cursorMax > 0) {
            if(!result.isEmpty()) {
                result += divider;
            }

            int cursorLeft = Math.max(0, cursorMin);
            String left = currentText.substring(0, cursorLeft);
            String mid = currentText.substring(cursorLeft, cursorMax);
            String right = currentText.substring(cursorMax);
            result += mid;
            setCurrentText(left + right);
        }
        if(!result.isEmpty()) {
            int newCursorPos = cursorMin < 0 ? cursorMin + bubRem : cursorMin;
            setCursorPosAndMark(newCursorPos, newCursorPos);
        }
        return result;
    }

    private void cut() {
        String result = removeSelected();
        if(!result.isEmpty()) {
            ClipboardUtil.set(result);
        }
    }


    public void focus() {
        requestFocusInWindow();
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {

        final BubbleTextField txt = new BubbleTextField()
            .setTrimBubbles(true)
            .setUseRotatingColors(true)
            .setBubbleChars(";\n")
            .setDebug(true)
        ;
        final BubbleTextField txtBub2;
//        Font larger = new Font("Arial", Font.PLAIN, 36);
//        txt.setFont(larger);

        final RTextArea txtLog;
        JTextField goaway;
        JCheckBox chkDebug;
        Lay.BLtg(Lay.fr("BubbleTextField Test"),
            "N", Lay.FL("L",
                txt,
                goaway = Lay.tx("go awayssssdlfjslfsjflakfjlkjlkj", "size=36"),
                new RButton("Do Something", new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
//                        txt.addBubble("asldfksja@asdf.com");
//                        txt.setBubbleBgColor(Color.yellow);
//                        txt.setBubbleBorderColor(Color.orange);
//                        txt.setUseRotatingColors(!txt.isUseRotatingColors());
                    }
                }),
                chkDebug = Lay.chk("Debug?", true, "opaque=false"),
                "bg=yellow"
            ),
            "C", Lay.BL(
                "N", Lay.lb("Events"),
                "C", Lay.GL(1, 2,
                    Lay.tx("test text alsfk jaslfk sflka sfalsk fjlsf jaslfkas flkafj aslkf sjkfl"),
                    txtBub2 = new BubbleTextField()
                )
//                "C", Lay.sp(txtLog = Lay.txa("", "editable=false"))
            ),
            "S", Lay.tx(""), //Lay.sp(new JList(new Object[] {"Mercury", "Mars"})),
            "size=600,visible=true,center"
        );
        txtLog = Lay.txa();
        chkDebug.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txt.setDebug(!txt.isDebug());
            }
        });
        Lay.hn(goaway, "dim=[100,30]");

        goaway.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("action performed!");
            }
        });

        txt.addMsgListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(!txtLog.getText().isEmpty()) {
                    txtLog.append("\n");
                }
                txtLog.append("Message: " + (String) e.getSource());
            }
        });

        txt.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                String anchors = "[" +
                    (txt.anchor == Anchor.LEFT ? "L" : "R") +
                    txt.anchorPos +
                    "]"
                ;
                if(!txtLog.getText().isEmpty()) {
                    txtLog.append("\n");
                }
                txtLog.append("Changed: " + txt.getBubbles() + " {" + txt.getCurrentText() + "} " + anchors);
                txtLog.setCaretPosition(txtLog.getText().length());
            }
        });
        txt.addActionListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                txtLog.append("\nAction Occurred");
            }
        });

//        txt.addBubble("bob@my.com");
        txt.insertBubble(0, "tony@who.com");
        txtBub2.addBubble("bob@my.com");
//        txtBub2.insertBubble(0, "tony@who.com");
//        Font larger = new Font("Arial", Font.PLAIN, 36);
        Lay.hn(txtBub2, "size=36");
//        txtBub2.setFont(larger);

        txtBub2.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
//                System.out.println(txtBub2.leftShift);
            }
        });

        final Map<String, String> replacements = new HashMap<>();
        replacements.put("tony@who.com", "gyja");
        replacements.put("bob@my.com", "Tii");

        txt.setReplacer(new BubbleReplacer() {
            @Override
            public String replaceComplete(String input) {
                return replacements.get(input);
            }
        });
//        txt.setValidator(new BubbleValidator() {
//            public boolean bubbleValid(String bubble) {
//                return bubble.matches("[a-z]+@[a-z]+\\.[a-z]+");
//            }
//        });
    }
}
