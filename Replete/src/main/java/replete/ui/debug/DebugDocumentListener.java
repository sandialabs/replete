package replete.ui.debug;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author Derek Trumbo
 */

public class DebugDocumentListener extends DebugListener implements DocumentListener {

    public DebugDocumentListener() {}
    public DebugDocumentListener(String evNames) {
        super(evNames);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        debug("changedUpdate", e);
    }
    @Override
    public void insertUpdate(DocumentEvent e) {
        debug("insertUpdate", e);
    }
    @Override
    public void removeUpdate(DocumentEvent e) {
        debug("removeUpdate", e);
    }

    protected void debug(String evName, DocumentEvent e) {
        if(!acceptEvent(evName)) {
            return;
        }

        String debugStr = evName + "{type=" + e.getType() +
            ",len=" + e.getLength() +
            ",offset=" + e.getOffset() +
            ",doc=" + e.getDocument() + "}";

        output(debugStr);
    }
}
