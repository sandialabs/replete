package replete.ui.combo;

import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import replete.ui.text.GlowingValidatingTextField;

public class GlowingValidatingComboBoxEditor extends BasicComboBoxEditor {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected JTextField createEditorComponent() {
        JTextField editor = new GlowingBorderlessTextField("", 9);
        editor.setBorder(null);
        return editor;
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class GlowingBorderlessTextField extends GlowingValidatingTextField {
        public GlowingBorderlessTextField(String value,int n) {
            super(value,n);
        }

        // workaround for 4530952
        @Override
        public void setText(String s) {
            if (getText().equals(s)) {
                return;
            }
            super.setText(s);
        }

        @Override
        public void setBorder(Border b) {
            if (!(b instanceof UIResource)) {
                super.setBorder(b);
            }
        }
    }
    public static class XUIResource extends GlowingValidatingComboBoxEditor
                                    implements javax.swing.plaf.UIResource {
    }
}
