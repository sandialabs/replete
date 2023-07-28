package replete.ui.windows;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.Window;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import replete.ui.GuiUtil;


public class LoadingWindow extends Window {
    private static ImageIcon loading = GuiUtil.getImageLocal("images/loading-blue.gif");

    public LoadingWindow() {
        super(null);
        final JLabel lblImage = new JLabel(loading);
        lblImage.setOpaque(false);
        final WhitePanel pnl = new WhitePanel(new GridBagLayout());
        pnl.add(lblImage);
        setLayout(new BorderLayout());
        add(pnl, BorderLayout.CENTER);
        setSize(60, 60);
        setAlwaysOnTop(true);
        setLocationRelativeTo(null);
        /*addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent arg0) {
                System.out.println("hello");
                pnl.add(lblImage);
                pnl.updateUI();
                doLayout();
                pnl.repaint();
            }
            public void windowIconified(WindowEvent arg0) {
            }
            public void windowDeiconified(WindowEvent arg0) {
            }
            public void windowDeactivated(WindowEvent arg0) {
            }
            public void windowClosing(WindowEvent arg0) {
            }
            public void windowClosed(WindowEvent arg0) {
            }
            public void windowActivated(WindowEvent arg0) {
            }
        });*/
    }

    private class WhitePanel extends JPanel {
        private int BWIDTH = 4;
        private Color[] bClrs = createBorderColors();
        public WhitePanel(LayoutManager m) {
            super(m);
        }
        @Override
        public void paintComponent(Graphics g) {
//            super.paintComponent(g);
            g.setColor(Color.white);
            g.fillRect(0, 0, getWidth(), getHeight());
            for(int b = 0; b < BWIDTH; b++) {
                g.setColor(bClrs[b]);
                g.drawRect(b, b, getWidth() - b * 2 - 1, getHeight() - b * 2 - 1);
            }
        }
        private Color[] createBorderColors() {
            Color[] clrs = new Color[BWIDTH];
            for(int b = 0; b < BWIDTH; b++) {
                int c = b * 60;
                clrs[b] = new Color(c, c, c);
            }
            return clrs;
        }
    }

    public static void main(String[] args) {
        LoadingWindow win = new LoadingWindow();
        win.setVisible(true);
        // I don't know why but the window shows up for a brief moment
        // showing gray only when the icon label is added to the
        // WhitePanel and only immediately upon setVisible (nothing
        // you do before setVisible like letting the image load changes
        // what happens when setVisible is called).
    }
}
