package finio.ui.app;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.JPanel;

import finio.ui.images.FinioImageModel;
import finio.ui.worlds.WorldsPanel;
import replete.ui.GuiUtil;
import replete.ui.lay.Lay;
import replete.ui.panels.MovingGradientPanel;
import replete.ui.text.RLabel;
import replete.ui.uiaction.UIAction;

public class LoadInitialWorldPanel extends MovingGradientPanel {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public LoadInitialWorldPanel(final AppContext ac) {

        final RLabel lblResume, lblNew, lblOpen;

        Lay.GBLtg(this,
            Lay.BL(
                "W", Lay.BL(
                    "C", Lay.lb(FinioImageModel.FINIO_LOGO_BIG,
                        "bg=red,dim=[100,100]")
                ),
                "C", Lay.BL(
                    "C", Lay.lb(FinioImageModel.FINIO_TEXT),
                    "dim=[200,100]"
                ),
                "S", Lay.BL(
                    "N", Lay.FL(Lay.hn(new JPanel(), "mb=[1b,blue],dim=[290,5]"), "dimh=15,bg=red"),
                    "C", Lay.FL(
                        lblResume = Lay.lb(FinioImageModel.RESUME_PLAY, "cursor=hand, dim=[64,64]"),
                        Box.createHorizontalStrut(20),
                        lblNew = Lay.lb(FinioImageModel.NEW_STAR, "cursor=hand, dim=[64,64]"),
                        Box.createHorizontalStrut(20),
                        lblOpen = Lay.lb(FinioImageModel.OPEN_FOLDER, "cursor=hand, dim=[64,64]")
                    ),
                    "opaque=true,bg=white"
                ),
                "bg=white,chtransp,dim=[310,200],eb=5,augb=mb(1,blue)"
            ),
            WorldsPanel.STYLE
        );

        lblResume.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                lblResume.setIcon(FinioImageModel.RESUME_PLAY);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                lblResume.setIcon(FinioImageModel.RESUME_PLAY_EMPH);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                lblResume.setCursor(null);
                ac.notImpl("Resume Session");
                lblResume.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        });

        lblNew.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                lblNew.setIcon(FinioImageModel.NEW_STAR);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                lblNew.setIcon(FinioImageModel.NEW_STAR_EMPH);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                lblNew.setCursor(null);
                UIAction action = ac.getActionMap().getAction("new-world");
                action.execute();
            }
        });

        lblOpen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                lblOpen.setIcon(FinioImageModel.OPEN_FOLDER);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                lblOpen.setIcon(FinioImageModel.OPEN_FOLDER_EMPH);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                lblOpen.setCursor(null);
                UIAction action = ac.getActionMap().getAction("open-world");
                action.execute();
            }
        });

        setFocusable(true);
        focus();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent arg0) {
                if(arg0.getKeyCode() == KeyEvent.VK_SPACE) {
                    ac.notImpl("Resume Session");
                }
            }
        });

        configureMovingGradient();
    }

    private void configureMovingGradient() {
        Color bg = getBackground();
        int x = -40;
        int y = -80;
        Color darker = Lay.clr("B2CEFF");
        Color darkerX = GuiUtil.deriveColor(darker, x, x, x);
        Color darkerY = GuiUtil.deriveColor(darker, y, y, y);
        Color[] c = new Color[] {darkerX, bg, darkerY, bg};
        float[] p = new float[] {0.0F, 0.15F, 0.40F, 0.15F};

        setParameters(c, p);
        setOrientation(Orientation.UPPER_LEFT_TO_LOWER_RIGHT);
        setInterval(50);
        setOffsetDelta(0.006F);
        start();
    }
}
