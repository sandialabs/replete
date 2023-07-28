package replete.ui.windows.common;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import replete.ui.windows.escape.EscapeDialog;
import replete.ui.windows.escape.EscapeFrame;
import replete.util.ReflectionUtil;


public class RWindowDemo {

    public static void main(String[] args) {
        RWindowDemoFrame demo = new RWindowDemoFrame();
        demo.setVisible(true);
    }

    public static interface WindowListable {
        public String listWindows();
    }

    public static class MyChildFrame extends EscapeFrame {
        public MyChildFrame(String title) {
            super(title);

            Color color = ReflectionUtil.get(Color.class, title.split(":")[0]);
            getContentPane().setBackground(color);

            JButton btnVis = new JButton("Make Not Visible");
            btnVis.addActionListener(e -> setVisible(false));

            setLayout(new FlowLayout());
            add(btnVis);

            setSize(350, 100);
            setLocationRelativeTo(null);
        }
    }

    public static class MyChildDialog extends EscapeDialog {
        public MyChildDialog(JFrame parent, String title, boolean mod) {
            super(parent, title, mod);
            init();
        }
        public MyChildDialog(JDialog parent, String title, boolean mod) {
            super(parent, title, mod);
            init();
        }
        private void init() {
            Color color = ReflectionUtil.get(Color.class, getTitle().split(":")[0]);
            getContentPane().setBackground(color);

            JButton btnVis = new JButton("Make Not Visible");
            btnVis.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });

            setLayout(new FlowLayout());
            add(btnVis);

            setSize(350, 100);
            setLocationRelativeTo(getParent());
        }
    }
}
