package replete.ui.sdplus.events;

/**
 * A listener alerted whenever a scale's corresponding
 * column in the table is shown or hidden.
 *
 * @author Derek Trumbo
 */

public interface ListedChangedListener {
    public void valueChanged(ListedChangedEvent e);
}
