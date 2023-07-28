package replete.ui.sdplus.events;

/**
 * A listener alerted whenever something changes in the
 * scale set panel.  This can involve changing colors,
 * filter values, etc.
 *
 * @author Derek Trumbo
 */

public interface ScaleSetChangedListener {
    public void valueChanged(ValueChangedEvent e);
}
