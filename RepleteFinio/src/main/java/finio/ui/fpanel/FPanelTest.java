package finio.ui.fpanel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import finio.plugins.FinioPluginManager;
import finio.plugins.platform.FinioPlugin;
import replete.plugins.PluginManager;
import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeFrame;

public class FPanelTest extends EscapeFrame {


    ////////////
    // FIELDS //
    ////////////

    private FPanel pnlOuter;
    private JButton btnClose;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FPanelTest() {
        super("Example Frame for FPanelTest");

        PluginManager.initialize(FinioPlugin.class);
        FinioPluginManager.initialize();

        String content =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
            "Sed eu tellus ex. Maecenas nec sapien est. Pellentesque " +
            "aliquam urna quis velit convallis pellentesque. Curabitur " +
            "quis mauris turpis. Phasellus egestas erat et ante gravida " +
            "dignissim. Pellentesque sapien enim, gravida eget felis eu, " +
            "bibendum faucibus odio.";
        JButton btnToggleRecursive, btnToggleOuter, btnToggleInner;

        Lay.BLtg(this,
            "N", Lay.BL(
                "N", Lay.lb("<html>" + content + "</html>", "bg=220,eb=5,augb=mb(1b,black)"),
                "S", Lay.FL("L",
                    btnToggleRecursive = Lay.btn("&Toggle Recursive"/*, CommonConcepts.CANCEL*/),
                    btnToggleOuter = Lay.btn("&Toggle Outer Drawer"/*, CommonConcepts.CANCEL*/),
                    btnToggleInner = Lay.btn("&Toggle Inner Drawer"/*, CommonConcepts.CANCEL*/),
                    btnClose = Lay.btn("&Close"/*, CommonConcepts.CANCEL*/),
                    "bg=100,mb=[1b,black]"
                )
            ),
            "C", pnlOuter = new FPanel(), //Lay.p("bg=cyan"),
            "bg=green,size=600,center"
        );


        final FPanel pnlInner = new FPanel();
        pnlInner.setBackground(Color.red);
//        pnlInner.setOpaque(false);
        pnlInner.setBounds(50, 50, 350, 350);
//        pnlInner.setLayout(new BorderLayout());
//        pnlInner.add(Lay.lb("test test!!aslfd sflksjf sal"), BorderLayout.NORTH);
//        pnlInner.add(Lay.p("prefw=30,bg=100"), BorderLayout.EAST);
//        pnlInner.add(Lay.sp(new JTree()));
//        System.out.println(pnlInner.getBackground());
//        System.out.println(pnlInner.isOpaque());

        Lay.hn(pnlOuter, "bg=yellow");
        pnlOuter.setLayout(null);
        pnlOuter.add(pnlInner);

        btnToggleRecursive.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pnlOuter.setShowConfigDrawer(!pnlOuter.isShowingConfigDrawer(), true);
            }
        });
        btnToggleOuter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pnlOuter.toggleShowConfig();
            }
        });
        btnToggleInner.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                System.out.println(pnlInner.getBackground());
//                System.out.println(pnlInner.isOpaque());
                pnlInner.toggleShowConfig();
//                System.out.println(pnlInner.getBackground());
//                System.out.println(pnlInner.isOpaque());
            }
        });
        btnClose.addActionListener(e -> close());

        setBackground(Color.yellow);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        FPanelTest frame = new FPanelTest();
        frame.setVisible(true);
    }
}

