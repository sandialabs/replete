package replete.ui.thumbs;

import javax.swing.DefaultListModel;


public class ThumbnailListModel extends DefaultListModel {
    public void fireUpdate() {
        fireContentsChanged(this, 0, size());
    }
}