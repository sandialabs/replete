package replete.ui.thumbs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import replete.ui.list.UpdatingList;


public class ThumbnailList extends UpdatingList {


    ////////////
    // FIELDS //
    ////////////

    private boolean globalMemoryError;
    private List<ThumbnailGlob> globList = new ArrayList<>();
    private Map<String, ThumbnailGlob> globs = new HashMap<>();
    private int desiredRows = -1;
    private int desiredCols = -1;
    private ThumbnailListModel model;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ThumbnailList() {
        this(-1);
    }
    public ThumbnailList(int cols) {
        desiredCols = cols;
        setCellRenderer(new ThumbnailRenderer());
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setLayoutOrientation(JList.HORIZONTAL_WRAP);
        setVisibleRowCount(3);
        setModel(model = new ThumbnailListModel());
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public boolean isGlobalMemoryError() {
        return globalMemoryError;
    }
    public void setGlobalMemoryError(boolean globalMemoryError) {
        this.globalMemoryError = globalMemoryError;
    }
    public void ensureSelectedVisible() {
        ensureIndexIsVisible(getSelectedIndex());
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    protected class ThumbnailRenderer extends JLabel implements ListCellRenderer {
        private final Color HIGHLIGHT_COLOR = new Color(215, 255, 255);

        public ThumbnailRenderer() {
            setOpaque(true);
        }

        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {

            ThumbnailSidebarEntry entry = (ThumbnailSidebarEntry) value;

            // Not sure why but was being called as null once.  Might not
            // be needed.
            if(entry == null) {
                setText("");
                setIcon(null);
                setBackground(Color.white);
                return this;
            }

            String memError = globalMemoryError ? " (could not load - memory error)" : "";
            setText(entry.getTitle() + memError);

            Icon icon = entry.getIcon();
            if(icon != null) {
                Dimension prefSize = new Dimension(150, icon.getIconHeight() + 30);
                setPreferredSize(prefSize);
                setIcon(icon);
                //                DU.p(" * " + entry.getTitle() + " " + icon.getIconWidth() + " "+icon.getIconHeight());
            } else {
                setIcon(null);
                setPreferredSize(new Dimension(150, 30));
            }

            setVerticalTextPosition(SwingConstants.BOTTOM);
            setHorizontalTextPosition(SwingConstants.CENTER);
            setHorizontalAlignment(SwingConstants.CENTER);

            if(isSelected) {
                setBackground(HIGHLIGHT_COLOR);
            } else {
                setBackground(Color.white);
            }

            return this;
        }
    }

    public void add(File file, String key, String name) {


    }
    public void add(Image image, String key, String name) {

    }
}
