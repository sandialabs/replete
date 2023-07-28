package replete.ui.tree.cb;

import java.util.EventObject;

import javax.swing.tree.TreePath;

/**
 * @author Derek Trumbo
 */

public class TreeCheckedEvent extends EventObject {
    protected TreePath path;
    protected boolean checked;

    public TreeCheckedEvent(Object src, TreePath p, boolean c) {
        super(src);
        path = p;
        checked = c;
    }

    public TreePath getPath() {
        return path;
    }

    public boolean isChecked() {
        return checked;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[source=" + source + ", path=" + path + ", checked=" + checked + "]";
    }
}
