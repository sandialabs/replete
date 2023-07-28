package replete.jgraph.ui.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.jgraph.ui.images.RepleteGraphImageModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.menu.RMenuItem;
import replete.ui.panels.GradientPanel;

public class StageWrapperTitlePanel extends GradientPanel {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    private static final Color titleLighterSelected = Lay.clr("FFFCCC");
    private static final Color titleDarkerSelected = Lay.clr("FFFB93");
    private static final Color titleBorderSelected = Lay.clr("E5DE50");

    private static final Color titleLighter = Lay.clr("E0F2FF");
    private static final Color titleDarker = Lay.clr("A8D6F4");
    private static final Color titleBorder = Lay.clr("365468");

    private static final Font font = new Font("Tahoma", Font.BOLD, 12);

    // UI

    private JButton btn;
    private boolean selected;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public StageWrapperTitlePanel(String name) {
        JLabel lbl;
        JPanel pnl;

        final JPopupMenu mnuPopup = new JPopupMenu();
        JMenuItem mnuExecute = new RMenuItem("&Execute", RepleteGraphImageModel.ERROR);
        mnuPopup.add(mnuExecute);

        Lay.BLtg(this,
            "C", pnl = Lay.BL(
                "W", lbl = Lay.lb(name),
                "C", Lay.p("opaque=false"),
                "E", btn = Lay.btn(CommonConcepts.SUBTRACT, "icon"),
                "opaque=false,eb=5"
            ),
            "gradient"
        );
        lbl.setFont(font);
        setSelected(false);
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireCollapseExpandNotifier();
            }
        });
        pnl.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e)) {
                    mnuPopup.show(StageWrapperTitlePanel.this, e.getX(), e.getY());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }
        });
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    private ChangeNotifier collapseExpandNotifier = new ChangeNotifier(this);
    public void addCollapseExpandListener(ChangeListener listener) {
        collapseExpandNotifier.addListener(listener);
    }
    private void fireCollapseExpandNotifier() {
        collapseExpandNotifier.fireStateChanged();
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessor

    public boolean isSelected() {
        return selected;
    }

    // Mutator

    public void setSelected(boolean selected) {
        this.selected = selected;
        if(selected) {
            setColors(titleLighterSelected, titleDarkerSelected);
            setBorderColor(titleBorderSelected);
        } else {
            setColors(titleLighter, titleDarker);
            setBorderColor(titleBorder);
        }
    }
    private void setBorderColor(Color c) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        String hex = String.format("#%02x%02x%02x", r, g, b);
        Lay.hn(this, "mb=[1b," + hex + "]");
    }
    public void setCollapsed(boolean collapsed) {
        btn.setIcon(ImageLib.get(collapsed ? CommonConcepts.ADD : CommonConcepts.SUBTRACT));
    }
}
