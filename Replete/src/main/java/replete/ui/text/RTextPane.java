package replete.ui.text;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.Utilities;

import replete.text.StringUtil;
import replete.ui.SelectionStateSavable;
import replete.ui.lay.Lay;
import replete.ui.panels.SelectionState;
import replete.ui.windows.ExampleFrame;


// Class not done, but an example of what we could do to simplify the text pane.

public class RTextPane extends JTextPane implements SelectionStateSavable {


    ////////////
    // FIELDS //
    ////////////

    private DefaultHighlighter.DefaultHighlightPainter hlPainter;
    private Object hlKey;
    private boolean textualUneditable = false;  // false = default JTextPane behavior
    private boolean selectAllEnabled = false;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RTextPane() {
        init();
    }
    public RTextPane(StyledDocument doc) {
        super(doc);
        init();
    }
    public RTextPane(String s) {
        setText(s);
        init();
    }
    public RTextPane(String s, StyledDocument doc) {
        super(doc);
        setText(s);
        init();
    }
    private void init() {
//        hlPainter = new FullLinePainter(Lay.clr("E8F2FE"));
        hlPainter = new DefaultHighlighter.DefaultHighlightPainter(Lay.clr("E8F2FE"));
        //System.out.println(getFocusTraversalKeysEnabled());
//        setFocusTraversalKEeysEnabled(false);
        //System.out.println(getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        //setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, new HashSet<AWTKeyStroke>());

    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public boolean isSelectAll() {
        return selectAllEnabled;
    }

    public String getTrimmed() {
        return getText().trim();
    }

    public boolean isTextualUneditable() {
        return textualUneditable;
    }

    @Override
    public void setEditable(boolean editable) {
        super.setEditable(editable);
        updateEditableUI();
    }
    public void setShowCurrentLine(boolean show) {
        if(show) {
            addChangeListener(dListener);
            addCaretListener(cListener);
        } else {
            removeChangeListener(dListener);
            removeCaretListener(cListener);
        }
    }
    public void setCurrentLineColor(Color clr) {
        hlPainter = new DefaultHighlighter.DefaultHighlightPainter(clr);
        repaint();
    }
    public void setTextualUneditable(boolean normalUneditable) {
        textualUneditable = normalUneditable;
        updateEditableUI();
    }

    private void updateEditableUI() {
        if(textualUneditable) {
            if(isEditable()) {
                removeFocusListener(uneditableFocusFixer);
            } else {
                addFocusListener(uneditableFocusFixer);
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));  // Either case
        } else {
            removeFocusListener(uneditableFocusFixer);
            if(isEditable()) {
                setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            } else {
                setCursor(Cursor.getDefaultCursor());
            }
        }
    }
    public void focus() {
        requestFocusInWindow();
    }

    public void addChangeListener(DocumentListener listener) {
        getDocument().addDocumentListener(listener);
    }
    public void removeChangeListener(DocumentListener listener) {
        getDocument().removeDocumentListener(listener);
    }

    public RTextPane setSelectAll(boolean selectAllEnabled) {
        this.selectAllEnabled = selectAllEnabled;
        removeFocusListener(selectAllFocusListener);
        if(selectAllEnabled) {
            addFocusListener(selectAllFocusListener);
        }
        return this;
    }


    //////////
    // MISC //
    //////////

    private FocusListener selectAllFocusListener = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            selectAll();
        }
    };


    ///////////////
    // LISTENERS //
    ///////////////

    private FocusListener uneditableFocusFixer = new FocusListener() {
        @Override
        public void focusLost(FocusEvent e) {
            getCaret().setVisible(false);
        }
        @Override
        public void focusGained(FocusEvent e) {
            getCaret().setVisible(true);
        }
    };
    public void clear() {
        setText("");
    }
    private DocumentListener dListener = new DocumentChangeListener() {
        @Override
        public void documentChanged(DocumentEvent e) {
            updateHighlightLine(getCaretPosition());
        }
    };
    private CaretListener cListener = new CaretListener() {
        public void caretUpdate(CaretEvent e) {
            if(e.getDot() == e.getMark()) {
                updateHighlightLine(e.getDot());
            } else {
                updateHighlightLine(-1);
            }
        }
    };

    private void updateHighlightLine(int position) {
        try {
            // NOTE: If performance becomes a problem moving the caret, then
            // you can technically only update the highlight if the caret
            // has moved to another line.  You can do this by saving both the
            // previous update method's start and end positions and the
            // return key from addHighlight.
//            System.out.println(Arrays.toString(getHighlighter().getHighlights()));
            // TODO: would be nice if mouse highlighted text was always about current line highlight.
            if(hlKey != null) {
                getHighlighter().removeHighlight(hlKey);
                hlKey = null;
            }
            if(position != -1 && getText().length() != 0) {
                try {
                    int start = Utilities.getRowStart(this, position);
                    int end = Utilities.getRowEnd(this, position);
                    hlKey = getHighlighter().addHighlight(start, end, hlPainter);
                } catch(IllegalArgumentException e) {
                    // Can still happen for some as of yet unknown reason.
                }
            }
        } catch(BadLocationException e1) {
            // Can be thrown in weird circumstances, but without any apparent
            // negative affect.
        }
    }

//    private class FullLineHighglighter extends DefaultHighlighter {
//        @Override
//        public void paintLayeredHighlights(Graphics g, int p0, int p1, Shape viewBounds,
//                                           JTextComponent editor, View view) {
//            System.out.println("hi2");
//            super.paintLayeredHighlights(g, p0, p1, viewBounds, editor, view);
//        }
//        @Override
//        public void paint(Graphics g) {
//            System.out.println("paint");
//            super.paint(g);
//        }
//    }


//    private class FullLinePainter extends DefaultHighlighter.DefaultHighlightPainter {
//        public FullLinePainter(Color c) {
//            super(c);
//        }
//        public void setColor(Color clr) {
//            ReflectionUtil.set("color", this, clr);
//        }
//        @Override
//        public void paint(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c) {
//            System.out.println("paint");
//            Rectangle alloc = bounds.getBounds();
//            try {
//                // --- determine locations ---
//                TextUI mapper = c.getUI();
//                Rectangle p0 = mapper.modelToView(c, offs0);
//                Rectangle p1 = mapper.modelToView(c, offs1);
//
//                // --- render ---
//                Color color = getColor();
//
//                if(color == null) {
//                    g.setColor(c.getSelectionColor());
//                } else {
//                    g.setColor(color);
//                }
//                System.out.println(p0);
//                System.out.println(p1);
//                System.out.println();
//                if(p0.y == p1.y) {
//                    // same line, render a rectangle
//                    Rectangle r = p0.union(p1);
//                    g.fillRect(r.x, r.y, r.width, r.height);
//                } else {
//                    // different lines
//                    int p0ToMarginWidth = alloc.x + alloc.width - p0.x;
//                    g.fillRect(p0.x, p0.y, p0ToMarginWidth, p0.height);
//                    if((p0.y + p0.height) != p1.y) {
//                        g.fillRect(alloc.x, p0.y + p0.height, alloc.width,
//                            p1.y - (p0.y + p0.height));
//                    }
//                    g.fillRect(alloc.x, p1.y, (p1.x - alloc.x), p1.height);
//                }
//            } catch(BadLocationException e) {
//                e.printStackTrace();
//                // can't render
//            }
//        }
//
//        @Override
//        public Shape paintLayer(Graphics g, int offs0, int offs1,
//                                Shape bounds, JTextComponent c, View view) {
//System.out.println("paintLayer");
//            Color color = getColor();
//            if(color == null) {
//                g.setColor(c.getSelectionColor());
//            }
//            else {
//                g.setColor(color);
//            }
//            if(offs0 == view.getStartOffset() &&
//                offs1 == view.getEndOffset()) {
//                // Contained in view, can just use bounds.
//                Rectangle alloc;
//                if(bounds instanceof Rectangle) {
//                    alloc = (Rectangle) bounds;
//                }
//                else {
//                    alloc = bounds.getBounds();
//                }
//System.out.println("A1:"+alloc);
//                g.fillRect(alloc.x, alloc.y, alloc.width, alloc.height);
//                return alloc;
//            }
//            else {
//                // Should only render part of View.
//                try {
//                    // --- determine locations ---
//                    Shape shape = view.modelToView(offs0, Position.Bias.Forward,
//                        offs1, Position.Bias.Backward,
//                        bounds);
//                    Rectangle r = (shape instanceof Rectangle) ?
//                        (Rectangle) shape : shape.getBounds();
//System.out.println("R:"+r);
//                    g.fillRect(r.x, r.y, 300000,/*r.width,*/ r.height);
//                    return r;
//                } catch(BadLocationException e) {
//                    e.printStackTrace();
//                }
//            }
//            System.out.println("huh");
//            // Only if exception
//            return null;
//        }
//    }

    private boolean allowHorizScroll = false;
    public boolean isAllowHorizScroll() {
        return allowHorizScroll;
    }
    public void setAllowHorizScroll(boolean allow) {
        allowHorizScroll = allow;
        updateUI();
    }

    public void appendln(String str) {
        append(str + "\n");
    }
    public void appendln(String str, Font f) {
        append(str + "\n", f);
    }
    public void appendln(String str, Color clr) {
        append(str + "\n", clr);
    }
    public void append(String str) {
        appendDoc(getDocument(), str);
    }
    public void append(String str, Font f) {
        appendDoc(getDocument(), str, f);
    }
    public void append(String str, Color clr) {
        appendDoc(getDocument(), str, clr);
    }
    public void append(String str, Font f, Color clr) {
        appendDoc(getDocument(), str, f, clr);
    }

    public void appendHtmlLike(String str) {
        Pattern p = Pattern.compile("<(/)?([ubicf]+)(:[^>]+)?>");
        Matcher m = p.matcher(str);
        Color color = null;
        boolean u = false;
        boolean b = false;
        boolean i = false;
        String font = null;
//        System.out.println(str);
        int prev = -1;
//        System.out.println("u=" + u +",b=" + b + ",i=" + i + ",v=n/a,c=" + color);
        while(m.find()) {
            String part = str.substring(prev + 1, m.start());
//            System.out.println("[" + part + "]");
            Font f = getFont().deriveFont((b?Font.BOLD:0)|(i?Font.ITALIC:0));
            if(font != null) {
                f = new Font(font, f.getStyle(), f.getSize());
            }
            appendDoc(getDocument(), part, f, color, u);

            prev = m.start() + m.group().length() - 1;

            boolean on = m.group(1) == null;
            if(m.group(2).contains("u")) {
                u = on;
            }
            if(m.group(2).contains("b")) {
                b = on;
            }
            if(m.group(2).contains("i")) {
                i = on;
            }
            if(m.group(2).contains("c")) {
                if(m.group(3) != null && on) {
                    color = Lay.clr(StringUtil.snip(m.group(3), 1));
                } else {
                    color = null;
                }
            }
            if(m.group(2).contains("f")) {
                if(m.group(3) != null && on) {
                    font = StringUtil.snip(m.group(3), 1);
                } else {
                    font = null;
                }
            }
//            System.out.println("u=" + u +",b=" + b + ",i=" + i + ",v=" + on + ",c=" + color);

        }
        String part = str.substring(prev + 1);
//        System.out.println("[" + part + "]");
        Font f = getFont().deriveFont((b?Font.BOLD:0)|(i?Font.ITALIC:0));
        if(font != null) {
            f = new Font(font, f.getStyle(), f.getSize());
        }
        appendDoc(getDocument(), part, f, color, u);
    }

    // To allow horizontal scroll bar, overriding JTextPane
    // default functionality of always wrapping.

    @Override
    public boolean getScrollableTracksViewportWidth() {
        if(allowHorizScroll) {
            return (getSize().width < getParent().getSize().width);
        }
        return super.getScrollableTracksViewportWidth();
    }
    @Override
    public void setSize(Dimension d) {
        if(allowHorizScroll) {
            if (d.width < getParent().getSize().width) {
                d.width = getParent().getSize().width;
            }
        }
        super.setSize(d);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // TODO: Add "creation methods" for only reselecting
    // the text if the text hasn't changed.  Only
    // behavior right now simply save/restores the
    // start/end indicies without looking at the
    // actual text that was selected.
    @Override
    public SelectionState getSelectionState() {
        return new SelectionState()
            .p("selStart", getSelectionStart())
            .p("selEnd", getSelectionEnd())
        ;
    }

    // NOTE: You won't actually see the text highlight until
    // the text area regains the focus!
    //
    // NOTE: This method may seem to change its parent JScrollPane
    // but it's actually later on a asynchronous repaint event
    // on the EDT where the cursor is required to be visible in
    // the scroll pane and the scroll pane is adjusted accordingly.
    // This is a little unintuitive, since the user may have scrolled
    // the cursor ("mark") out of the view after selecting some text,
    // which is just fine.  But this selection state save/restore
    // doesn't yet fully account for that nuance of the Swing API.
    @Override
    public void setSelectionState(SelectionState state) {
        select(state.getGx("selStart"), state.getGx("selEnd"));
        // ^Will silently modify the indicies internally so they're in bounds.
    }

//    public void printSelectionInfo() {
//        System.out.println("CAR: DOT=" + getCaret().getDot());
//        System.out.println("CAR: MRK=" + getCaret().getMark());
//        System.out.println("CAR: MCP=" + getCaret().getMagicCaretPosition());
//        System.out.println("CAR: SVS=" + getCaret().isSelectionVisible());
//        System.out.println("CAR: VIS=" + getCaret().isVisible());
//
//        System.out.println("CRP: " + getCaretPosition());
//        System.out.println("STX: " + getSelectedText());
//        System.out.println("SST: " + getSelectionStart());    // Math.min(caret.getDot(), caret.getMark());
//        System.out.println("SED: " + getSelectionEnd());      // Math.max(caret.getDot(), caret.getMark());
//    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        ExampleFrame f = new ExampleFrame();
        final RTextPane txt = new RTextPane();
        //txt.setText("alsdfk alsdf jlsaf laskd fjlksafklsad kflas jfklsa flksdafj ksda flasd jfl jadslf alsd fjldajfk jdkflsjlkfa sf sf jks jflkd jf jalsf jakdj flas fksd fsj fk sjdafjlads flkadj lf jfadjkfksal fkad jfadsj lkf asfjsajfas kfdj asdlfj lasd jfls lfdkf lask\n");
        txt.appendHtmlLike("derek<f:Courier New><u>safd<b>lskd</f></u>kjh<c:red>khkj<i>  lkjl jlkj  </i>   M</b></c>ddiejfj<ubi>sdfs</b>afafa</ui>");
        JButton btnDo = new JButton("do");
        btnDo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
//                txt.setAllowHorizScroll(!txt.isAllowHorizScroll());
                txt.append("alsdkfj lkjalsfkjsalkfas lflfjslkf jlsf\n", new Font("Courier New", Font.BOLD, 24));
            }
        });
        Lay.BLtg(f, "C", Lay.sp(txt), "S", Lay.FL(btnDo), "eb=5");
        f.setVisible(true);
    }


    ///////////////////
    // APPEND TO DOC //
    ///////////////////

    private static Map<String, Style> styles = new HashMap<>();

    public static void appendDoc(Document doc, String str) {
        try {
            doc.insertString(doc.getLength(), str, null);
        } catch(BadLocationException e) {}
    }

//    public void setAllStyleSizes(int size) {
////        for(Style style : styles.values()) {
////            StyleConstants.setFontSize(style, size);
////        }
//        Document doc = getDocument();
//        Element[] e = doc.getRootElements();
//        for(Element es : e) {
//            change(es, size);
//        }
//    }
//
//    private void change(Element es, int size) {
//        AttributeSet as = es.getAttributes();
//        if(as instanceof MutableAttributeSet) {
//            StyleConstants.setFontSize((MutableAttributeSet) as, size);
//            System.out.println("changed!");
//        }
//        for(int i = 0; i < es.getElementCount(); i++) {
//            change(es.getElement(i), size);
//        }
//    }

    public static void appendDoc(Document doc, String str, Font f) {
        String code = f.getName() + "|" + f.getSize() + "|" + f.getStyle();
        Style st = styles.get(code);
        if(st == null) {
            Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
            st = ((StyledDocument) doc).addStyle(code, def);
            StyleConstants.setFontFamily(st, f.getFamily());
            StyleConstants.setFontSize(st, f.getSize());
            if(f.isBold()) {
                StyleConstants.setBold(st, true);
            }
            if(f.isItalic()) {
                StyleConstants.setItalic(st, true);
            }
            styles.put(code, st);
        }
        try {
            doc.insertString(doc.getLength(), str, st);
        } catch(BadLocationException e) {}
    }
    public static void appendDoc(Document doc, String str, Color clr) {
        String code = Lay.clr(clr);
        Style st = styles.get(code);
        if(st == null) {
            Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
            st = ((StyledDocument) doc).addStyle(code, def);
            StyleConstants.setForeground(st, clr);
            styles.put(code, st);
        }
        try {
            doc.insertString(doc.getLength(), str, st);
        } catch(BadLocationException e) {}
    }
    public static void appendDoc(Document doc, String str, Font f, Color clr) {
        appendDoc(doc, str, f, clr, false);
    }
    public static void appendDoc(Document doc, String str, Font f, Color clr, boolean underline) {
        String fcode = f.getName() + "|" + f.getSize() + "|" + f.getStyle() + "|" + underline;
        String ccode = clr == null ? "null" : Lay.clr(clr);
        String code = fcode + "|" + ccode;
        Style st = styles.get(code);
        if(st == null) {
            Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
            st = ((StyledDocument) doc).addStyle(code, def);
            StyleConstants.setFontFamily(st, f.getFamily());
            StyleConstants.setFontSize(st, f.getSize());
            if(underline) {
                StyleConstants.setUnderline(st, true);
            }
            if(f.isBold()) {
                StyleConstants.setBold(st, true);
            }
            if(f.isItalic()) {
                StyleConstants.setItalic(st, true);
            }
            if(clr != null) {
                StyleConstants.setForeground(st, clr);
            }
//            StyleConstants.setLeftIndent(st, 10);
            styles.put(code, st);
        }
        try {
            doc.insertString(doc.getLength(), str, st);
        } catch(BadLocationException e) {}
    }
}
