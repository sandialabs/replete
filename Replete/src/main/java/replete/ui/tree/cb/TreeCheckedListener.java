package replete.ui.tree.cb;

import java.util.EventListener;

/**
 * @author Derek Trumbo
 */

public interface TreeCheckedListener extends EventListener {
    public void valueChanged(TreeCheckedEvent e);
}
