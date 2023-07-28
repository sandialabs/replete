package finio;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import replete.ui.lay.Lay;
import replete.ui.uidebug.UiDebugUtil;
import replete.ui.windows.escape.EscapeFrame;

public class Regions extends EscapeFrame {

    public Regions() {
        super("Title Here");

        UiDebugUtil.enableColor();
        UiDebugUtil.enableTicks();

        Lay.hn(getContentPane(), "dim=[100,100]");

        JPanel pnlTop;
        Lay.BLtg(this,
            "N", pnlTop = Lay.FL("L",
                Lay.p("dim=[10,10]"),
                Lay.p("dim=[10,10]"),
                "nogap,dimh=10"
            ),
            "C", Lay.AL(
                Lay.BL("C", Lay.lb("dim=[20,20]"), "bounds=[40,40,20,20],lxoc=[40,40],dixm=[20,20]"),
                Lay.p("bounds=[0,70,20,20]")
            ),
            "center,visible,pack"
        );
        pnlTop.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                check();
            }
        });
    }

    protected void check() {
        check(getContentPane());
    }

    private static String renderComponent(Component cc) {
        String extra = "";

        if(cc instanceof JLabel) {
            JLabel lbl = (JLabel) cc;
            extra = lbl.getText();
        } else if(cc instanceof JButton) {
            JButton btn = (JButton) cc;
            extra = btn.getText();
        } else if(cc instanceof JPanel) {
            JPanel pnl = (JPanel) cc;
            extra = pnl.getLayout() == null ? "{Absolute}" : pnl.getLayout().getClass().getName();
        }

        return cc.getClass().getSimpleName() + " (" + extra + ")";
    }

    public static void check(Container c) {
        System.out.println(renderComponent(c));
        for(Component cc : c.getComponents()) {
            if(cc instanceof Container) {
                check((Container) cc);
            }
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        Regions frame = new Regions();
        frame.setVisible(true);
    }
}