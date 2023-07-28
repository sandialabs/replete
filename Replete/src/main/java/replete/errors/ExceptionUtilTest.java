package replete.errors;

import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JPanel;

import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeFrame;

public class ExceptionUtilTest extends EscapeFrame {


    ////////////
    // FIELDS //
    ////////////

    private JPanel pnlInner;
    private JButton btnClose;
    private JButton btnThread;

    public ExceptionUtilTest() {
        super("Example Frame for Test");

        String content =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed eu tellus ex. Maecenas nec sapien est. Pellentesque aliquam urna quis velit convallis pellentesque. Curabitur quis mauris turpis. Phasellus egestas erat et ante gravida dignissim. Pellentesque sapien enim, gravida eget felis eu, bibendum faucibus odio.";
        Lay.BLtg(this,
            "N", Lay.lb("<html>" + content + "</html>", "bg=220,eb=5,augb=mb(1b,black)"),
            "C", pnlInner = new InnerPanel(),
            "S", Lay.FL("R",
                btnThread = Lay.btn("&Thread"),
                btnClose = Lay.btn("&Close"),
                "bg=100,mb=[1t,black]"
            ),
            "size=600,center"
        );

        btnThread.addActionListener(e -> {
            Thread t = new Thread() {
                @Override
                public void run() {
                    ExceptionUtil.toss("BG-Thread");
                }
            };
            t.setName("BG Thread A");
            t.start();
        });

        btnClose.addActionListener(e -> ExceptionUtil.toss("UI-Thread"));
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class InnerPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {

//        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
//            public void uncaughtException(Thread t, Throwable e) {
//                System.out.println("SAME? " + (t == Thread.currentThread()));
//                System.out.println(t + " {" + SwingUtilities.isEventDispatchThread() + "}");
////                e.printStackTrace();
//                Component c = KeyboardFocusManager.
//                    getCurrentKeyboardFocusManager().getFocusOwner();
//                System.out.println(c);
//                System.out.println(GuiUtil.win(c));
//            }
//        });

        ExceptionUtil.setStandardUncaughtExceptionHandler();

//        ExceptionUtil.toss();

        ExceptionUtilTest frame = new ExceptionUtilTest();
        frame.setVisible(true);
        ExceptionUtilTest frame2 = new ExceptionUtilTest();
        frame2.setVisible(true);
    }
}
