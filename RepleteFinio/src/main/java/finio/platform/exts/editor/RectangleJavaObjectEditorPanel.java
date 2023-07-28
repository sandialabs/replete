package finio.platform.exts.editor;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;

import finio.plugins.extpoints.JavaObjectEditorPanel;
import replete.numbers.NumUtil;
import replete.ui.lay.Lay;
import replete.ui.text.DocumentChangeListener;
import replete.ui.text.RTextField;

public class RectangleJavaObjectEditorPanel extends JavaObjectEditorPanel {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    private static final int MIN_TEXT_FIELD_WIDTH = 30;
    private static final int TEXT_FIELD_RIGHT_MARGIN = 20;

    // Model

    private Rectangle R;

    // UI

    private JLabel lblX;
    private JLabel lblY;
    private JLabel lblW;
    private JLabel lblH;
    private RTextField txtX;
    private RTextField txtY;
    private RTextField txtW;
    private RTextField txtH;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public RectangleJavaObjectEditorPanel() {
        Lay.FLtg(this,
            lblX = Lay.lb("X: "), txtX = Lay.tx("", "selectall"),
            lblY = Lay.lb("  Y: "), txtY = Lay.tx("", "selectall"),
            lblW = Lay.lb("  Width: "), txtW = Lay.tx("", "selectall"),
            lblH = Lay.lb("  Height: "), txtH = Lay.tx("", "selectall"),
            "nogap"
        );

        attachListeners(txtX);
        attachListeners(txtY);
        attachListeners(txtW);
        attachListeners(txtH);

        txtX.addHitLeftListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                fireHitLeftNotifier();
            }
        });
        txtH.addHitRightListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                fireHitRightNotifier();
            }
        });
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
        txtX.selectAll(reverse);
    }

    @Override
    public void setKeyListener(KeyListener keyListener) {
        txtX.addKeyListener(keyListener);
        txtY.addKeyListener(keyListener);
        txtW.addKeyListener(keyListener);
        txtH.addKeyListener(keyListener);
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);

        updateLabelFont(lblX, font);
        updateLabelFont(lblY, font);
        updateLabelFont(lblW, font);
        updateLabelFont(lblH, font);

        updateTextFieldFont(txtX, font);
        updateTextFieldFont(txtY, font);
        updateTextFieldFont(txtW, font);
        updateTextFieldFont(txtH, font);
    }

    private void updateLabelFont(JLabel lbl, Font font) {
        if(lbl != null) {
            lbl.setFont(font);
        }
    }

    private void updateTextFieldFont(RTextField txt, Font font) {
        if(txt != null) {
            txt.setFont(font);
            updateTextFieldWidth(txt);
            // Cannot call updateUI here.
        }
    }

    @Override
    public void setObject(Object O) {
        super.setObject(O);
        R = (Rectangle) O;

        txtX.setText(R.x);
        txtY.setText(R.y);
        txtW.setText(R.width);
        txtH.setText(R.height);
    }

    @Override
    public Object getObject() {
        R.setRect(
            Double.parseDouble(txtX.getText()),
            Double.parseDouble(txtY.getText()),
            Double.parseDouble(txtW.getText()),
            Double.parseDouble(txtH.getText())
        );
        return R;
    }

    @Override
    public boolean isValidState() {
        return
            NumUtil.isDouble(txtX.getText()) &&
            NumUtil.isDouble(txtY.getText()) &&
            NumUtil.isDouble(txtW.getText()) &&
            NumUtil.isDouble(txtH.getText());
    }

    @Override
    public boolean isReturnsNewObject() {
        return false;                          // Rectangle objects not immutable
    }

    @Override
    public boolean allowsEdit() {
        return true;
    }

    @Override
    public void focus() {
        txtX.focus();
    }
}
