package finio;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import finio.ui.fpanel.FPanel;
import replete.ui.lay.Lay;
import replete.ui.uidebug.UiDebugUtil;
import replete.ui.windows.escape.EscapeFrame;

public class JPanelExperiment extends EscapeFrame {

    public JPanelExperiment() {
        super("Title Here");
        Lay.BLtg(this,
            "N", new Panel1(),
            "W", new Panel2(),
            "C", new Panel3(),
            "size=600,center"
        );
    }

    private class Panel1 extends FPanel {
        public Panel1() {
            Lay.WLtg(this, "L",
                Lay.btn("111111"),
                Lay.btn("222222"),
                Lay.btn("333333"),
                Lay.btn("444444"),
                Lay.btn("555555"),
                Lay.btn("666666"),
                Lay.btn("777777"),
                Lay.btn("888888"),
                Lay.btn("999999")
            );
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            AffineTransform T = new AffineTransform();
            T.translate(10, 10);
            g2.setTransform(T);
            super.paintComponent(g);

            g2.setColor(Color.red);
            g2.fillRect(0, 0, 15, 15);
        }
    }

    private class Panel2 extends FPanel {
        public Panel2() {
            Lay.BLtg(this,
                "C", Lay.sp(
                    Lay.lst((Object) new Object[] {"apple", "orange", "banana"})
                )
            );
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            AffineTransform T = new AffineTransform();
            T.translate(10, 10);
            g2.setTransform(T);
            super.paintComponent(g);

            g2.setColor(Color.red);
            g2.fillRect(0, 0, 15, 15);
        }
    }

    private class Panel3 extends FPanel {
        public Panel3() {
            Lay.BLtg(this,
                "C", Lay.sp(
                    Lay.tbl(new String[] {"name", "age"}, new String[][]{{"tony", "22"},{"sally", "12"}})
                )
            );
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            AffineTransform T = new AffineTransform();
            T.translate(10, 10);
            g2.setTransform(T);
            super.paintComponent(g);

            g2.setColor(Color.red);
            g2.fillRect(0, 0, 15, 15);
        }
    }

    public static void main(String[] args) {
        UiDebugUtil.enableColor();
        JPanelExperiment frame = new JPanelExperiment();
        frame.setVisible(true);
    }
}