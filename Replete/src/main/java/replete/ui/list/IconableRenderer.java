package replete.ui.list;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import replete.ui.Iconable;
import replete.ui.lay.Lay;

public class IconableRenderer extends DefaultListCellRenderer {   // Useful for combo boxes
    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel lbl = (JLabel) super.getListCellRendererComponent(
            list, value, index, isSelected, cellHasFocus);
        Iconable iconable = (Iconable) value;
        lbl.setIcon(iconable.getIcon());
        Lay.hn(lbl, "eb=3l");
        return lbl;
    }
}
