package finio.platform.exts.editor;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;

import finio.platform.exts.view.treeview.ui.editors.UnexpectedValueType;
import finio.plugins.extpoints.JavaObjectEditorPanel;
import replete.numbers.NumUtil;
import replete.ui.lay.Lay;
import replete.ui.text.DocumentChangeListener;
import replete.ui.text.NullDisplayTextField;
import replete.ui.text.RTextField;

public class StringObjectEditorPanel extends JavaObjectEditorPanel {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    private static final int MIN_TEXT_FIELD_WIDTH = 30;
    private static final int TEXT_FIELD_RIGHT_MARGIN = 20;

    // UI

    protected NullDisplayTextField txt;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public StringObjectEditorPanel() {
        Lay.FLtg(this,
            txt = new NullDisplayTextField(),
            "nogap"
        );

        attachListeners(txt);
    }

    private void attachListeners(final RTextField txt) {
        txt.addChangeListener(new DocumentChangeListener() {
            @Override
            public void documentChanged(DocumentEvent e) {
                updateTextFieldWidth(txt);
                updateUI();
                firePreferredSizeChangedNotifier();
            }
        });
        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                fireChildFocusNotifier();
            }
        });
        txt.addHitLeftListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                fireHitLeftNotifier();
            }
        });
        txt.addHitRightListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                fireHitRightNotifier();
            }
        });
    }

    private void updateTextFieldWidth(RTextField txt) {
        if(txt != null) {
            FontMetrics fontMetrics = txt.getFontMetrics(txt.getFont());
            int textWidth = fontMetrics.stringWidth(txt.getText());
            int textHeight = fontMetrics.getHeight();
            int newWidth = Math.max(textWidth + TEXT_FIELD_RIGHT_MARGIN, MIN_TEXT_FIELD_WIDTH);
            Lay.hn(txt, "dim=[" + newWidth + "," + (textHeight + 4) + "]");
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void selectAll(boolean reverse) {
        txt.selectAll(reverse);
    }

    @Override
    public void setKeyListener(KeyListener keyListener) {
        txt.addKeyListener(keyListener);
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);

        if(txt != null) {
            txt.setFont(font);
            updateTextFieldWidth(txt);
            // Cannot call updateUI here.
        }
    }

    @Override
    public void setObject(Object O) {
        super.setObject(O);

        // Since we're using a null display text field,
        // we won't convert a null into a "".
        if(O == null) {
            txt.setShowNull(true);

        } else {
            String S = convertObjectToString(O);
            txt.setText(S);
        }
    }

    protected String convertObjectToString(Object O) {
        return O.toString();
    }

    // Just return the current value in the text box
    // as the current value.  This will be a new
    // immutable String object.
    @Override
    public Object getObject() {
        Object Onew;

        if(txt.isShowNull()) {
            Onew = null;

        } else {
            if(true) {
                Onew = chooseAutoDetect();
            } else{
                Onew = choosePreviousType();
            }
        }

        return Onew;
    }

    private Object chooseAutoDetect() {
        Object Onew;
        String text = txt.getText();

        if(NumUtil.isBoolean(text)) {
            Onew = NumUtil.b(text);
        } else if(NumUtil.isInt(text)) {        // Could technically do Byte & Short too.
            Onew = NumUtil.i(text);
        } else if(NumUtil.isLong(text)) {
            Onew = NumUtil.l(text);
        } else if(NumUtil.isFloat(text)) {
            Onew = NumUtil.f(text);
        } else if(NumUtil.isDouble(text)) {
            Onew = NumUtil.d(text);
        } else if(text.length() == 1) {
            Onew = text.charAt(0);
        } else {
            Onew = text;
        }

        return Onew;
    }

    private Object choosePreviousType() {
        Object Onew;
        String text = txt.getText();

        if(O == null) {
            Onew = text;

        } else if(O instanceof Character) {
            if(text.length() > 1) {
                Onew = text;   // TODO: Changes, could also prevent change
            } else {
                Onew = new Character(text.charAt(0));
            }

        } else if(O instanceof String)  {
            Onew = text;

        } else if(O instanceof Boolean) {
            try {
                Onew = Boolean.parseBoolean(text);
            } catch(NumberFormatException e) {
                throw new UnexpectedValueType(Boolean.class);
            }

        } else if(O instanceof Byte)    {
            try {
                Onew = Byte.parseByte(text);
            } catch(NumberFormatException e) {
                throw new UnexpectedValueType(Byte.class);
            }

        } else if(O instanceof Short)   {
            try {
                Onew = Short.parseShort(text);
            } catch(NumberFormatException e) {
                throw new UnexpectedValueType(Short.class);
            }

        } else if(O instanceof Integer) {
            try {
                Onew = Integer.parseInt(text);
            } catch(NumberFormatException e) {
                throw new UnexpectedValueType(Integer.class);
            }

        } else if(O instanceof Long)    {
            try {
                Onew = Long.parseLong(text);
            } catch(NumberFormatException e) {
                throw new UnexpectedValueType(Long.class);
            }

        } else if(O instanceof Float)   {
            try {
                Onew = Float.parseFloat(text);
            } catch(NumberFormatException e) {
                throw new UnexpectedValueType(Float.class);
            }

        } else if(O instanceof Double)  {
            try {
                Onew = Double.parseDouble(text);
            } catch(NumberFormatException e) {
                throw new UnexpectedValueType(Double.class);
            }

        } else {
            // ERROR (This panel should not be handling other Java objects).
            throw new RuntimeException();
        }

        return Onew;
    }

    @Override
    public boolean isValidState() {
        return true;                       // TODO: No validation performed here yet
    }

    @Override
    public boolean isReturnsNewObject() {
        return true;                       // String objects are immutable
    }

    @Override
    public boolean allowsEdit() {
        return true;
    }

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
        txt.setFocusable(focusable);
    }

    @Override
    public void focus() {
        txt.focus();
    }
}
