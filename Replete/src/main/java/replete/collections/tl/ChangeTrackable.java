package replete.collections.tl;

import javax.swing.event.ChangeListener;

public interface ChangeTrackable {
    public void addChangeListener(ChangeListener listener);
    public void removeChangeListener(ChangeListener listener);
}
