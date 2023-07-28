package replete.ui.windows.common;

import java.awt.AWTEvent;

public class RWindowClosingEvent extends AWTEvent {
    private boolean cancelClose;

    public RWindowClosingEvent(Object source, int id) {
        super(source, id);
    }

    public void cancelClose() {
        cancelClose = true;
    }
    public boolean isCancelClose() {
        return cancelClose;
    }
}
