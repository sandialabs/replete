package replete.ui.text;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import replete.event.ChangeNotifier;
import replete.numbers.NumUtil;
import replete.text.StringUtil;
import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeFrame;

public class RTextField extends JTextField {


    ////////////
    // FIELDS //
    ////////////

    private String errorMessage = null;
    private boolean selectAllEnabled = false;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RTextField() {
        super();
        init();
    }
    public RTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
        init();
    }
    public RTextField(int columns) {
        super(columns);
        init();
    }
    public RTextField(String text, int columns) {
        super(text, columns);
        init();
    }
    public RTextField(String text) {
        super(text);
        init();
    }

    private void init() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                    if(getCaretPosition() == 0) {
                        fireHitLeftNotifier();
                    }
                } else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if(getCaretPosition() == getTextLength()) {
                        fireHitRightNotifier();
                    }
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if(errorMessage != null) {
                    updateToolTip(e.getX(), e.getY());
                }
            }
        });
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    @Override
    public Point getToolTipLocation(MouseEvent event) {
        if(errorMessage != null) {
            int w = 16;              // Icon width
            int h = 16;              // Icon height
            int rightMargin = 4;     //
            int ix = getWidth() - w - rightMargin;
            int iy = (getHeight() - h) / 2;
            int mx = event.getX();
            int my = event.getY();
            if(mx >= ix && mx < ix + w && my >= iy && my < iy + h) {
                return new Point(mx + 10, my);
            }
        }
        return super.getToolTipLocation(event);
    }
    @Override
    public String getToolTipText(MouseEvent event) {
        if(errorMessage != null) {
            int w = 16;              // Icon width
            int h = 16;              // Icon height
            int rightMargin = 4;     //
            int ix = getWidth() - w - rightMargin;
            int iy = (getHeight() - h) / 2;
            int mx = event.getX();
            int my = event.getY();
            if(mx >= ix && mx < ix + w && my >= iy && my < iy + h) {
                return errorMessage;
            }
        }
        return null;
    }

    protected void updateToolTip(int mx, int my) {
        int w = 16;              // Icon width
        int h = 16;              // Icon height
        int rightMargin = 4;     //
        int ix = getWidth() - w - rightMargin;
        int iy = (getHeight() - h) / 2;
        if(mx >= ix && mx < ix + w && my >= iy && my < iy + h) {
//            setToolTipText(errorMessage);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else {
//            setToolTipText(null);
            setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        }
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    private ChangeNotifier hitLeftNotifier = new ChangeNotifier(this);
    public void addHitLeftListener(ChangeListener listener) {
        hitLeftNotifier.addListener(listener);
    }
    private void fireHitLeftNotifier() {
        hitLeftNotifier.fireStateChanged();
    }

    private ChangeNotifier hitRightNotifier = new ChangeNotifier(this);
    public void addHitRightListener(ChangeListener listener) {
        hitRightNotifier.addListener(listener);
    }
    private void fireHitRightNotifier() {
        hitRightNotifier.fireStateChanged();
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isSelectAll() {
        return selectAllEnabled;
    }

    // Accessors (Computed)

    public String getTrimmed() {
        return getText().trim();
    }
    public Integer getInteger() {
        return NumUtil.i(getText());
    }
    public Long getLong() {
        return NumUtil.l(getText());
    }
    public Float getFloat() {
        return NumUtil.f(getText());
    }
    public Double getDouble() {
        return NumUtil.d(getText());
    }
    public int getTextLength() {
        return getText().length();
    }
    public boolean isBlank() {
        return getTrimmed().isEmpty();
    }
    public boolean isEmpty() {
        return getText().isEmpty();
    }
    public String getErrorMessage() {
        return errorMessage;
    }

    // Mutators

    public RTextField setSelectAll(boolean selectAllEnabled) {
        this.selectAllEnabled = selectAllEnabled;
        removeFocusListener(selectAllFocusListener);
        if(selectAllEnabled) {
            addFocusListener(selectAllFocusListener);
        }
        return this;
    }
    public RTextField append(String text) {
        setText(getText() + text);
        return this;
    }
    public RTextField insert(int offset, String text) {
        if(offset < 0) {
            offset = 0;

        } else if(offset > getTextLength()) {
            offset = getTextLength();
        }

        String existing = getText();
        String left = existing.substring(0, offset);
        String right = existing.substring(offset);
        setText(left + text + right);
        return this;
    }
    public RTextField insertAtCaret(String text) {
        return insert(getCaretPosition(), text);
    }
    public RTextField setText(Object o) {
        setText(StringUtil.cleanNull(o));
        return this;
    }
    public RTextField setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        repaint();
        return this;
    }
    public RTextField clear() {
        setText("");
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(errorMessage != null) {
            int w = 16;              // Icon width
            int h = 16;              // Icon height
            int rightMargin = 4;     // Space from right border
            int ix = getWidth() - w - rightMargin;
            int iy = (getHeight() - h) / 2;
            ImageIcon icon = ImageLib.get(RepleteImageModel.VALIDATION_ERROR);
            g.drawImage(icon.getImage(), ix, iy, w, h, null);
        }
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

    public void focus() {
        requestFocusInWindow();
    }

    public void selectAll(boolean reverseCaret) {
        if(reverseCaret) {
            Document doc = getDocument();
            if (doc != null) {
                setCaretPosition(getTextLength());
                moveCaretPosition(0);
            }
        } else {
            super.selectAll();
        }
    }

    public void addChangeListener(DocumentListener listener) {
        getDocument().addDocumentListener(listener);
    }
    public void removeChangeListener(DocumentListener listener) {
        getDocument().removeDocumentListener(listener);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
//        RTextField f = new RTextField();
//        for(int i = -2; i <= "Derek Trumbo".length() + 2; i++) {
//            f.setText("Derek Trumbo");
//            f.insert(i, ".");
//            System.out.println(f.getText());
//        }
//        RTextField txt;
//        Lay.BLtg(Lay.fr("RTextField"),
//            "N", Lay.FL("L", Lay.lb("hi"), txt = Lay.tx("test", 10)),
//            "size=600,center,visible"
//        );
//        txt.setBorder(null);

        RTextField txtMin = Lay.tx(  "0", 10, "selectall");
        RTextField txtMax = Lay.tx("100", 10, "selectall");
        RTextField txtVal = Lay.tx( "80", 10, "selectall");
        RTextField txtLim = Lay.tx( "70", 10, "selectall");

        txtLim.setErrorMessage("test");

        JFrame frame = new EscapeFrame("RTextField/select-all");
        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        frame.add(new JLabel("SelectAllTextField 1:"), c);
        c.gridy = 1;
        frame.add(new JLabel("SelectAllTextField 2:"), c);
        c.gridy = 2;
        frame.add(new JLabel("SelectAllTextField 3:"), c);
        c.gridy = 3;
        frame.add(new JLabel("SelectAllTextField 4:"), c);
        c.gridx = 1;
        c.gridy = 0;
        frame.add(txtMin, c);
        c.gridy = 1;
        frame.add(txtMax, c);
        c.gridy = 2;
        frame.add(txtVal, c);
        c.gridy = 3;
        frame.add(txtLim, c);

        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }
}
