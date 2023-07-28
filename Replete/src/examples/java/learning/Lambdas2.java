package learning;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;

// Streams Package:
// Lambda Expressions
// Stream Library
// Functional Interfaces
//  - @FunctionInterface
// Default Methods
// effectively final vs. final

public class Lambdas2 {

    public static void main(String[] args) {
        float $ = 0.33F;
        int $$$ = 393;

        Observable o1 = new MyObservable();

        Observable o2 = new Observable() {  // Anonymous inner class
            @Override
            public void observe() {
                System.out.println("Hello World!");
            }

//            @Override
//            public void lookup() {
//                // TODO Auto-generated method stub
//
//            }
        };

        o2.observe();

        Observable o3 = () -> {
            System.out.println("Hello World!");    // If Observable had both observe() and lookup() then which is this
        };

        Observable o4 = () -> System.out.println("Hello World!");  // Single lines of lambda expressions are ok!

        o3.observe();

        JButton btnRun = new JButton("Run");
        btnRun.addActionListener(new ActionListener() {     // Anonymous inner class
            @Override
            public void actionPerformed(ActionEvent e) {
                // (COMPILER DOESNT KNOW THE CODE)
            }
        });

        btnRun.addActionListener(e -> {
            System.out.println(e);
        });

        btnRun.addActionListener((e) -> {
            System.out.println(e);
        });

        btnRun.addActionListener((ActionEvent e) -> {
            System.out.println(e);
        });

        btnRun.addActionListener(e -> System.out.println(e));  // Compiler knows its void

//        btnRun.addKeyListener(new KeyListener() {
//            @Override
//            public void keyTyped(KeyEvent e) {
//            }
//            @Override
//            public void keyReleased(KeyEvent e) {
//            }
//            @Override
//            public void keyPressed(KeyEvent e) {
//            }
//        });
//
//        btnRun.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyTyped(KeyEvent e) {
//
//            }
//        });
//
//        JFrame f = null;
//        f.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowOpened(WindowEvent e) {
//
//            }
//        });
//        f.addWindowListener(new WindowListener() {
//
//            @Override
//            public void windowOpened(WindowEvent e) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void windowIconified(WindowEvent e) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void windowDeiconified(WindowEvent e) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void windowDeactivated(WindowEvent e) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void windowClosing(WindowEvent e) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void windowClosed(WindowEvent e) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void windowActivated(WindowEvent e) {
//                // TODO Auto-generated method stub
//
//            }
//        });

//        if(true)
//            System.out.println(1);
//            System.out.println(2);

        Drawable d1 = (x, y) -> {

        };
        Drawable d2 = (int x, int y) -> {

        };

        performSquaring((x1, x2) -> x1 * x2, 42);

        Squarer sq = (x1, x2) -> {
//            asdf
//            dasf
//            adsf
//            afsd
//            asdf
            return x1 * x2;           // Return required (if non-void) in non-single line lambdas
        };

        someMethod(34);

//        Method m = {
//            return x * x;
//        };
//        Method m2 = m;
//        Method m3 = (int x) -> {
//            return x * x;
//        };
//        m(34);

    }

    private static int __super_secret_method_lambda_34831634__(int x, int y) {
        return x * y;
    }

    public static int someMethod(int x) {
        return x * x;
    }
    public static int performSquaring(Squarer sq, int x) {
        return sq.square(x, x);
    }

    public static interface Squarer {
        int square(int x1, int x2);
    }

    private static interface Drawable {
        void draw(int x, int y);
    }

    // Functional Interface - an interface with exactly 1 non-default method

    // Inner interface (Inner class)
    @FunctionalInterface                  // Completely optional annotation
    private static interface Observable /*extends Map*/ {
        // NO FIELDS in an interface!

        void observe();             // The compiler will target this method for lambdas

        default void lookup() {
            observe();
            observe();
            //entrySet();
            // Convenience calls to other methods in the interface or ancestor interfaces
            // Other convenient logic that can be performed right here in the method!
        }

//        void something();  // Compiler complains of this
    }

    // Inner class
    private static class MyObservable implements Observable {
        @Override
        public void observe() {
        }
    }

    // Inner class
    private static class MyHandler implements ActionListener, MouseListener, KeyListener, WindowListener {

        @Override
        public void windowOpened(WindowEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void windowClosing(WindowEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void windowClosed(WindowEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void windowIconified(WindowEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void windowDeiconified(WindowEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void windowActivated(WindowEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void keyTyped(KeyEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void keyPressed(KeyEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void keyReleased(KeyEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseClicked(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mousePressed(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }
        @Override
        public void mouseExited(MouseEvent e) {
        }
        @Override
        public void actionPerformed(ActionEvent e) {
        }
    }
}
