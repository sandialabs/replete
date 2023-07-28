package replete.ui.list;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JList;

public class UpdatingList extends JList {
    protected boolean isUpdating;
    public boolean isUpdating() {
        return isUpdating;
    }
    public void setUpdating(boolean updating) {
        isUpdating = updating;
        repaint();
    }
    protected Color dimmedColor = new Color(0, 0, 0, 128);
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(isUpdating) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(dimmedColor);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}