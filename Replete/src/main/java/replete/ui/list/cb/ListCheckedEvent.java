package replete.ui.list.cb;

import java.util.EventObject;

/**
 * @author Derek Trumbo
 */

public class ListCheckedEvent extends EventObject {
    protected int index;
    protected boolean checked;

    public ListCheckedEvent(Object src, int index, boolean checked) {
        super(src);
        this.index = index;
        this.checked = checked;
    }

    public int getIndex() {
        return index;
    }
    public boolean isChecked() {
        return checked;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[source=" + source + ", index=" + index + ", checked=" + checked + "]";
    }
}
