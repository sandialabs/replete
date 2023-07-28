package finio.platform.exts.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import finio.core.FUtil;
import finio.plugins.extpoints.JavaObjectEditorPanel;
import finio.ui.images.FinioImageModel;
import replete.ui.lay.Lay;
import replete.ui.text.RLabel;

public class UnknownNativeObjectEditorPanel extends JavaObjectEditorPanel {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    private static final int MIN_TEXT_FIELD_WIDTH = 30;
    private static final int TEXT_FIELD_RIGHT_MARGIN = 20;
    private static final int ICON_WIDTH_AND_PADDING = 16 + 4;

    // UI

    protected RLabel lbl;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public UnknownNativeObjectEditorPanel() {
        lbl = new UnkLabel();
        lbl.setIcon(FinioImageModel.NT_MANAGED_MAP);          //FinioImageMode.UNK_OBJ));
        lbl.setForeground(Color.blue);
        lbl.setBackground(Color.white);
        Lay.eb(lbl, "2l");

        Lay.FLtg(this,
            lbl, // = Lay.lb(FinioImageMode.UNK_OBJ, "fg=blue"),
            "nogap"
        );
    }

    private void updateLabelWidth() {
        if(lbl != null) {
            FontMetrics fontMetrics = lbl.getFontMetrics(lbl.getFont());
            int textWidth = fontMetrics.stringWidth(lbl.getText());
            int textHeight = fontMetrics.getHeight();
            int newWidth = Math.max(textWidth + TEXT_FIELD_RIGHT_MARGIN - 12, MIN_TEXT_FIELD_WIDTH);
            Lay.hn(lbl, "dim=[" + (newWidth + ICON_WIDTH_AND_PADDING) + "," + (textHeight + 4) + "]");
            firePreferredSizeChangedNotifier();
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void setFont(Font font) {
        super.setFont(font);

        if(lbl != null) {
            int it = O == null ? Font.ITALIC : 0;
            font = font.deriveFont(Font.BOLD | it);  // Ensure always bold
            lbl.setFont(font);
            updateLabelWidth();
            // Cannot call updateUI here.
        }
    }

    @Override
    public void setObject(Object O) {
        super.setObject(O);

        if(O == null) {
            lbl.setText(FUtil.NULL_TEXT);
            Font font = lbl.getFont();
            font = font.deriveFont(Font.BOLD | Font.ITALIC);
            lbl.setFont(font);
        } else {
            String S = FUtil.toDiagnosticString(O);
            lbl.setText(S);
        }

        // Technically this class could also one
        // day have the responsibility of displaying
        // primitives or nulls (maybe).
        // if(AUtil.isNull(V)) {
        //     vStr = NodeTerminal.NULL_TEXT;  // "null" also an option in the spec (would be BOLD+ITALIC)
        // } else if(AUtil.isString(V)) {
        //     vStr = "\"" + V + "\"";
        // } else if(V instanceof Number || V instanceof Boolean) {
        //     vStr = V.toString();
        // } else if(V instanceof AMap) {
        //     vStr = "ERROR";
        // } else if(V instanceof AList) {
        //     vStr = "ERROR";
        // } else {                             // "Other Java/PL/Platform/Host Object"
        //     vStr = AUtil.toDiagnosticString(V);
        // }

        // Since the label's content has been changed,
        // update the width of the label.
        updateLabelWidth();
        updateUI();
    }

    // The object is always valid because the user
    // can't edit it in this panel.
    @Override
    public boolean isValidState() {
        return true;
    }

    // Since original object is always returned,
    // this will not be a new object.
    @Override
    public boolean isReturnsNewObject() {
        return false;
    }

    // This panel does not allow the editing
    // of the object for which it's responsible.
    @Override
    public boolean allowsEdit() {
        return false;
    }


    /////////////////
    // INNER CLASS //
    /////////////////

    private class UnkLabel extends RLabel {
        @Override
        protected void paintComponent(Graphics g) {
            int r = 10;
            g.setColor(Color.white);
            g.fillRoundRect(0, 0,
                getWidth(), getHeight(),
                r, r);
            g.setColor(Color.black);
            g.drawRoundRect(0, 0,
                getWidth() - 1, getHeight() - 1,
                r, r);
            super.paintComponent(g);
        }
    }
}
