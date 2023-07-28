package replete.ui.list.icons;

import java.awt.Component;
import java.awt.Insets;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

import replete.ui.Iconable;
import replete.ui.lay.Lay;



//////////////
// RENDERER //
//////////////

public class IconListCellRenderer extends DefaultListCellRenderer {


    ////////////
    // FIELDS //
    ////////////

    protected Map<?, Icon> iconMap = null;
    protected Insets insets;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public IconListCellRenderer() {
        setInsets(new Insets(0, 0, 0, 0));
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessor

    @Override
    public Insets getInsets() {
        return insets;
    }

    // Mutators

    public void setInsets(Insets newInsets) {
        insets = newInsets;
    }
    public void setIconMap(Map<?, Icon> map) {
        iconMap = map;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        JLabel label =
            (JLabel) super.getListCellRendererComponent(list,
                value, index, isSelected, cellHasFocus);
        try {
            Icon icon = null;
            if(value instanceof Iconable) {
                icon = ((Iconable) value).getIcon();
            } else if(iconMap != null && iconMap.get(value) != null) {
                icon = iconMap.get(value);
            }
            if(value != null) {
                label.setIcon(icon);
            }
            String code = insets.top + "t" + insets.left + "l" +
                          insets.right + "r" + insets.bottom + "b";
            label.setBorder(
                BorderFactory.createCompoundBorder(label.getBorder(),
                    Lay.eb(code)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return label;
    }
}
