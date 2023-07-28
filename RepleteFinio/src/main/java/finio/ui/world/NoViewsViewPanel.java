package finio.ui.world;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import finio.ui.FContextMenuCreator;
import finio.ui.actions.FActionMap;
import finio.ui.app.AppContext;
import finio.ui.fpanel.FPanel;
import finio.ui.images.FinioImageModel;
import finio.ui.worlds.WorldContext;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.text.RLabel;

public class NoViewsViewPanel extends FPanel {


    ////////////
    // FIELDS //
    ////////////

    private FActionMap actionMap;
    private AppContext ac;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public NoViewsViewPanel(AppContext ac, WorldContext wc) {
        this.ac = ac;
        actionMap = ac.getActionMap();

        Lay.BLtg(this,
            "C", new LoadInitialViewPanel()   // Currently can't @Override paintComponent so need to compose
        );
    }


    /////////////////
    // INNER CLASS //
    /////////////////

    private class LoadInitialViewPanel extends RPanel {

        public LoadInitialViewPanel() {

            final RLabel lblIcon, lblBottom;
            Lay.GBLtg(this,
                Lay.BL(
                    "W", lblIcon = Lay.lb(FinioImageModel.LARGE_BINOCS, "cursor=hand"),
                    "C", Lay.BxL("Y",
                        Box.createVerticalGlue(),
                        Lay.GL(2, 1,
                            Lay.lb("Your information awaits!",
                                "font=Helvetica,bold,size=16,mb=[1b,black],center"),
                            lblBottom = Lay.lb("<html>Now choose your <font color='blue'>first view</font>.</html>",
                                "font=Helvetica,size=14,cursor=hand")
                        ),
                        Box.createVerticalGlue(),
                        "eb=10l"
                    ),
                    "eb=10tlb15r,augb=mb(1,blue),bg=white,chtransp"
                )
            );

            lblIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    lblIcon.setIcon(FinioImageModel.LARGE_BINOCS);
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    lblIcon.setIcon(FinioImageModel.LARGE_BINOCS_EMPH);
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    JPopupMenu mnuPopup = createPopupMenu(e);
                    mnuPopup.show(lblIcon, e.getX(), e.getY());
                }
            });
            lblBottom.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    JPopupMenu mnuPopup = createPopupMenu(e);
                    mnuPopup.show(lblBottom, e.getX(), e.getY());
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if(SwingUtilities.isRightMouseButton(e)) {
                        JPopupMenu mnuPopup = createPopupMenu(e);
                        mnuPopup.show(NoViewsViewPanel.this, e.getX(), e.getY());
                    }
                }
            });
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Color[] c = new Color[] {Lay.clr("8B89FF"), Color.white, Lay.clr("8B89FF"), Color.white};
            float[] p = new float[] {0.0F, 0.15F, 0.40F, 0.90F};

            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(
                new LinearGradientPaint(0, getHeight(), getWidth(), 0, p, c)
            );
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        protected JPopupMenu createPopupMenu(MouseEvent e) {
            return new FContextMenuCreator(ac, actionMap).createPopupMenu(e);
        }
    }

//    @Override
//    public void addAnyActionListener(ChangeListener listener) {
//    }
//
//    @Override
//    public String getViewName() {
//        return "Empty";
//    }
//
//    @Override
//    public void addSelectedListener(ChangeListener listner) {
//    }
//
//    @Override
//    public void removeSelectedListener(ChangeListener listener) {
//    }
//
//    @Override
//    public SelectionContext[] getSelectedValues(int reverseDepth) {
//        return new SelectionContext[] {
//            new SelectionContext().addSegment(new SelectionContextSegment(getK(), getV()))
//        };
//    }
}
