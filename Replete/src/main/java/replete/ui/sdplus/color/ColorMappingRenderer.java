package replete.ui.sdplus.color;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * @author Derek Trumbo
 */

public class ColorMappingRenderer extends JLabel implements ListCellRenderer {

    protected SquareFilledColorIcon icon = new SquareFilledColorIcon();

    public ColorMappingRenderer() {
        setIcon(icon);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

        ColorMapping mapping = (ColorMapping) value;
        setText(mapping.toString());

        icon.color = mapping.value;

        Dimension d = new Dimension(list.getWidth(), 30);
        setMinimumSize(d);
        setPreferredSize(d);
        setMaximumSize(d);

        if(isSelected) {
            setBackground(list.getSelectionBackground());
        } else {
            setBackground(list.getBackground());
        }
        setOpaque(true);

        return this;
    }

    protected class SquareFilledColorIcon implements Icon {

        protected Color color;

        public int getIconHeight() {
            return 25;
        }

        public int getIconWidth() {
            return 27;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillRect(5, 5, 20, 20);
            g.setColor(Color.black);
            g.drawRect(5, 5, 20, 20);
        }
    }
}