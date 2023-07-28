package replete.ui.text;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public interface DocumentChangeListener extends DocumentListener {
    public default void insertUpdate(DocumentEvent e) {
        documentChanged(e);
    }
    public default void removeUpdate(DocumentEvent e) {
        documentChanged(e);
    }
    public default void changedUpdate(DocumentEvent e) {
        documentChanged(e);
    }
    public void documentChanged(DocumentEvent e);
}
