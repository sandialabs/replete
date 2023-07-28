package learning;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;

public class Lambdas {

    public static void main(String[] args) {
        float $ = 223.0F;

//        List l = new ArrayList();
        Observable myObs = new MyObservable();

        Observable myObs2 = new Observable() {     // Anonymous inner classes
            @Override
            public void observe() {
                System.out.println("1");
            }
        };
        Observable myObs3 = new Observable() {     // Anonymous inner classes
            @Override
            public void observe() {
                System.out.println("2");
            }
        };

        Observable myObs4 = () -> {
            System.out.println("2");
        };

        Observable myObs5 = () -> System.out.println("2");

//        SomethingMadeUp here = i -> {
//            asdf
//            asfasdf
//            asfd
//            asdf
//            asdfas
//            dfasdf
//            asdf
//            asdf
//            asdfa
//            sdf
//            return i * 2;
//        };
//        SomethingMadeUp here2 = i -> i * 2;

        JButton btnRun = new JButton("Run");
        btnRun.addActionListener(handler);
        btnRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("the code");
            }
        });
        btnRun.addActionListener(event -> {
            System.out.println("the code");
        });
    }
    private static MyHandler handler = new MyHandler();

//    private static RETURN __secret_lambda_jvm_3432543(PARAMS) {
//        return i * 2;
//    }

    private static class MyHandler implements ActionListener, MouseListener, WindowListener {

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
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub

        }

    }

    Observable myObs3 = new Observable() {     // Anonymous inner classes
        @Override
        public void observe() {
            // TODO Auto-generated method stub

        }
    };

    private static class MyObservable implements Observable {   // Inner class
        @Override
        public void observe() {
        }
    }

    private static interface Observable {    // Inner interface
        void observe();
    }

}
