package learning.ui.draw;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import learning.ui.images.LearningImageModel;
import replete.ui.images.concepts.ImageLib;

public class DrawFrame extends JFrame /* implements ActionListener, KeyListener (bad way) */{


    ////////////
    // FIELDS //
    ////////////

    //private EventHandler eventHandler = new EventHandler();  (bad way)
    private JButton btn1;
    private JTextField txtName;
    private JButton btnClose;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public DrawFrame() {
        super("My Draw App");

//        setIconImage(ImageLib.get("iconx.gif").getImage());

        setLayout(new BorderLayout());

        JPanel pnlNorth = new JPanel();
        pnlNorth.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel lbl1 = new JLabel("My First Label");
        lbl1.setForeground(Color.WHITE);
        btn1 = new JButton("My First Button");
        btn1.setMnemonic('B');
        btn1.addActionListener(new ActionListener() {             // Good way
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                    DrawFrame.this, "My Message!!",
                    "The Title", JOptionPane.WARNING_MESSAGE
                );
                System.out.println("First Button Clicked");
                System.out.println("  Text Is: " + btn1.getText());
            }
        });
        txtName = new JTextField("Name", 10);
        txtName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = (String) JOptionPane.showInputDialog(
                    DrawFrame.this, "Give me SUM 1nPUTZ.....  Please perform additional edits to the name.",
                    "The Title", JOptionPane.QUESTION_MESSAGE,
                    null /*icon*/, null /*available options*/,
                    txtName.getText()
                );

                if(input == null) {
                    System.out.println("User Canceled Input Dialog");
                } else {
                    System.out.println("Dialog Input: [" + input + "]");
                    System.out.println("Trimmed Dialog Input: [" + input.trim() + "]");
                }

                System.out.println("Text Field Enter Key Pressed");
                System.out.println("  Text Is: " + txtName.getText());
            }
        });
        txtName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                printKeyEvent("keyTyped", e);
            }
            @Override
            public void keyPressed(KeyEvent e) {
                printKeyEvent("keyPressed", e);
            }
            @Override
            public void keyReleased(KeyEvent e) {
                printKeyEvent("keyReleased", e);
            }
            private void printKeyEvent(String method, KeyEvent e) {
                System.out.println(method + ": '" +
                    e.getKeyChar() + "' [" +
                    e.getKeyCode() + "] Mod=" +
                    e.getModifiers()
                );
            }
        });
        pnlNorth.setBackground(new Color(100, 100, 100));
        pnlNorth.add(lbl1);
        pnlNorth.add(btn1);
        pnlNorth.add(txtName);

        JList lstNames = new JList(
            new Object[] {
                "Abby",
                "Johnny",
                "Marco",
                "Anotonio Marquez Domingo Arriba Chimichanga"
            }
        );
        JScrollPane scrNames = new JScrollPane(lstNames);
        scrNames.setPreferredSize(new Dimension(160, 1));

        add(pnlNorth, BorderLayout.NORTH);
        add(scrNames, BorderLayout.WEST);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnClose = new JButton("Cancel");
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        btnClose.setMnemonic('C');
        pnlButtons.add(btnClose);
        add(pnlButtons, BorderLayout.SOUTH);

        JPanel pnlCenter = new MyCustomDrawPanel();
//        pnlCenter.setBackground(new Color(12, 54, 200));
        //pnlCenter.setFocusable(true);  Generally labels and panels never focusable
        add(pnlCenter, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("GOODBYE CRUEL WORLD");
                System.exit(0);   // Not required in most cases
            }
        });

        setSize(600, 600);
        setLocationRelativeTo(null);
    }

    private class MyCustomDrawPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);  // ALMOST ALWAYS CALL SUPER

//            DrawUtil.grid(g, getWidth(), getHeight());

            g.setColor(Color.blue);
            g.drawLine(0, 0, 100, 100);

            g.setColor(Color.red);
            g.drawLine(100, 0, 0, 100);

            g.setColor(Color.pink);
            g.fillRect(100, 100, 100, 100);

            g.setColor(Color.yellow);
            g.fillRect(0, 200, getWidth(), 12);
            g.setColor(Color.black);
            g.drawRect(0, 200, getWidth() - 1, 12 - 1);

            int height = getHeight();
            for(int i = 0; i < height; i += 10) {
//                int lineLength;
//                if(i % 100 == 0) {
//                    lineLength = 20;
//                } else {
//                    lineLength = 10;
//                }
                int lineLength = (i % 100 == 0) ? 20 : 10;
                g.drawLine(0, i, lineLength, i);
            }

            Font f = new Font("Courier New", Font.BOLD + Font.ITALIC, 32);
            g.setFont(f);
            g.drawString("42 RULZ", 30, 300);

            Image icon = ImageLib.get(LearningImageModel.ECLIPSE_LOGO).getImage();
            g.drawImage(icon, 200, 200, 50, 50, null);
        }
    }

    /*
    private class EventHandler extends WindowAdapter implements ActionListener, KeyListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if(source == btn1) {
                JOptionPane.showMessageDialog(
                    DrawFrame.this, "My Message!!",
                    "The Title", JOptionPane.WARNING_MESSAGE
                );

                System.out.println("First Button Clicked");
                System.out.println("  Text Is: " + btn1.getText());

            } else if(source == txtName) {
                String input = (String) JOptionPane.showInputDialog(
                    DrawFrame.this, "Give me SUM 1nPUTZ.....  Please perform additional edits to the name.",
                    "The Title", JOptionPane.QUESTION_MESSAGE,
                    null , null,
                    txtName.getText()
                );

                if(input == null) {
                    System.out.println("User Canceled Input Dialog");
                } else {
                    System.out.println("Dialog Input: [" + input + "]");
                    System.out.println("Trimmed Dialog Input: [" + input.trim() + "]");
                }

                System.out.println("Text Field Enter Key Pressed");
                System.out.println("  Text Is: " + txtName.getText());

            } else if(source == btnClose) {
                dispose();
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
            printKeyEvent("keyTyped", e);
        }
        @Override
        public void keyPressed(KeyEvent e) {
            printKeyEvent("keyPressed", e);
        }
        @Override
        public void keyReleased(KeyEvent e) {
            printKeyEvent("keyReleased", e);
        }

        private void printKeyEvent(String method, KeyEvent e) {
            System.out.println(method + ": '" +
                e.getKeyChar() + "' [" +
                e.getKeyCode() + "] Mod=" +
                e.getModifiers()
            );
        }

        @Override
        public void windowClosing(WindowEvent e) {
            System.out.println("GOODBYE CRUEL WORLD");
            System.exit(0);
        }
    }*/
}
