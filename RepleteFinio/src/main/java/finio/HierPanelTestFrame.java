package finio;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

import finio.ui.fpanel.FPanel;
import replete.text.StringUtil;
import replete.ui.DrawUtil;
import replete.ui.GuiUtil;
import replete.ui.panels.RPanel;
import replete.ui.windows.escape.EscapeFrame;

public class HierPanelTestFrame extends EscapeFrame {


    ////////////
    // FIELDS //
    ////////////

    private JButton btnSet;
    private JButton btnClose;
    private FPanel pnlLeft;
    private DrawPanel pnlRight;

    // Insets is ONLY relating to the currently set borders and nothing more.
    // There is no way to specifically set the insets.  Only getInsets() is present
    // on components and getBorderInsets() is present on Border objects.

    public HierPanelTestFrame() {
        super("Example Frame for HierPanelTest");

//        JPanel pnl;
//        Lay.ALtg(this,
//            pnl = Lay.FL(
//                Lay.btn("A"),
//                Lay.btn("B"),
//                "bounds=[20,20,400,200],bg=red,mb=[40,blue]"
//            ),
//            "size=600,center"
//        );

//        String content =
//            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed eu tellus ex. Maecenas nec sapien est. Pellentesque aliquam urna quis velit convallis pellentesque. Curabitur quis mauris turpis. Phasellus egestas erat et ante gravida dignissim. Pellentesque sapien enim, gravida eget felis eu, bibendum faucibus odio.";
//        Lay.BLtg(this,
//            "N", Lay.lb("<html>" + content + "</html>", "bg=220,eb=5,augb=mb(1b,black)"),
//            "C", Lay.GL(1, 2,
//                pnlLeft = Lay.BL(
//                    "N", Lay.FL(Lay.btn("Test", "name=ButtonTest"), "name=SourceNorthFL"),
//                    "W", Lay.sp(Lay.lst(new String[] {"AAA", "BBB", "CCC"}, "name=List"), "name=SourceWestSP"),
//                    "C", Lay.GL(2, 1,
//                        Lay.sp(Lay.tbl("name=Table"), "name=GridSP"),
//                        Lay.btn("Big", "name=ButtonBig"),
//                        "name=SourceCenterGrid"
//                    ),
//                    "name=Source"
//                ),
//                pnlRight = (DrawPanel) Lay.hn(new DrawPanel(), "name=DestDraw"),
//                "name=Harness"
//            ),
//            "S", Lay.FL("R",
//                btnSet = Lay.btn("&Set"),
//                btnClose = Lay.btn("&Close"/*, CommonConcepts.CANCEL*/),
//                "bg=100,mb=[1t,black]"
//            ),
//            "size=600,center"
//        );
//        setLocation(getLocation().x, 20);
//        btnSet.addActionListener(e -> pnlRight.initializeFrom(pnlLeft));
//        btnClose.addActionListener(e -> closeFrame());
    }


    /////////////////
    // INNER CLASS //
    /////////////////

    private class DrawPanel extends RPanel {
        private Component cmp;
        private boolean highlighted;
        private boolean initialized;

        public DrawPanel() {
            setLayout(null);
            setHandCursor();

            addMouseListener(new MouseListener() {

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    highlighted = false;
                    repaint();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    highlighted = true;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if(!initialized) {
                String str = "Uninitialized";
                g.drawString(str, (getWidth() - GuiUtil.stringWidth(g, str)) / 2, getHeight() / 2);
                return;
            }
            if(highlighted) {
                g.setColor(Color.green);
            } else {
                g.setColor(Color.red);
            }
            g.fillRect(0, 0, getWidth(), getHeight());
            DrawUtil.grid(g, 1000, 1000);
            g.setColor(Color.yellow);
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            if(cmp != null) {
                g.drawString(cmp.getName(), 5, 15);
            }
        }

        public void fix() {

        }

        public void initializeFrom(Component cmp) {
            initializeFrom(cmp, 0);
        }

        private void initializeFrom(Component cmp, int level) {
            this.cmp = cmp;

            if(level == 0) {
                removeAll();
            }

            System.out.println(StringUtil.spaces(4 * level) + cmp.getName() + ": (" + cmp.getBounds().x + "," + cmp.getBounds().y + ") [" + cmp.getBounds().width + "x" + cmp.getBounds().height + "] " + cmp.getClass().getSimpleName());
            if(cmp instanceof Container) {
                Container cnt = (Container) cmp;
                for(int c = 0; c < cnt.getComponentCount(); c++) {
                    Component cmpChild = cnt.getComponent(c);
                    DrawPanel pnlWrapperChild = new DrawPanel();
                    pnlWrapperChild.initializeFrom(cmpChild, level + 1);
                    add(pnlWrapperChild);
                    pnlWrapperChild.setBounds(cmpChild.getBounds());

//                    if(cmpChild instanceof Container) {
//                        p2.initialized = true;
//                        initializeFrom(cnt2, level + 1, p2);
//                    } else {
//                        System.out.println("xxx" + cmpChild);
//                    }
                }
            }

            initialized = cmp != null;

            if(level == 0) {
                updateUI();
            }
        }
    }

//    private class XPanel extends JPanel {
//        @Override
//        protected void paintComponent(Graphics g) {
//            super.paintComponent(g);
//            DrawUtil.grid(g, 1000, 1000);
//            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
//        }
//    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        HierPanelTestFrame frame = new HierPanelTestFrame();
        frame.setVisible(true);
    }
}
