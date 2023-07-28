package replete.ui.text;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class LimitedDocument extends PlainDocument {
    private int limit;

    public LimitedDocument(int limit) {
        this.limit = limit;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if(doInsertString(offset, str, attr)) {
            super.insertString(offset, str, attr);
        }
    }

    protected boolean doInsertString(int offset, String str, AttributeSet attr) {
        if(str == null) {
            return false;
        }

        return (getLength() + str.length()) <= limit;
    }
}
