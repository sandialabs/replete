package replete.ui;

import java.awt.Color;
import java.awt.Graphics;

import replete.ui.lay.Lay;

public class DrawUtil {

    public static void grid(Graphics g, int w, int h) {
        Color g1 = Lay.clr("100");
        Color g2 = Lay.clr("150");//new Color(245, 245, 245);

        for(int x = 0; x < w; x++) {
            if(((x + 1) % 100) == 0) {
                g.setColor(g1);
                g.drawLine(x, 0, x, h - 1);
            }else if(((x + 1) % 10) == 0) {
                g.setColor(g2);
                g.drawLine(x, 0, x, h - 1);
            }
        }

        for(int y = 0; y < h; y++) {
            if(((y + 1) % 100) == 0) {
                g.setColor(g1);
                g.drawLine(0, y, w - 1, y);
            }else if(((y + 1) % 10) == 0) {
                g.setColor(g2);
                g.drawLine(0, y, w - 1, y);
            }
        }

        g.setColor(Color.black);
        g.drawLine(0, 0, w - 1, 0);
        for(int x = 0; x < w; x++) {
            if(((x + 1) % 100) == 0) {
                g.drawLine(x, 0, x, 8);
                int nw = GuiUtil.stringWidth(g, "" + x);
                g.drawString("" + x, x - nw/2, 20);
            }else if(((x + 1) % 10) == 0) {
                g.drawLine(x, 0, x, 4);
            }
        }

        g.drawLine(0, 0, 0, h - 1);
        for(int y = 0; y < h; y++) {
            if(((y + 1) % 100) == 0) {
                g.drawLine(0, y, 8, y);
                g.drawString("" + y, 10, y + 5);
            }else if(((y + 1) % 10) == 0) {
                g.drawLine(0, y, 4, y);
            }
        }
    }

}
